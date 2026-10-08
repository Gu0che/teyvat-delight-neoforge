package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Renders guidance beside fields; Gson remains responsible for the actual JSON values. */
final class KatheryneConfigComments {
  private static final Gson JSON =
      new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

  private KatheryneConfigComments() {}

  static String format(JsonObject value, String existing, JsonObject previous) {
    String generated = render(value);
    Set<String> managed = new LinkedHashSet<>(comments(generated));
    if (previous != null) managed.addAll(comments(render(previous)));
    Set<String> notes = new LinkedHashSet<>(comments(existing));
    notes.removeAll(managed);
    StringBuilder result = new StringBuilder();
    for (String note : notes) line(result, 0, note);
    result.append(generated);
    return result.toString();
  }

  private static String render(JsonObject value) {
    StringBuilder output = new StringBuilder();
    write(output, value, 0);
    return output.append('\n').toString();
  }

  private static void write(StringBuilder out, JsonElement value, int depth) {
    write(out, value, depth, new JsonObject());
  }

  private static void write(StringBuilder out, JsonElement value, int depth, JsonObject selectorHelp) {
    if (value.isJsonObject()) {
      JsonObject object = value.getAsJsonObject();
      if (object.size() == 2 && object.has("item") && object.has("count")) {
        out.append(JSON.toJson(object));
        return;
      }
      JsonObject help = new JsonObject();
      KatheryneGuidance.forObject(object).entrySet().forEach(entry -> {
        if (object.has(entry.getKey())) help.add(entry.getKey(), entry.getValue());
      });
      selectorHelp.entrySet().forEach(entry -> {
        if (!help.has(entry.getKey())) help.add(entry.getKey(), entry.getValue());
      });
      var fields = object.entrySet().stream().toList();
      out.append("{");
      if (!fields.isEmpty() || help.size() > 0) out.append('\n');
      for (int i = 0; i < fields.size(); i++) {
        var field = fields.get(i);
        if (help.has(field.getKey())) note(out, depth + 1, help.get(field.getKey()));
        out.append("  ".repeat(depth + 1)).append(JSON.toJson(field.getKey())).append(": ");
        JsonObject childHelp = new JsonObject();
        if (field.getKey().equals("targets") || field.getKey().equals("random"))
          for (String key : List.of("type", "source", "drawCount", "count", "repeat"))
            if (help.has(key)) childHelp.add(key, help.get(key));
        write(out, field.getValue(), depth + 1, childHelp);
        if (i + 1 < fields.size()) out.append(',');
        out.append('\n');
        if (i + 1 < fields.size()) out.append('\n');
      }
      if (!fields.isEmpty() || help.size() > 0) out.append("  ".repeat(depth));
      out.append('}');
    } else if (value.isJsonArray()) {
      String compact = JSON.toJson(value);
      boolean simple = true;
      for (JsonElement item : value.getAsJsonArray())
        if (!item.isJsonPrimitive()) simple = false;
      if (simple && compact.length() + depth * 2 <= 120) {
        out.append(compact);
      } else {
        out.append("[\n");
        var array = value.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          out.append("  ".repeat(depth + 1));
          write(out, array.get(i), depth + 1, selectorHelp);
          if (i + 1 < array.size()) out.append(',');
          out.append('\n');
        }
        out.append("  ".repeat(depth)).append(']');
      }
    } else {
      out.append(JSON.toJson(value));
    }
  }

  private static void note(StringBuilder out, int depth, JsonElement value) {
    String text = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()
        ? value.getAsString() : JSON.toJson(value);
    // Existing guides separate Chinese and English with " / " or "/ ".
    for (String language : text.split("\\s*/\\s+(?=[A-Za-z0-9{}_#@$-])"))
      for (String part : language.split("\\R")) line(out, depth, part);
  }

  private static void line(StringBuilder out, int depth, String text) {
    out.append("  ".repeat(depth)).append("// ").append(text.strip()).append('\n');
  }

  /** Comment-only lexer. Strings and escapes are skipped; no configured values are parsed here. */
  static List<String> comments(String text) {
    List<String> result = new ArrayList<>();
    char quote = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (quote != 0) {
        if (c == '\\') i++;
        else if (c == quote) quote = 0;
        continue;
      }
      if (c == '"' || c == '\'') { quote = c; continue; }
      if (c == '#' || (c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '/')) {
        int start = i + (c == '#' ? 1 : 2);
        int end = start;
        while (end < text.length() && text.charAt(end) != '\n' && text.charAt(end) != '\r') end++;
        result.add(text.substring(start, end).strip());
        i = end - 1;
      } else if (c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
        int end = text.indexOf("*/", i + 2);
        if (end < 0) break;
        for (String part : text.substring(i + 2, end).split("\\R"))
          result.add(part.strip());
        i = end + 1;
      }
    }
    return result;
  }
}
