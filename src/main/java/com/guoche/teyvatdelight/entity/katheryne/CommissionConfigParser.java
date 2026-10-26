package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Entry;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Pool;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Target;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Reward;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Template;
import com.guoche.teyvatdelight.entity.katheryne.CommissionConfig.Settings;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig.RawStack;
import java.util.*;
import net.minecraft.resources.ResourceLocation;

/** Parses and validates the commission definition document without owning runtime state. */
final class CommissionConfigParser {
  private CommissionConfigParser() {}

  public static CommissionConfig.Settings parse(JsonObject root) {
    JsonObject q = root.getAsJsonObject("commissions");
    Map<String, CommissionConfig.Pool> pools = new LinkedHashMap<>();
    JsonObject definitions = root.getAsJsonObject("pools");
    if (definitions == null || definitions.size() > 1024) fail("pools");
    for (var field : definitions.entrySet()) {
      id(field.getKey());
      JsonObject p = field.getValue().getAsJsonObject();
      String type = text(p, "type", "item", 128);
      if (!type.equals("item") && !type.equals("entity")) fail("pool type: " + field.getKey());
      List<CommissionConfig.Entry> entries = new ArrayList<>();
      JsonArray include = p.getAsJsonArray("include");
      if (include == null || include.size() > 4096) fail("pool include");
      for (JsonElement e : include) {
        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
          entries.add(new CommissionConfig.Entry(e.getAsString(), 1));
        else if (e.isJsonArray()) {
          JsonArray pair = e.getAsJsonArray();
          if (pair.size() != 2 || !pair.get(0).isJsonPrimitive()
              || !pair.get(0).getAsJsonPrimitive().isString())
            fail("weighted candidate must be [\"ID or #tag\", weight]");
          entries.add(new CommissionConfig.Entry(pair.get(0).getAsString(), weight(pair.get(1))));
        } else fail("candidate must be an ID, #tag or [\"ID or #tag\", weight]");
      }
      List<String> exclude = new ArrayList<>();
      if (p.has("exclude"))
        for (JsonElement e : p.getAsJsonArray("exclude")) {
          if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString())
            fail("pool exclusions must be strings");
          exclude.add(e.getAsString());
        }
      if (exclude.size() > 4096) fail("pool exclusions");
      pools.put(field.getKey(), new CommissionConfig.Pool(type, List.copyOf(entries), List.copyOf(exclude)));
    }
    if (q == null) fail("commissions");
    int limit = number(q, "limit", 4, 1, CommissionConfig.MAX_QUESTS), count = number(q, "dailyCount", 1, 1, limit);
    CommissionConfig.Reward rewards = reward(q.getAsJsonObject("reward"));
    JsonObject milestone = q.getAsJsonObject("milestone");
    if (milestone == null) fail("milestone");
    int every = number(milestone, "every", 4, 1, 1000000);
    CommissionConfig.Reward bonus = reward(milestone.getAsJsonObject("reward"));
    List<CommissionConfig.Template> templates = new ArrayList<>();
    Set<String> ids = new HashSet<>();
    JsonArray list = q.getAsJsonArray("templates");
    if (list == null || list.size() > 1024) fail("templates");
    for (JsonElement element : list) {
      JsonObject t = element.getAsJsonObject();
      String key = text(t, "id", "", 128);
      id(key);
      if (!ids.add(key)) fail("duplicate template " + key);
      String icon = text(t, "icon", "", 256);
      if (!icon.isEmpty() && ResourceLocation.tryParse(icon) == null) fail("icon");
      String type = text(t, "type", "objectives", 16);
      if (!Set.of("objectives", "view").contains(type)) fail("commission type " + type);
      List<CommissionConfig.Target> goals = new ArrayList<>();
      if (!type.equals("view") || t.has("targets") && !t.getAsJsonArray("targets").isEmpty())
        goals.addAll(targets(t.getAsJsonArray("targets")));
      if (type.equals("view"))
        goals.add(new CommissionConfig.Target("view", "teyvatdelight:view_information", "", 1, 1, false));
      templates.add(
          new CommissionConfig.Template(
              key,
              text(t, "title", "", 256),
              text(t, "description", "", CommissionConfig.MAX_TEXT),
              icon,
              weight(t),
              List.copyOf(goals),
              !t.has("reward") || t.get("reward").isJsonPrimitive()
                  && t.get("reward").getAsJsonPrimitive().isString()
                  && t.get("reward").getAsString().isEmpty()
                  ? null : reward(t.getAsJsonObject("reward")),
              !t.has("repeatable") || booleanValue(t.get("repeatable")),
              t.has("important") && booleanValue(t.get("important")),
              advancementIds(t),
              t.has("urgent") && booleanValue(t.get("urgent")),
              CommissionConditions.parse(t.get("conditions")),
              CommissionCompletionRule.parse(t.get("completionRules")),
              text(t, "introduction", "", 8192),
              t.has("previewBeforeUnlock") && booleanValue(t.get("previewBeforeUnlock"))));
    }
    String selection = text(q, "selection", "random", 16);
    if (!Set.of("random", "rotation").contains(selection))
      fail("selection must be random or rotation");
    CommissionConfig.Settings result =
        new CommissionConfig.Settings(
            number(q, "refreshTime", 22000, 0, 23999),
            number(q, "durationTicks", -1, -1, 8760000),
            count,
            limit,
            selection.equals("rotation"),
            rewards,
            every,
            bonus,
            Map.copyOf(pools),
            List.copyOf(templates));
    for (CommissionConfig.Template t : templates) {
      validateTargets(t.targets(), pools);
      Set<String> allAdvancements = new HashSet<>(t.completionAdvancements());
      for (var rule : t.completionRules()) allAdvancements.addAll(rule.advancements());
      if (allAdvancements.size() > 64) fail("combined completion advancements exceed 64 IDs");
    }
    for (CommissionConfig.Template t : templates)
      if (t.reward() != null) validateTargets(t.reward().random(), pools);
    validateTargets(rewards.random(), pools);
    validateTargets(bonus.random(), pools);
    for (CommissionConfig.Pool p : pools.values())
      for (CommissionConfig.Entry e : p.include()) {
        if (e.selector().startsWith("$"))
          fail("nested pool references are not supported; reference pools at consumers");
        selector(p.type(), e.selector());
      }
    for (CommissionConfig.Pool p : pools.values()) for (String e : p.exclude()) selector(p.type(), e);
    int perQuest = 0;
    for (CommissionConfig.Template t : templates) {
      CommissionConfig.Reward r = t.reward() == null ? rewards : t.reward();
      int rewardSize =
          new Gson().toJson(r.fixed()).length()
              + r.random().stream().mapToInt(v -> v.draws() * 320).sum();
      int size =
          new Gson().toJson(t.title()).length()
              + new Gson().toJson(t.description()).length()
              + new Gson().toJson(t.introduction()).length()
              + t.icon().length()
              + 320
              + t.targets().stream().mapToInt(v -> v.draws() * 352).sum()
              + rewardSize;
      perQuest = Math.max(perQuest, size);
    }
    if ((long) perQuest + new Gson().toJson(bonus.fixed()).length()
        + bonus.random().stream().mapToInt(v -> v.draws() * 320).sum() > 180000)
      fail("a commission exceeds safe page size; reduce objectives, text or rewards");
    return result;
  }

  private static List<String> advancementIds(JsonObject value) {
    if (!value.has("completionAdvancements")) return List.of();
    JsonArray ids = value.getAsJsonArray("completionAdvancements");
    if (ids == null || ids.size() > 64) fail("completionAdvancements must contain at most 64 IDs");
    LinkedHashSet<String> result = new LinkedHashSet<>();
    for (JsonElement id : ids) {
      if (!id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString()
          || id.getAsString().length() > 256
          || ResourceLocation.tryParse(id.getAsString()) == null)
        fail("completion advancement ID");
      result.add(id.getAsString());
    }
    return List.copyOf(result);
  }

  private static void validateTargets(List<CommissionConfig.Target> list, Map<String, CommissionConfig.Pool> pools) {
    for (CommissionConfig.Target t : list) {
      if (!Set.of("item", "kill", "stat", "event", "view").contains(t.type()))
        fail("target type " + t.type());
      if (!Set.of("random", "any").contains(t.selection()))
        fail("target selection must be random or any");
      if (t.any() && (!t.type().equals("item") || t.draws() != 1 || t.repeat()))
        fail("any selection requires an item target, drawCount 1 and repeat false");
      if (!t.target().isEmpty() && !t.pool().isEmpty())
        fail("target and pool are mutually exclusive");
      if (!t.pool().isEmpty()) {
        CommissionConfig.Pool p = pools.get(t.pool());
        if (p == null
            || !p.type().equals(t.type().equals("kill") ? "entity" : "item")
            || t.type().equals("stat")
            || t.type().equals("event")) fail("pool reference " + t.pool());
      } else if (ResourceLocation.tryParse(t.target().replaceFirst("^#", "")) == null)
        fail("target " + t.target());
    }
  }

  private static List<CommissionConfig.Target> targets(JsonArray array) {
    return targets(array, true);
  }

  private static List<CommissionConfig.Target> targets(JsonArray array, boolean submission) {
    if (array == null || array.isEmpty() || array.size() > CommissionConfig.MAX_TARGETS)
      fail("targets must contain 1..16 entries");
    List<CommissionConfig.Target> list = new ArrayList<>();
    for (JsonElement e : array) {
      JsonObject t = e.getAsJsonObject();
      if (text(t, "type", "item", 16).equals("view"))
        fail("Use commission type view; its final viewing step is added automatically");
      list.add(
          new CommissionConfig.Target(
              text(t, "type", "item", 16),
              text(t, "target", "", 256),
              text(t, "pool", "", 128),
              number(t, "count", 1, 1, 1000000),
              number(t, "drawCount", 1, 1, CommissionConfig.MAX_TARGETS),
              t.has("repeat") && booleanValue(t.get("repeat")),
              text(t, "selection", "random", 16),
              text(t, "name", "", 256),
              text(t, "tag", "", 256)));
    }
    if (list.stream().mapToInt(CommissionConfig.Target::draws).sum() > CommissionConfig.MAX_TARGETS) fail("too many rolled targets");
    return List.copyOf(list);
  }

  private static CommissionConfig.Reward reward(JsonObject r) {
    if (r == null) fail("reward must be an object; use {} for no reward");
    List<RawStack> fixed = new ArrayList<>();
    if (r.has("items"))
      for (JsonElement e : r.getAsJsonArray("items")) {
        JsonObject stack = e.getAsJsonObject();
        String item = text(stack, "item", "", 256);
        if (ResourceLocation.tryParse(item) == null) fail("reward item");
        fixed.add(new RawStack(item, number(stack, "count", 1, 1, 1000000)));
      }
    if (fixed.size() > 128) fail("too many rewards");
    return new CommissionConfig.Reward(
        List.copyOf(fixed), r.has("random") && !r.getAsJsonArray("random").isEmpty()
            ? targets(r.getAsJsonArray("random"), false) : List.of());
  }

  private static void selector(String type, String text) {
    if (text.length() > 256) fail("selector too long");
    if (text.equals("@merchant_ingredients") && type.equals("item")) return;
    if (ResourceLocation.tryParse(text.replaceFirst("^#", "")) == null) fail("selector " + text);
  }

  public static JsonObject defaultRoot() {
    JsonObject root = KatheryneDataPack.defaultsRoot();
    root.keySet().removeIf(key -> !key.equals("commissions") && !key.equals("pools"));
    stripNotes(root);
    for (var entry : root.getAsJsonObject("pools").entrySet())
      entry.getValue().getAsJsonObject().remove("id");
    return root;
  }

  private static void stripNotes(JsonElement element) {
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      object.remove("_comment");
      object.remove("_help");
      object.remove("order");
      object.entrySet().forEach(entry -> stripNotes(entry.getValue()));
    } else if (element.isJsonArray()) {
      for (JsonElement child : element.getAsJsonArray()) stripNotes(child);
    }
  }

  public static int number(JsonObject o, String k, int fallback, int min, int max) {
    if (!o.has(k)) return fallback;
    if (!o.get(k).isJsonPrimitive() || !o.get(k).getAsJsonPrimitive().isNumber())
      fail(k + " must be a number");
    int value = new java.math.BigDecimal(o.get(k).getAsString()).intValueExact();
    if (value < min || value > max) fail(k + " must be " + min + ".." + max);
    return value;
  }

  private static String text(JsonObject o, String k, String fallback, int max) {
    if (!o.has(k)) return fallback;
    if (!o.get(k).isJsonPrimitive() || !o.get(k).getAsJsonPrimitive().isString()) fail(k);
    String text = o.get(k).getAsString().trim();
    if (text.length() > max) fail(k + " too long");
    return text;
  }

  private static double weight(JsonObject o) {
    return o.has("weight") ? weight(o.get("weight")) : 1;
  }

  private static double weight(JsonElement value) {
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
      fail("weight must be a number");
    double w = value.getAsDouble();
    if (!Double.isFinite(w) || w <= 0 || w > 1000000) fail("weight");
    return w;
  }

  private static void id(String id) {
    if (!id.matches("[a-z0-9_:/.-]{1,128}")) fail("id " + id);
  }

  private static boolean booleanValue(JsonElement value) {
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean())
      fail("Expected a JSON boolean");
    return value.getAsBoolean();
  }

  private static void fail(String detail) {
    throw new IllegalArgumentException("Katheryne configuration: " + detail);
  }
}
