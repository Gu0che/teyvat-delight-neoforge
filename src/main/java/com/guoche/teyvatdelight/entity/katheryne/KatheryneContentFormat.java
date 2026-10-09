package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Editable content is compiled once into the existing server settlement definitions. */
final class KatheryneContentFormat {
  private KatheryneContentFormat() {}

  static String identity(String category, String resource, KatheryneDataPack.Catalog catalog) {
    return resource.startsWith("teyvatdelight:")
        ? resource.substring("teyvatdelight:".length()) : resource;
  }

  static JsonObject canonical(String category, JsonObject source, KatheryneDataPack.Catalog catalog) {
    JsonObject value = source.deepCopy();
    for (String key : List.of("_help", "_comment"))
      if (value.has(key)) throw new IllegalArgumentException("Use // comments, not " + key);
    if (!category.equals("offers") && value.has("id"))
      throw new IllegalArgumentException("Definition ID comes from its file path");
    if (category.equals("pools")) {
      if (value.has("enabled") || value.has("order"))
        throw new IllegalArgumentException("Pools are lists, not enabled/ordered entries");
      return value;
    }
    if (category.equals("rewards")) {
      if (value.has("enabled") || value.has("order"))
        throw new IllegalArgumentException("Rewards are referenced definitions; omit enabled/order");
      validateSources(value, "random");
      return value;
    }
    if (!KatheryneDataPack.enabled(value)) return value;
    if (Set.of("shops", "offers", "commissions").contains(category)
        && !CommissionConditions.possible(CommissionConditions.parse(value.get("conditions")))) return value;
    if (category.equals("shops")) {
      if (!value.has("offers") || !value.get("offers").isJsonArray())
        throw new IllegalArgumentException("Shop needs an offers array");
      JsonArray offers = new JsonArray();
      for (JsonElement entry : value.getAsJsonArray("offers")) {
        if (!entry.isJsonObject())
          throw new IllegalArgumentException("Write trade objects inside the shop, not file references");
        try { offers.add(canonical("offers", entry.getAsJsonObject(), catalog)); }
        catch (IllegalArgumentException error) {
          throw new IllegalArgumentException("Trade " + entry.getAsJsonObject().get("id") + ": " + error.getMessage(), error);
        }
      }
      value.add("offers", offers);
    } else if (category.equals("offers")) {
      if (value.has("pool")) throw new IllegalArgumentException("Random trade requires source");
    } else if (category.equals("commissions")) {
      validateSources(value, "targets");
      if (value.has("reward") && value.get("reward").isJsonObject())
        value.add("reward", canonical("rewards", value.getAsJsonObject("reward"), catalog));
    }
    return value;
  }

  private static void validateSources(JsonObject value, String field) {
    if (!value.has(field)) return;
    for (JsonElement element : value.getAsJsonArray(field)) {
      JsonObject target = element.getAsJsonObject();
      if (!target.has("source") || target.has("target") || target.has("pool"))
        throw new IllegalArgumentException("Target/random reward requires source only");
    }
  }

  static JsonObject compile(JsonObject controls, KatheryneDataPack.Catalog catalog) {
    JsonObject root = controls.deepCopy();
    JsonObject pools = new JsonObject();
    for (var entry : catalog.category("pools").entrySet()) {
      JsonObject definition = canonical("pools", entry.getValue(), catalog);
      pools.add(entry.getKey(), definition);
    }
    root.add("pools", pools);
    JsonObject commissions = root.getAsJsonObject("commissions");
    commissions.add("reward", compileReward(commissions.get("reward"), catalog, pools, "global"));
    JsonObject milestone = commissions.getAsJsonObject("milestone");
    milestone.add("reward", compileReward(milestone.get("reward"), catalog, pools, "milestone"));
    JsonArray templates = new JsonArray();
    for (var entry : KatheryneDataPack.ordered(catalog.category("commissions"))) {
      JsonObject template = checked("commissions", entry, catalog);
      if (!KatheryneDataPack.enabled(template)) continue;
      if (!CommissionConditions.possible(CommissionConditions.parse(template.get("conditions")))) continue;
      String id = identity("commissions", entry.getKey(), catalog);
      template.addProperty("id", id);
      compileTargets(template, "targets", pools, "commission/" + id);
      if (template.has("reward") && !(template.get("reward").isJsonPrimitive()
          && template.get("reward").getAsString().isEmpty()))
        template.add("reward", compileReward(template.get("reward"), catalog, pools, "reward/" + id));
      templates.add(template);
    }
    commissions.add("templates", templates);
    JsonArray shops = new JsonArray();
    for (var entry : KatheryneDataPack.ordered(catalog.category("shops"))) {
      JsonObject shop = checked("shops", entry, catalog);
      if (!KatheryneDataPack.enabled(shop)) continue;
      if (!CommissionConditions.possible(CommissionConditions.parse(shop.get("conditions")))) continue;
      String id = identity("shops", entry.getKey(), catalog);
      shop.addProperty("id", id);
      JsonArray offers = new JsonArray();
      for (JsonElement element : shop.getAsJsonArray("offers")) {
        JsonObject offer = element.getAsJsonObject();
        if (!KatheryneDataPack.enabled(offer)) continue;
        if (!CommissionConditions.possible(CommissionConditions.parse(offer.get("conditions")))) continue;
        if (offer.has("type") && offer.get("type").getAsString().trim().equals("random")) {
          String pool = compileSource(offer.get("source"), "item", pools, "shop/" + id + "/" + offer.get("id").getAsString());
          JsonArray selectors = new JsonArray();
          selectors.add("$" + pool);
          offer.add("pool", selectors);
          offer.remove("source");
        }
        offers.add(offer);
      }
      shop.add("offers", offers);
      shops.add(shop);
    }
    root.add("shops", shops);
    return root;
  }

