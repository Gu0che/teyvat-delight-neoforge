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

/** Dispatch prerequisites use lifetime history, never an accepted objective's progress. */
public final class CommissionConditions {
  public record Condition(String type, String id, String category, long min,
      List<Condition> children) {
    public boolean matches(ServerPlayer player, CommissionBook.State state) {
      return switch (type) {
        case "all" -> children.stream().allMatch(c -> c.matches(player, state));
        case "any" -> children.stream().anyMatch(c -> c.matches(player, state));
        case "commission" -> state.completedTemplates.contains(templateId(id));
        case "event" -> state.events.getOrDefault(id, 0L) >= min;
        case "stat" -> statistic(player, category, id) >= min;
        case "advancement" -> {
          var advancement = player.server.getAdvancements().get(ResourceLocation.tryParse(id));
          yield advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        default -> false;
      };
    }
  }

  private CommissionConditions() {}

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
      if (o.has("all") || o.has("any")) {
        String group = o.has("all") ? "all" : "any";
        if (o.size() != 1) throw invalid("a group contains only all or any");
        var children = parseArray(o.get(group), depth + 1, budget);
        if (children.isEmpty()) throw invalid("condition groups cannot be empty");
        result.add(new Condition(group, "", "", 1, children));
        continue;
      }
      String type = string(o, "type", ""), id = string(o, "id", "");
      if (!Set.of("advancement", "commission", "stat", "event").contains(type))
        throw invalid("unknown type " + type);
      if (ResourceLocation.tryParse(id) == null || id.length() > 256)
        throw invalid("invalid ID " + id);
      Set<String> allowed = type.equals("stat") ? Set.of("type", "id", "category", "min")
          : type.equals("event") ? Set.of("type", "id", "min") : Set.of("type", "id");
      if (!allowed.containsAll(o.keySet())) throw invalid("unknown fields for " + type);
      String category = string(o, "category", "minecraft:custom");
      if (type.equals("stat") && ResourceLocation.tryParse(category) == null)
        throw invalid("invalid statistic category");
      long min = 1;
      if (o.has("min")) {
        if (!o.get("min").isJsonPrimitive() || !o.get("min").getAsJsonPrimitive().isNumber())
          throw invalid("min must be a positive integer");
        try { min = o.get("min").getAsBigDecimal().longValueExact(); }
        catch (RuntimeException e) { throw invalid("min must be a positive integer"); }
        if (min < 1) throw invalid("min must be a positive integer");
      }
      result.add(new Condition(type, id, category, min, List.of()));
    }
    return List.copyOf(result);
  }

  public static void validate(List<Condition> conditions) {
    for (Condition c : conditions) {
      if (c.type.equals("all") || c.type.equals("any")) validate(c.children);
      else if (c.type.equals("stat")) {
        var category = ResourceLocation.tryParse(c.category);
        StatType<?> type = BuiltInRegistries.STAT_TYPE.get(category);
        if (!BuiltInRegistries.STAT_TYPE.containsKey(category) || type == null
            || !type.getRegistry().containsKey(ResourceLocation.tryParse(c.id)))
          throw invalid("unknown statistic " + c.category + "/" + c.id);
      }
    }
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
