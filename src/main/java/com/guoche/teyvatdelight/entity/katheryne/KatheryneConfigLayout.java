package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;

/** Presentation only: never changes IDs, configured values, or array order. */
final class KatheryneConfigLayout {
  private static final Gson COMPACT =
      new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

  private KatheryneConfigLayout() {}

  static String format(JsonObject root) throws IOException {
    StringWriter output = new StringWriter();
    try (JsonWriter writer = new JsonWriter(output)) {
      writer.setIndent("  ");
      writer.setHtmlSafe(false);
      writer.setSerializeNulls(true);
      write(writer, root, 0);
    }
    return output + System.lineSeparator();
  }

  private static void write(JsonWriter writer, JsonElement value, int depth) throws IOException {
    if (inline(value, depth)) {
      writer.jsonValue(COMPACT.toJson(value));
    } else if (value.isJsonObject()) {
      writer.beginObject();
      for (var entry : value.getAsJsonObject().entrySet()) {
        writer.name(entry.getKey());
        write(writer, entry.getValue(), depth + 1);
      }
      writer.endObject();
    } else {
      writer.beginArray();
      for (JsonElement element : value.getAsJsonArray()) write(writer, element, depth + 1);
      writer.endArray();
    }
  }

  private static boolean inline(JsonElement value, int depth) {
    if (value.isJsonNull() || value.isJsonPrimitive()) return true;
    if (value.isJsonObject()) {
      JsonObject object = value.getAsJsonObject();
      if (object.size() > 5) return false;
      for (var entry : object.entrySet())
        if (!entry.getValue().isJsonPrimitive() && !entry.getValue().isJsonNull()) return false;
    } else {
      for (JsonElement element : value.getAsJsonArray()) {
        if (element.isJsonArray()) return false;
        if (element.isJsonObject()) {
          for (var entry : element.getAsJsonObject().entrySet())
            if (!entry.getValue().isJsonPrimitive() && !entry.getValue().isJsonNull()) return false;
        }
      }
    }
    // Keep short item/count pairs and selectors together, but do not flatten whole sections.
    return COMPACT.toJson(value).length() + depth * 2 <= 120;
  }
}
