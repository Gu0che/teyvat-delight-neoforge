package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

/** Detached local drafts. Saving definitions never changes player stock or commission records. */
public final class KatheryneConfigEditor {
  private KatheryneConfigEditor() {}

  public static Session open(Path directory) throws IOException {
    return new Session(directory);
  }

  public static JsonObject defaults() {
    return KatheryneDataPack.defaultControls().deepCopy();
  }

  public static boolean validItem(String text) {
    ResourceLocation id = ResourceLocation.tryParse(text);
    return id != null && BuiltInRegistries.ITEM.containsKey(id)
        && BuiltInRegistries.ITEM.get(id) != Items.AIR;
  }

  public static boolean validShopId(String text) {
    ResourceLocation id = ResourceLocation.tryParse(text);
    return id != null && text.contains(":") && !text.equals("teyvatdelight:commissions")
        && Arrays.stream(id.getPath().split("/", -1))
            .noneMatch(part -> part.isEmpty() || part.equals(".") || part.equals(".."));
  }

  public static final class Session {
    private final Path directory;
    private final Map<Path, String> originals = new HashMap<>();
    private KatheryneDataPack.Catalog catalog;
    private JsonObject controls;

    private Session(Path directory) throws IOException {
      this.directory = directory.toAbsolutePath().normalize();
      catalog = KatheryneDataPack.local(KatheryneDataPack.bundled(), pack());
      Path path = controlsPath();
      remember(path);
      controls = readControls();
      validate(controls, catalog, false);
      for (String key : catalog.category("shops").keySet()) remember(shopPath(key));
    }

    public JsonObject controls() { return controls.deepCopy(); }

    public List<String> shops() {
      return KatheryneDataPack.ordered(catalog.category("shops")).stream()
          .filter(e -> !KatheryneDataPack.isExample(e.getKey())
              || KatheryneDataPack.enabled(e.getValue()))
          .map(Map.Entry::getKey).toList();
    }

    public JsonObject shop(String id) {
      JsonObject value = catalog.category("shops").get(id);
      if (value == null) throw new IllegalArgumentException("Unknown shop: " + id);
      return value.deepCopy();
    }

    public JsonObject reward(JsonElement ref) {
      if (ref.isJsonObject()) return ref.getAsJsonObject().deepCopy();
      JsonObject value = catalog.category("rewards").get(ref.getAsString());
      if (value == null) throw new IllegalArgumentException("Unknown reward: " + ref);
      return value.deepCopy();
    }

    public List<String> pools() {
      return catalog.category("pools").entrySet().stream()
          .filter(e -> !e.getValue().has("type")
              || e.getValue().get("type").getAsString().equals("item"))
          .map(Map.Entry::getKey).sorted().toList();
    }

    public JsonObject pool(String id) {
      JsonObject value = catalog.category("pools").get(id);
      if (value == null) throw new IllegalArgumentException("Unknown pool: " + id);
      return value.deepCopy();
    }

    public void validateControls(JsonObject draft, boolean tagsReady) throws IOException {
      for (String key : draft.keySet())
        if (!Set.of("commissions", "shopRefreshTime", "equipment", "balanceItems").contains(key))
          throw new IllegalArgumentException("Unknown control: " + key);
      validate(draft, freshCatalog(), tagsReady);
    }

    public void validateShop(String id, JsonObject draft, boolean tagsReady) throws IOException {
      KatheryneDataPack.location(id);
      JsonObject checked = draft.deepCopy();
      checked.addProperty("enabled", true);
      JsonArray rows = checked.getAsJsonArray("offers");
      if (rows == null) throw new IllegalArgumentException("Missing offers");
      for (JsonElement row : rows) {
        JsonObject offer = row.getAsJsonObject();
        offer.addProperty("enabled", true);
        checkStacks(offer.get("cost"), false);
        String type = offer.has("type") ? offer.get("type").getAsString() : "fixed";
        if (type.equals("fixed")) checkStacks(offer.get("sell"), true);
      }
      var changed = replaceShop(freshCatalog(), id, checked);
      validate(readControls(), changed, tagsReady);
    }

    public boolean saveControls(JsonObject draft, boolean tagsReady) throws IOException {
      checkUnchanged(controlsPath());
      validateControls(draft, tagsReady);
      if (draft.equals(controls)) return false;
      String existing = originals.get(controlsPath());
      String content = KatheryneConfigComments.format(draft,
          existing == null ? "" : existing, controls);
      checkSize(content);
      backup(controlsPath());
      Files.createDirectories(controlsPath().getParent());
      KatheryneShopConfig.writeAtomically(controlsPath(), content);
      controls = draft.deepCopy();
      remember(controlsPath());
      return true;
    }

