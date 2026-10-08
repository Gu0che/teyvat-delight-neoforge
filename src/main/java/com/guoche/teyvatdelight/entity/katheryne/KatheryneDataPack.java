package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** Definition assembly only. Player records remain in CommissionBook and ShopBook. */
public final class KatheryneDataPack {
  public static final String PACK_DIRECTORY = "teyvatdelight-katheryne-data";
  static final String PREFIX = "katheryne/";
  static final List<String> CATEGORIES =
      List.of("pools", "rewards", "commissions", "shops");
  private static Catalog bundled;
  private static Catalog active;
  private static Catalog pending;
  private static Path activeDirectory;

  record Catalog(Map<String, Map<String, JsonObject>> entries) {
    Map<String, JsonObject> category(String name) {
      return entries.getOrDefault(name, Map.of());
    }
  }

  private KatheryneDataPack() {}

  /** Export only bundled examples, never the active player state or user overrides. */
  public static Path exportExamples() throws IOException {
    Path destination = KatheryneShopConfig.directory().resolve("teyvatdelight-katheryne-example");
    if (Files.exists(destination))
      throw new IllegalArgumentException("Example directory already exists / 示例目录已存在: " + destination);
    KatheryneDataFiles.ensurePack(destination);
    writeBundled(destination, false);
    writeBundled(destination, true);
    return destination;
  }

  static boolean isExample(String resourceId) {
    return location(resourceId).getPath().startsWith("example_");
  }