  private static JsonObject checked(String category, Map.Entry<String, JsonObject> entry,
      KatheryneDataPack.Catalog catalog) {
    try { return canonical(category, entry.getValue(), catalog); }
    catch (IllegalArgumentException error) {
      throw new IllegalArgumentException("katheryne/" + category + "/" + entry.getKey()
          + ": " + error.getMessage(), error);
    }
  }

  private static JsonObject compileReward(JsonElement ref, KatheryneDataPack.Catalog catalog, JsonObject pools, String context) {
    if (ref == null) throw new IllegalArgumentException("Missing reward / 未填写奖励: " + context);
    JsonObject value = ref.isJsonObject() ? ref.getAsJsonObject() : catalog.category("rewards").get(ref.getAsString());
    if (value == null) throw new IllegalArgumentException("Unknown reward / 找不到奖励: " + ref);
    value = canonical("rewards", value, catalog);
    compileTargets(value, "random", pools, context);
    return value;
  }

  private static void compileTargets(JsonObject parent, String field, JsonObject pools, String context) {
    if (!parent.has(field)) return;
    int index = 0;
    for (JsonElement element : parent.getAsJsonArray(field)) {
      JsonObject target = element.getAsJsonObject();
      String type = target.has("type") ? target.get("type").getAsString().trim() : "item";
      JsonElement source = target.get("source");
      if (source == null) throw new IllegalArgumentException("Missing source / 未填写目标来源: " + context);
      String tag = "";
      if (source.isJsonPrimitive() && source.getAsJsonPrimitive().isString())
        tag = source.getAsString();
      else if (source.isJsonObject()) {
        var include = source.getAsJsonObject().getAsJsonArray("include");
        if (include != null && include.size() == 1) {
          var entry = include.get(0);
          if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) tag = entry.getAsString();
        }
      }
      if (tag.startsWith("#")) target.addProperty("tag", tag.substring(1));
      if (type.equals("stat") || type.equals("event")) {
        if (!source.isJsonPrimitive() || !source.getAsJsonPrimitive().isString())
          throw new IllegalArgumentException("Statistics/events need a source ID / 统计和事件只填写具体ID");
        target.add("target", source);
        target.addProperty("pool", "");
      } else {
        target.addProperty("pool", compileSource(source, type.equals("kill") ? "entity" : "item", pools, context + "/" + index));
        target.addProperty("target", "");
        var include = pools.getAsJsonObject(target.get("pool").getAsString()).getAsJsonArray("include");
        if (!target.has("tag") && include != null && include.size() == 1) {
          JsonElement candidate = include.get(0);
          if (candidate.isJsonArray()) candidate = candidate.getAsJsonArray().get(0);
          if (candidate.isJsonPrimitive() && candidate.getAsJsonPrimitive().isString()
              && candidate.getAsString().startsWith("#"))
            target.addProperty("tag", candidate.getAsString().substring(1));
        }
      }
      target.remove("source");
      index++;
    }
  }

  private static String compileSource(JsonElement source, String type, JsonObject pools, String context) {
    if (source == null) throw new IllegalArgumentException("Missing source / 未填写抽取来源: " + context);
    JsonObject definition;
    if (source.isJsonPrimitive() && source.getAsJsonPrimitive().isString()) {
      definition = new JsonObject();
      JsonArray include = new JsonArray();
      include.add(source.getAsString());
      definition.add("include", include);
    } else if (source.isJsonObject()) definition = source.getAsJsonObject().deepCopy();
    else throw new IllegalArgumentException("Invalid source / 抽取来源必须是ID或对象: " + context);
    for (String key : definition.keySet())
      if (!Set.of("pool", "include", "exclude").contains(key))
        throw new IllegalArgumentException("Unknown source field / 抽取来源字段拼写错误: " + key + " in " + context);
    if (definition.has("pool")) {
      if (definition.size() != 1) throw new IllegalArgumentException("Pool reference cannot also include/exclude / 引用公共池时不要再填include/exclude: " + context);
      String id = definition.get("pool").getAsString();
      JsonObject pool = pools.has(id) ? pools.getAsJsonObject(id) : null;
      if (pool == null || !type.equals(pool.has("type") ? pool.get("type").getAsString().trim() : "item"))
        throw new IllegalArgumentException("Missing/wrong pool / 抽取池不存在或物品/生物类型不符: " + id + " in " + context);
      return id;
    }
    if (!definition.has("include") || !definition.get("include").isJsonArray())
      throw new IllegalArgumentException("Source needs include / 抽取来源需要include列表: " + context);
    definition.addProperty("type", type);
    String id = "teyvatdelight:inline/" + UUID.nameUUIDFromBytes(context.getBytes(StandardCharsets.UTF_8));
    pools.add(id, definition);
    return id;
  }
}
