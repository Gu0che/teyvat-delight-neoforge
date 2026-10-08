package com.guoche.teyvatdelight.client.config;

import com.google.gson.*;
import java.util.*;
import java.util.function.Consumer;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneConfigEditor;

/** Cloth API stays behind the optional client entrypoint. */
final class ClothFields {
  private final ConfigBuilder builder;
  private final ConfigCategory category;
  private final ConfigEntryBuilder entries;

  ClothFields(Screen parent, Component title) {
    builder = ConfigBuilder.create().setParentScreen(parent).setTitle(title);
    entries = builder.entryBuilder();
    category = builder.getOrCreateCategory(title);
  }

  Screen build() { return builder.build(); }

  void integer(JsonObject object, String field, int fallback, int min, int max) {
    integer(object, field, fallback, min, max, field);
  }

  void integer(JsonObject object, String field, int fallback, int min, int max, String label) {
    category.addEntry(entries.startIntField(t(label), number(object, field, fallback))
        .setMin(min).setMax(max).setDefaultValue(fallback).setTooltip(t(label + ".tip"))
        .setSaveConsumer(v -> object.addProperty(field, v)).build());
  }

  void decimal(JsonObject object, String field, double fallback, double min, double max) {
    category.addEntry(entries.startDoubleField(t(field),
        object.has(field) ? object.get(field).getAsDouble() : fallback)
        .setMin(min).setMax(max).setDefaultValue(fallback).setTooltip(t(field + ".tip"))
        .setSaveConsumer(v -> object.addProperty(field, v)).build());
  }

  void bool(JsonObject object, String field, boolean fallback) {
    category.addEntry(entries.startBooleanToggle(t(field),
        object.has(field) ? object.get(field).getAsBoolean() : fallback)
        .setDefaultValue(fallback).setTooltip(t(field + ".tip"))
        .setSaveConsumer(v -> object.addProperty(field, v)).build());
  }

  void toggle(String field, boolean value, boolean fallback, Consumer<Boolean> save) {
    category.addEntry(entries.startBooleanToggle(t(field), value).setDefaultValue(fallback)
        .setTooltip(t(field + ".tip")).setSaveConsumer(save).build());
  }

  void string(JsonObject object, String field, String fallback, int max, boolean item) {
    category.addEntry(entries.startStrField(t(field.equals("title") ? "titleField" : field),
            string(object, field, fallback))
        .setDefaultValue(fallback).setTooltip(t(field + ".tip"))
        .setErrorSupplier(v -> v.length() > max || item && !KatheryneConfigEditor.validItem(v)
            ? Optional.of(t(item ? "invalid_item" : "invalid_text")) : Optional.empty())
        .setSaveConsumer(v -> object.addProperty(field, v)).build());
  }

  void choice(JsonObject object, String field, String fallback, String... options) {
    category.addEntry(entries.startSelector(t(field), options, string(object, field, fallback))
        .setNameProvider(v -> t("value." + v)).setDefaultValue(fallback)
        .setTooltip(t(field + ".tip")).setSaveConsumer(v -> object.addProperty(field, v)).build());
  }

  void strings(JsonObject object, String field, List<String> fallback, int max, boolean items) {
    List<String> value = object.has(field) ? object.getAsJsonArray(field).asList().stream()
        .map(JsonElement::getAsString).toList() : fallback;
    category.addEntry(entries.startStrList(t(field), value).setDefaultValue(fallback)
        .setExpanded(true).setTooltip(t(field + ".tip"))
        .setCellErrorSupplier(v -> items && !KatheryneConfigEditor.validItem(v)
            ? Optional.of(t("invalid_item")) : Optional.empty())
        .setErrorSupplier(v -> v.size() > max || new HashSet<>(v).size() != v.size()
            ? Optional.of(t("invalid_list")) : Optional.empty())
        .setSaveConsumer(v -> { JsonArray a = new JsonArray(); v.forEach(a::add); object.add(field, a); })
        .build());
  }

  void literal(String key, String value, Consumer<String> save, boolean selector) {
    category.addEntry(entries.startStrField(t(key), value).setDefaultValue("")
        .setTooltip(t(key + ".tip"))
        .setErrorSupplier(v -> v.length() > 256 || selector && !validSelector(v)
            ? Optional.of(t("invalid_source")) : Optional.empty())
        .setSaveConsumer(save).build());
  }

  static boolean validSelector(String value) {
    if (value.startsWith("#"))
      return net.minecraft.resources.ResourceLocation.tryParse(value.substring(1)) != null;
    return KatheryneConfigEditor.validItem(value);
  }

  static Component t(String key) { return KatheryneEditorScreens.text(key); }
  static int number(JsonObject o, String key, int fallback) {
    return o.has(key) ? o.get(key).getAsInt() : fallback;
  }
  static String string(JsonObject o, String key, String fallback) {
    return o.has(key) ? o.get(key).getAsString() : fallback;
  }
}