  static void writeBundled(Path destination, boolean examples) throws IOException {
    for (String category : CATEGORIES)
      for (var entry : bundled().category(category).entrySet()) {
        if (isExample(entry.getKey()) != examples) continue;
        Path file = KatheryneDataFiles.resourcePath(destination, category, entry.getKey());
        if (Files.exists(file)) continue;
        if (examples) {
          String resource = fileName(category, location(entry.getKey()));
          try (var stream = KatheryneDataPack.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) throw new IOException("Missing bundled example: " + resource);
            Files.createDirectories(file.getParent());
            KatheryneShopConfig.writeAtomically(file,
                new String(stream.readAllBytes(), StandardCharsets.UTF_8));
          }
        } else KatheryneDataFiles.save(file, entry.getValue());
      }
  }

  static JsonObject defaultControls() {
    return bundledFile("data/teyvatdelight/katheryne/settings.json");
  }

  static Catalog bundled() {
    if (bundled == null) {
      JsonObject index = bundledFile("data/teyvatdelight/katheryne/catalog.json");
      Map<String, Map<String, JsonObject>> result = new LinkedHashMap<>();
      for (String category : CATEGORIES) {
        Map<String, JsonObject> values = new LinkedHashMap<>();
        for (JsonElement element : index.getAsJsonArray(category)) {
          ResourceLocation id = location(element.getAsString());
          values.put(id.toString(), KatheryneEditableDefaults.complete(
              category, id.toString(), bundledFile(fileName(category, id))));
        }
        result.put(category, Map.copyOf(values));
      }
      bundled = new Catalog(Map.copyOf(result));
    }
    return bundled;
  }

  static JsonObject defaultsRoot() {
    return assemble(defaultControls(), bundled());
  }

  static Catalog configured(Path directory) throws IOException {
    if (active != null && directory.equals(activeDirectory)) return active;
    return local(bundled(), directory.resolve(PACK_DIRECTORY));
  }

  static Catalog local(Catalog base, Path pack) throws IOException {
    Map<String, Map<String, JsonObject>> result = copy(base);
    Path data = pack.resolve("data");
    if (Files.isDirectory(data)) {
      try (var files = Files.walk(data)) {
        for (Path file : files.filter(Files::isRegularFile).sorted().toList()) {
          Path relative = data.relativize(file);
          if (relative.getNameCount() < 4
              || !relative.getName(1).toString().equals("katheryne")
              || !file.getFileName().toString().endsWith(".json")) continue;
          String category = relative.getName(2).toString();
          if (category.equals("offers"))
            throw new IllegalArgumentException("Trade rows belong in shops/*.json: " + file);
          if (!CATEGORIES.contains(category)) continue;
          String path = relative.subpath(3, relative.getNameCount()).toString().replace('\\', '/');
          String key = location(relative.getName(0) + ":" + path.substring(0, path.length() - 5))
              .toString();
          try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            result.get(category).put(key, read(reader, file.toString()));
          }
        }
      }
    }
    return new Catalog(result);
  }

  static Catalog resources(ResourceManager manager) throws IOException {
    if (!manager.listResources(PREFIX + "offers", id -> id.getPath().endsWith(".json")).isEmpty())
      throw new IllegalArgumentException("Trade rows belong in shops/*.json");
    Map<String, Map<String, JsonObject>> result = new LinkedHashMap<>();
    for (String category : CATEGORIES) {
      String prefix = PREFIX + category + "/";
      Map<String, JsonObject> values = new LinkedHashMap<>();
      for (var entry : manager.listResources(PREFIX + category, id -> id.getPath().endsWith(".json"))
          .entrySet()) {
        ResourceLocation file = entry.getKey();
        String path = file.getPath();
        String key = file.getNamespace() + ":" + path.substring(prefix.length(), path.length() - 5);
        try (Reader reader = entry.getValue().openAsReader()) {
          values.put(key, read(reader, file.toString()));
        }
        if (values.size() > 16384) throw new IllegalArgumentException("Too many " + category);
      }
      result.put(category, values);
    }
    return new Catalog(result);
  }

  static void stage(Catalog candidate) {
    pending = candidate;
  }

  static void applyPending() {
    if (pending == null) return;
    Catalog candidate = pending;
    pending = null;
    Path directory = KatheryneShopConfig.directory();
    if (KatheryneShopConfig.loadDataPack(candidate)) {
      active = candidate;
      activeDirectory = directory;
    }
  }

  static void stopped() {
    pending = null;
    active = null;
    activeDirectory = null;
  }

  static JsonObject assemble(JsonObject controls, Catalog catalog) {
    return KatheryneContentFormat.compile(controls, catalog);
  }

  static boolean enabled(JsonObject value) {
    if (!value.has("enabled")) return true;
    JsonElement enabled = value.get("enabled");
    if (!enabled.isJsonPrimitive() || !enabled.getAsJsonPrimitive().isBoolean())
      throw new IllegalArgumentException("enabled must be a boolean");
    return enabled.getAsBoolean();
  }

  static List<Map.Entry<String, JsonObject>> ordered(Map<String, JsonObject> values) {
    List<Map.Entry<String, JsonObject>> result = new ArrayList<>(values.entrySet());
    result.sort(Comparator
        .comparingInt((Map.Entry<String, JsonObject> entry) ->
            CommissionConfig.number(entry.getValue(), "order", 1000, -1000000, 1000000))
        .thenComparing(Map.Entry::getKey));
    return result;
  }

  static String stableId(Map.Entry<String, JsonObject> entry) {
    return entry.getValue().has("id") ? entry.getValue().get("id").getAsString().trim() : entry.getKey();
  }

  static String fileName(String category, ResourceLocation id) {
    return "data/" + id.getNamespace() + "/" + PREFIX + category + "/" + id.getPath() + ".json";
  }

  static ResourceLocation location(String text) {
    return Objects.requireNonNull(ResourceLocation.tryParse(text), "Invalid resource ID: " + text);
  }

  private static Map<String, Map<String, JsonObject>> copy(Catalog source) {
    Map<String, Map<String, JsonObject>> result = new LinkedHashMap<>();
    for (String category : CATEGORIES)
      result.put(category, new LinkedHashMap<>(source.category(category)));
    return result;
  }

  private static JsonObject bundledFile(String file) {
    var stream = KatheryneDataPack.class.getClassLoader().getResourceAsStream(file);
    if (stream == null) throw new IllegalStateException("Missing bundled Katheryne data: " + file);
    try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
      return read(reader, file);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read " + file, e);
    }
  }

  static JsonObject read(Reader reader, String source) throws IOException {
    char[] buffer = new char[4096];
    StringBuilder text = new StringBuilder();
    for (int read; (read = reader.read(buffer)) != -1; ) {
      text.append(buffer, 0, read);
      if (text.length() > 262144) throw new IllegalArgumentException("Data file too large: " + source);
    }
    try {
      return JsonParser.parseString(text.toString()).getAsJsonObject();
    } catch (RuntimeException e) {
      throw new IllegalArgumentException("Invalid Katheryne data in " + source, e);
    }
  }
}