    public boolean saveShop(String id, JsonObject draft, boolean tagsReady) throws IOException {
      Path path = shopPath(id);
      // New IDs may not replace an existing local or bundled definition.
      if (!originals.containsKey(path)) {
        if (Files.exists(path) || freshCatalog().category("shops").containsKey(id))
          throw new IllegalArgumentException("Shop already exists: " + id);
        originals.put(path, null);
      }
      checkUnchanged(path);
      validateShop(id, draft, tagsReady);
      if (draft.equals(catalog.category("shops").get(id))) return false;
      String existing = Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : "";
      StringBuilder notes = new StringBuilder();
      for (String note : KatheryneConfigComments.comments(existing))
        notes.append("// ").append(note).append('\n');
      String content = notes + KatheryneConfigLayout.format(draft);
      checkSize(content);
      KatheryneDataFiles.ensurePack(pack());
      Files.createDirectories(path.getParent());
      backup(path);
      KatheryneShopConfig.writeAtomically(path, content);
      catalog = replaceShop(freshCatalog(), id, draft.deepCopy());
      remember(path);
      return true;
    }

    public Path shopPath(String id) {
      if (!validShopId(id)) throw new IllegalArgumentException("Invalid shop resource ID: " + id);
      return KatheryneDataFiles.resourcePath(pack(), "shops", id);
    }
    private Path controlsPath() { return directory.resolve(KatheryneShopConfig.FILE_NAME); }
    private Path pack() { return directory.resolve(KatheryneDataPack.PACK_DIRECTORY); }

    private JsonObject readControls() throws IOException {
      if (!Files.exists(controlsPath())) return defaults();
      try (var reader = Files.newBufferedReader(controlsPath(), StandardCharsets.UTF_8)) {
        return KatheryneDataPack.read(reader, controlsPath().toString());
      }
    }

    private KatheryneDataPack.Catalog freshCatalog() throws IOException {
      return KatheryneDataPack.local(KatheryneDataPack.bundled(), pack());
    }

    private void remember(Path path) throws IOException {
      originals.put(path, Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : null);
    }

    private void checkUnchanged(Path path) throws IOException {
      String now = Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : null;
      if (!Objects.equals(originals.get(path), now))
        throw new IllegalStateException("File changed since opening; reopen the editor / 文件已被其他程序修改，请重新打开编辑器");
    }

    private void backup(Path path) throws IOException {
      if (!Files.exists(path)) return;
      Path relative = directory.relativize(path);
      Path destination = directory.resolve("teyvatdelight-editor-backups")
          .resolve(System.currentTimeMillis() + "-" + UUID.randomUUID()).resolve(relative);
      Files.createDirectories(destination.getParent());
      Files.copy(path, destination);
    }
  }

  private static KatheryneDataPack.Catalog replaceShop(
      KatheryneDataPack.Catalog source, String id, JsonObject value) {
    Map<String, Map<String, JsonObject>> result = new LinkedHashMap<>(source.entries());
    Map<String, JsonObject> shops = new LinkedHashMap<>(source.category("shops"));
    shops.put(id, value);
    result.put("shops", shops);
    return new KatheryneDataPack.Catalog(result);
  }

  private static void validate(JsonObject controls, KatheryneDataPack.Catalog catalog,
      boolean tagsReady) {
    JsonObject root = KatheryneDataPack.assemble(controls, catalog);
    var commissions = CommissionConfig.parse(root);
    var shops = ShopDefinitions.parse(root);
    var settings = KatheryneShopConfig.readSettings(root);
    KatheryneShopConfig.readBalanceItems(root, true);
    for (var reward : List.of(commissions.reward(), commissions.milestoneReward()))
      for (var item : reward.fixed())
        if (!validItem(item.item())) throw new IllegalArgumentException("Unknown reward item: " + item.item());
    if (tagsReady) {
      CommissionConfig.validateRegistries(commissions);
      ShopDefinitions.validate(commissions, shops, settings.equipment());
    }
  }

  private static void checkStacks(JsonElement value, boolean required) {
    if (value == null || !value.isJsonArray())
      throw new IllegalArgumentException("Missing item list");
    JsonArray array = value.getAsJsonArray();
    if (array.size() > 128 || required && array.isEmpty())
      throw new IllegalArgumentException("Invalid item list size");
    Set<String> ids = new HashSet<>();
    for (JsonElement element : array) {
      JsonObject stack = element.getAsJsonObject();
      String id = stack.get("item").getAsString();
      if (!validItem(id) || !ids.add(ResourceLocation.tryParse(id).toString()))
        throw new IllegalArgumentException("Invalid/duplicate item: " + id);
      CommissionConfig.number(stack, "count", 0, 1, 1000000);
    }
  }

  private static void checkSize(String content) {
    if (content.length() > 262144)
      throw new IllegalArgumentException("Data file exceeds 262144 characters");
  }
}
