package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

/** Dispatch prerequisites use lifetime history, never an accepted objective's progress. */
public final class CommissionConditions {
  public record Condition(String type, String id, String category, long min, long max,
      List<Condition> children) {
    public Condition {
      children = List.copyOf(children);
    }
    public Condition(String type, String id, String category, long min, List<Condition> children) {
      this(type, id, category, min, -1, children);
    }
    public boolean matches(ServerPlayer player, CommissionBook.State state) {
      return evaluate(player, state) > 0;
    }

    // Unknown references remain unknown through NOT instead of granting access.
    private int evaluate(ServerPlayer player, CommissionBook.State state) {
      if (type.equals("not")) return -children.get(0).evaluate(player, state);
      if (type.equals("all") || type.equals("any")) {
        boolean unknown = false, all = type.equals("all");
        for (var child : children) {
          int value = child.evaluate(player, state);
          if (all && value < 0) return -1;
          if (!all && value > 0) return 1;
          unknown |= value == 0;
        }
        return unknown ? 0 : all ? 1 : -1;
      }
      if (type.equals("advancement")
          && player.server.getAdvancements().get(ResourceLocation.tryParse(id)) == null) {
        warnUnknown("advancement " + id);
        return 0;
      }
      return switch (type) {
        case "mod_loaded" -> ModList.get().isLoaded(id);
        case "commission" -> inRange(state == null ? 0 : Math.max(
            state.completedCounts.getOrDefault(templateId(id), 0L),
            state.completedTemplates.contains(templateId(id)) ? 1L : 0L));
        case "commission_total" -> inRange(state == null ? 0 : state.totalCompleted);
        case "trade_total" -> inRange(KatheryneData.get(player.server).stores.tradeCount(player));
        case "shop_trades" -> inRange(KatheryneData.get(player.server).stores.tradeCount(player, id));
        case "event" -> inRange(state == null ? 0 : state.events.getOrDefault(id, 0L));
        case "stat" -> inRange(statistic(player, category, id));
        case "advancement" -> {
          var advancement = player.server.getAdvancements().get(ResourceLocation.tryParse(id));
          yield advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        default -> false;
      } ? 1 : -1;
    }

    private boolean inRange(long value) {
      return value >= min && (max < 0 || value < max);
    }

    // Three-valued evaluation: player-dependent leaves must not be treated as false.
    public int staticMatch() {
      return switch (type) {
        case "mod_loaded" -> ModList.get().isLoaded(id) ? 1 : -1;
        case "not" -> -children.get(0).staticMatch();
        case "all" -> children.stream().anyMatch(c -> c.staticMatch() < 0) ? -1
            : children.stream().allMatch(c -> c.staticMatch() > 0) ? 1 : 0;
        case "any" -> children.stream().anyMatch(c -> c.staticMatch() > 0) ? 1
            : children.stream().allMatch(c -> c.staticMatch() < 0) ? -1 : 0;
        default -> 0;
      };
    }

    public Component description(ServerPlayer player) {
      String prefix = "gui.teyvatdelight.katheryne.condition.";
      if (Set.of("all", "any", "not").contains(type)) {
        var text = Component.translatable(prefix + type).append(" (");
        for (int i = 0; i < children.size(); i++) {
          if (i > 0) text.append("; ");
          text.append(children.get(i).description(player));
        }
        return text.append(")");
      }
      Component subject = Component.literal(id);
      if (type.equals("advancement")) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.tryParse(id));
        if (advancement != null && advancement.value().display().isPresent())
          subject = advancement.value().display().get().getTitle();
      }
      if (type.equals("stat")) subject = Component.literal(category + "/" + id);
      if (type.equals("commission")) {
        var template = CommissionConfig.settings().templates().stream()
            .filter(t -> t.id().equals(templateId(id))).findFirst().orElse(null);
        if (template != null && !template.title().isEmpty()) subject = Component.translatable(template.title());
      }
      if (type.equals("shop_trades")) {
        var shop = ShopDefinitions.get(id);
        if (shop != null && !shop.title().isEmpty()) subject = Component.translatable(shop.title());
      }
      var text = Component.translatable(prefix + type, subject);
      if (!type.equals("mod_loaded") && !type.equals("advancement")) {
        text.append(" ").append(Component.translatable(prefix + "min", min));
        if (max >= 0) text.append(" ").append(Component.translatable(prefix + "max", max));
      }
      return text;
    }
  }

  private CommissionConditions() {}

  private static long warningRevision = -1;
  private static final Set<String> warnedReferences = new java.util.HashSet<>();

  private static void warnUnknown(String reference) {
    if (warningRevision != CommissionConfig.revision()) {
      warnedReferences.clear();
      warningRevision = CommissionConfig.revision();
    }
    if (warnedReferences.size() < 1024 && warnedReferences.add(reference))
      com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn(
          "Katheryne condition references unknown {}; this condition is locked, including under NOT", reference);
  }

  public static boolean possible(List<Condition> conditions) {
    return conditions.stream().noneMatch(c -> c.staticMatch() < 0);
  }

  public static boolean matches(ServerPlayer player, List<Condition> conditions) {
    var state = KatheryneData.get(player.server).commissions.conditionState(player);
    return conditions.stream().allMatch(c -> c.matches(player, state));
  }

  public static Component description(ServerPlayer player, List<Condition> conditions) {
    var result = Component.empty();
    for (int i = 0; i < conditions.size(); i++) {
      if (i > 0) result.append("; ");
      result.append(conditions.get(i).description(player));
    }
    return result;
  }

  public static List<Condition> statistics(List<Condition> conditions) {
    List<Condition> result = new ArrayList<>();
    for (Condition c : conditions) {
      if (c.type.equals("stat")) result.add(c);
      result.addAll(statistics(c.children));
    }
    return result;
  }

  public static List<Condition> parse(JsonElement value) {
    if (value == null) return List.of();
    int[] budget = {64};
    return parseArray(value, 0, budget);
  }

  private static List<Condition> parseArray(JsonElement value, int depth, int[] budget) {
    if (!value.isJsonArray() || depth > 8) throw invalid("expected an array; maximum nesting is 8");
    List<Condition> result = new ArrayList<>();
    for (JsonElement element : value.getAsJsonArray()) {
      if (--budget[0] < 0 || !element.isJsonObject()) throw invalid("maximum 64 condition entries");
      JsonObject o = element.getAsJsonObject();
      if (o.has("not")) {
        if (o.size() != 1 || !o.get("not").isJsonObject()) throw invalid("not needs one condition object");
        var child = new com.google.gson.JsonArray();
        child.add(o.get("not"));
        result.add(new Condition("not", "", "", 1, parseArray(child, depth + 1, budget)));
        continue;
      }
      if (o.has("all") || o.has("any")) {
        String group = o.has("all") ? "all" : "any";
        if (o.size() != 1) throw invalid("a group contains only all or any");
        var children = parseArray(o.get(group), depth + 1, budget);
        if (children.isEmpty()) throw invalid("condition groups cannot be empty");
        result.add(new Condition(group, "", "", 1, children));
        continue;
      }
      String type = string(o, "type", ""), id = string(o, "id", "");
      if (!Set.of("mod_loaded", "advancement", "commission", "commission_total", "trade_total", "shop_trades", "stat", "event").contains(type))
        throw invalid("unknown type " + type);
      boolean total = type.equals("commission_total") || type.equals("trade_total");
      if (type.equals("mod_loaded") ? !id.matches("[a-z][a-z0-9_]{1,63}")
          : !total && (ResourceLocation.tryParse(id) == null || id.length() > 256))
        throw invalid("invalid ID " + id);
      Set<String> allowed = type.equals("stat") ? Set.of("type", "id", "category", "min", "max")
          : type.equals("advancement") || type.equals("mod_loaded") ? Set.of("type", "id")
          : total ? Set.of("type", "min", "max") : Set.of("type", "id", "min", "max");
      if (!allowed.containsAll(o.keySet())) throw invalid("unknown fields for " + type);
      String category = string(o, "category", "minecraft:custom");
      if (type.equals("stat") && ResourceLocation.tryParse(category) == null)
        throw invalid("invalid statistic category");
      long min = integer(o, "min", o.has("max") ? 0 : 1), max = integer(o, "max", -1);
      if (min < 0 || o.has("max") && max <= min) throw invalid("require 0 <= min < max (max is exclusive)");
      result.add(new Condition(type, id, category, min, max, List.of()));
    }
    return List.copyOf(result);
  }

  public static void validate(List<Condition> conditions) {
    for (Condition c : conditions) {
      if (c.staticMatch() < 0) continue;
      if (Set.of("all", "any", "not").contains(c.type)) validate(c.children);
      else if (c.type.equals("stat")) {
        var category = ResourceLocation.tryParse(c.category);
        StatType<?> type = BuiltInRegistries.STAT_TYPE.get(category);
        if (!BuiltInRegistries.STAT_TYPE.containsKey(category) || type == null
            || !type.getRegistry().containsKey(ResourceLocation.tryParse(c.id)))
          throw invalid("unknown statistic " + c.category + "/" + c.id);
      }
    }
  }

  private static long integer(JsonObject o, String field, long fallback) {
    if (!o.has(field)) return fallback;
    if (!o.get(field).isJsonPrimitive() || !o.get(field).getAsJsonPrimitive().isNumber())
      throw invalid(field + " must be an integer");
    try { return o.get(field).getAsBigDecimal().longValueExact(); }
    catch (RuntimeException e) { throw invalid(field + " must be a long integer"); }
  }

  public static long statistic(ServerPlayer player, String category, String id) {
    var key = ResourceLocation.tryParse(category);
    if (key == null || !BuiltInRegistries.STAT_TYPE.containsKey(key)) return 0;
    return statistic(player, BuiltInRegistries.STAT_TYPE.get(key), ResourceLocation.tryParse(id));
  }

  private static <T> long statistic(ServerPlayer player, StatType<T> type, ResourceLocation id) {
    if (type == null || id == null || !type.getRegistry().containsKey(id)) return 0;
    return player.getStats().getValue(type.get(type.getRegistry().get(id)));
  }

  public static String templateId(String id) {
    return id.startsWith("teyvatdelight:") ? id.substring("teyvatdelight:".length()) : id;
  }

  private static String string(JsonObject o, String key, String fallback) {
    if (!o.has(key)) return fallback;
    JsonElement value = o.get(key);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
      throw invalid(key + " must be text");
    return value.getAsString();
  }

  private static IllegalArgumentException invalid(String message) {
    return new IllegalArgumentException("Commission conditions: " + message);
  }
}
