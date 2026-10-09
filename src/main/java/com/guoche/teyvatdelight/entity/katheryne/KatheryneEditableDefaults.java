package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/** Exposes parser defaults without replacing any explicitly configured value. */
final class KatheryneEditableDefaults {
  private KatheryneEditableDefaults() {}

  static JsonObject complete(String category, String resourceId, JsonObject source) {
    JsonObject value = source.deepCopy();
    boolean switchable = !category.equals("pools") && !category.equals("rewards");
    if (switchable) put(value, "enabled", true);
    if (switchable && !KatheryneDataPack.enabled(value)) return value;
    if (category.equals("commissions") || category.equals("shops")) put(value, "order", 1000);
    switch (category) {
      case "commissions" -> {
        put(value, "title", "");
        put(value, "description", "");
        put(value, "type", "objectives");
        put(value, "introduction", "");
        put(value, "previewBeforeUnlock", false);
        put(value, "icon", "");
        put(value, "reward", "");
        put(value, "repeatable", true);
        put(value, "important", false);
        array(value, "completionAdvancements");
        array(value, "completionRules");
        targets(value, "targets");
      }
      case "shops" -> {
        put(value, "title", "");
        put(value, "refreshTime", -1);
        array(value, "conditions");
        put(value, "lockedDisplay", "hide");
        if (value.has("offers"))
          for (var entry : value.getAsJsonArray("offers"))
            if (entry.isJsonObject()) offer(entry.getAsJsonObject());
      }
      case "offers" -> offer(value);
      case "pools" -> array(value, "exclude");
      case "rewards" -> {
        array(value, "items");
        array(value, "random");
        targets(value, "random");
      }
      default -> throw new IllegalArgumentException("Unknown editable category " + category);
    }
    return value;
  }

  private static void offer(JsonObject value) {
    put(value, "enabled", true);
    put(value, "type", "fixed");
    put(value, "name", "");
    array(value, "conditions");
    put(value, "lockedDisplay", "hide");
    put(value, "artifactStars", 0);
    boolean fixed = value.get("type").getAsString().trim().equals("fixed");
    put(value, "dailyLimit", fixed ? -1 : 1);
    if (!fixed) {
      put(value, "drawCount", 1);
      put(value, "count", 1);
      put(value, "repeat", false);
      put(value, "enchantEquipment", false);
    }
  }

  private static void targets(JsonObject value, String key) {
    if (!value.has(key) || !value.get(key).isJsonArray()) return;
    for (var element : value.getAsJsonArray(key)) {
      if (!element.isJsonObject()) continue;
      JsonObject target = element.getAsJsonObject();
      put(target, "type", "item");
      put(target, "count", 1);
      put(target, "drawCount", 1);
      put(target, "repeat", false);
      put(target, "name", "");
    }
  }

  private static void array(JsonObject value, String key) {
    if (!value.has(key)) value.add(key, new JsonArray());
  }

  private static void put(JsonObject value, String key, String fallback) {
    if (!value.has(key)) value.addProperty(key, fallback);
  }

  private static void put(JsonObject value, String key, int fallback) {
    if (!value.has(key)) value.addProperty(key, fallback);
  }

  private static void put(JsonObject value, String key, boolean fallback) {
    if (!value.has(key)) value.addProperty(key, fallback);
  }
}
