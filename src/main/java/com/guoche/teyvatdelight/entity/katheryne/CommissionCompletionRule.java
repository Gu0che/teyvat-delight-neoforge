package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Conditions inspect only items consumed by the validated submission plan. */
public record CommissionCompletionRule(String item, String name,
    List<String> advancements, boolean stopRepeating) {
  static List<CommissionCompletionRule> parse(JsonElement value) {
    if (value == null) return List.of();
    if (!value.isJsonArray() || value.getAsJsonArray().size() > 16)
      throw new IllegalArgumentException("completionRules must be an array with at most 16 rules");
    List<CommissionCompletionRule> result = new ArrayList<>();
    for (JsonElement element : value.getAsJsonArray()) {
      JsonObject rule = element.getAsJsonObject();
      if (!Set.of("submittedItem", "advancements", "stopRepeating").containsAll(rule.keySet()))
        throw new IllegalArgumentException("Unknown completionRules field");
      JsonObject submitted = rule.getAsJsonObject("submittedItem");
      if (submitted == null || !Set.of("item", "name").containsAll(submitted.keySet()))
        throw new IllegalArgumentException("submittedItem needs item and optional name");
      String item = string(submitted, "item", "");
      String name = string(submitted, "name", "");
      if (ResourceLocation.tryParse(item) == null || name.length() > 256)
        throw new IllegalArgumentException("Invalid submitted item or name");
      LinkedHashSet<String> advancements = new LinkedHashSet<>();
      if (rule.has("advancements")) {
        JsonArray ids = rule.getAsJsonArray("advancements");
        if (ids.size() > 64) throw new IllegalArgumentException("Too many completion advancements");
        for (JsonElement id : ids) {
          if (!id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString()
              || id.getAsString().length() > 256 || ResourceLocation.tryParse(id.getAsString()) == null)
            throw new IllegalArgumentException("Invalid completion advancement ID");
          advancements.add(id.getAsString());
        }
      }
      boolean stop = false;
      if (rule.has("stopRepeating")) {
        JsonElement flag = rule.get("stopRepeating");
        if (!flag.isJsonPrimitive() || !flag.getAsJsonPrimitive().isBoolean())
          throw new IllegalArgumentException("stopRepeating must be a boolean");
        stop = flag.getAsBoolean();
      }
      result.add(new CommissionCompletionRule(item, name, List.copyOf(advancements), stop));
    }
    return List.copyOf(result);
  }

  private static String string(JsonObject object, String key, String fallback) {
    if (!object.has(key)) return fallback;
    JsonElement value = object.get(key);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
      throw new IllegalArgumentException(key + " must be a string");
    return value.getAsString();
  }

  boolean matches(ItemStack stack) {
    return !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(item)
        && (name.isEmpty() || stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME) != null
            && stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME).getString().equals(name));
  }
}
