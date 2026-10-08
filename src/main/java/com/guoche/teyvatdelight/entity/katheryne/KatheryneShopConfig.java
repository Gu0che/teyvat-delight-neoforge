package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawOffer;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
import com.guoche.teyvatdelight.TeyvatDelight;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KatheryneShopConfig {
  public static final String FILE_NAME = "teyvatdelight-katheryne.json";
  private static Path directory;
  public static String lastError = "";
  public static boolean registriesReady;
  private static List<RawOffer> offers = List.of();
  private static final List<String> DEFAULT_BALANCE_ITEMS =
      List.of("teyvatdelight:mora", "teyvatdelight:primogem");
  private static List<String> balanceItems = DEFAULT_BALANCE_ITEMS;
  private static java.util.Map<String, DailyEntry> slotsById = java.util.Map.of();
  private static Settings settings =
      new Settings(-1, new Equipment(0.70D, 0.10D, false), List.of());

  public record Equipment(double firstChance, double chanceStep, boolean allowCurses) {}

  public record DailyEntry(
      String id,
      String name,
      boolean fixed,
      List<RawStack> sell,
      List<String> pool,
      int drawCount,
      int count,
      boolean enchantEquipment,
      List<RawStack> cost,
      boolean repeat) {
    public DailyEntry(
        String id,
        String name,
        boolean fixed,
        List<RawStack> sell,
        List<String> pool,
        int drawCount,
        int count,
        boolean enchantEquipment,
        List<RawStack> cost) {
      this(id, name, fixed, sell, pool, drawCount, count, enchantEquipment, cost, false);
    }
  }

  public record Settings(int refreshTime, Equipment equipment, List<DailyEntry> dailySlots) {}

  private KatheryneShopConfig() {}

  private static JsonObject effectiveRoot;

  public static void initialize(Path configDirectory) {
    directory = configDirectory;
    Path path = configDirectory.resolve(FILE_NAME);
    try {
      Files.createDirectories(configDirectory);
      if (Files.notExists(path))
        KatheryneDataFiles.save(path, KatheryneDataPack.defaultControls());
      KatheryneGeneratedData.initialize(configDirectory);
      JsonObject controls = readControls();
      installRoot(
          KatheryneDataPack.assemble(controls, KatheryneDataPack.configured(directory)),
          registriesReady);
      lastError = "";
    } catch (IOException | RuntimeException exception) {
      lastError = exception.getMessage();
      TeyvatDelight.LOGGER.error("Cannot load Katheryne config {}", path, exception);
    }
  }

  static Path directory() {
    return directory;
  }

  /** Snapshot for diagnostics/export; changes to it cannot mutate active definitions. */
  public static JsonObject exportDefinitions() {
    return effectiveRoot == null ? KatheryneDataPack.defaultsRoot() : effectiveRoot.deepCopy();
  }

  static boolean loadDataPack(KatheryneDataPack.Catalog catalog) {
    try {
      installRoot(KatheryneDataPack.assemble(readControls(), catalog), true);
      lastError = "";
      return true;
    } catch (IOException | RuntimeException exception) {
      lastError = exception.getMessage();
      TeyvatDelight.LOGGER.error("Rejected Katheryne data pack; keeping previous definitions", exception);
      return false;
    }
  }

  private static JsonObject readControls() throws IOException {
    JsonObject controls = readRoot(directory.resolve(FILE_NAME));
    for (String key : controls.keySet())
      if (!List.of("commissions", "shopRefreshTime", "equipment", "balanceItems").contains(key))
        throw new IllegalArgumentException("Unknown Katheryne global option: " + key);
    if (!controls.has("commissions") || !controls.get("commissions").isJsonObject())
      throw new IllegalArgumentException("Katheryne global config requires a commissions object");
    if (controls.has("shops") || controls.has("pools")
        || controls.getAsJsonObject("commissions").has("templates"))
      throw new IllegalArgumentException("Move content definitions into the Katheryne data pack");
    return controls;
  }

  private static void installRoot(JsonObject root, boolean validate) {
    CommissionConfig.Settings commissions = CommissionConfig.parse(root);
    List<ShopDefinitions.Shop> loadedShops = ShopDefinitions.parse(root);
    List<RawOffer> loadedOffers = ShopDefinitions.ordinaryOffers(loadedShops);
    Settings global = readSettings(root);
    List<String> loadedBalances = readBalanceItems(root, validate);
    Settings loadedSettings =
        new Settings(global.refreshTime(), global.equipment(), ShopDefinitions.dailyEntries(loadedShops));
    if (validate) {
      CommissionConfig.validateRegistries(commissions);
      ShopDefinitions.validate(commissions, loadedShops, global.equipment());
    }
    offers = loadedOffers;
    settings = loadedSettings;
    balanceItems = loadedBalances;
    ShopDefinitions.install(loadedShops);
    slotsById = loadedSettings.dailySlots().stream()
        .collect(java.util.stream.Collectors.toUnmodifiableMap(DailyEntry::id, e -> e));
    CommissionConfig.install(commissions);
    effectiveRoot = root.deepCopy();
  }

  public static List<RawOffer> offers() {
    return offers;
  }

  public static Settings settings() {
    return settings;
  }

  public static List<String> balanceItems() {
    return balanceItems;
  }

  static List<String> readBalanceItems(JsonObject root, boolean validate) {
    if (!root.has("balanceItems")) return DEFAULT_BALANCE_ITEMS;
    if (!root.get("balanceItems").isJsonArray())
      throw new IllegalArgumentException("balanceItems must be an array of item IDs");
    JsonArray values = root.getAsJsonArray("balanceItems");
    if (values.size() > 2)
      throw new IllegalArgumentException("balanceItems supports up to two header icons");
    List<String> result = new ArrayList<>();
    for (JsonElement value : values) {
      String name = string(value);
      var id = net.minecraft.resources.ResourceLocation.tryParse(name);
      if (id == null)
        throw new IllegalArgumentException("Invalid balance item: " + name);
      String normalized = id.toString();
      if (normalized.equals("minecraft:air") || result.contains(normalized)
          || validate && !net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(id))
        throw new IllegalArgumentException("Invalid or duplicate balance item: " + name);
      result.add(normalized);
    }
    return List.copyOf(result);
  }

  public static boolean reload() {
    long before = CommissionConfig.revision();
    initialize(directory);
    return before != CommissionConfig.revision();
  }

  public static DailyEntry dailyEntry(String id) {
    return slotsById.get(id);
  }

  private static JsonObject readRoot(Path path) throws IOException {
    try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      return JsonParser.parseReader(reader).getAsJsonObject();
    }
  }

  static void writeAtomically(Path path, String text) throws IOException {
    Path temporary = Files.createTempFile(path.getParent(), "katheryne-shop-", ".tmp");
    try {
      Files.writeString(
          temporary, text, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
      try {
        Files.move(
            temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException exception) {
        Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  static Settings readSettings(JsonObject root) {
    int time = root.has("shopRefreshTime") ? integer(root.get("shopRefreshTime")) : -1;
    if (time < -1 || time > 23999)
      throw new IllegalArgumentException("shopRefreshTime must be -1..23999");
    JsonObject equipment =
        root.has("equipment") ? root.getAsJsonObject("equipment") : new JsonObject();
    double first = probability(equipment, "firstChance", 0.70D);
    double step = probability(equipment, "chanceStep", 0.10D);
    boolean curses = equipment.has("allowCurses") && bool(equipment.get("allowCurses"));
    return new Settings(time, new Equipment(first, step, curses), List.of());
  }

  private static String string(JsonElement value) {
    if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
      throw new IllegalArgumentException("Expected a JSON string");
    return value.getAsString();
  }

  private static int integer(JsonElement value) {
    if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
      throw new IllegalArgumentException("Expected an integer");
    return new BigDecimal(value.getAsString()).intValueExact();
  }

  private static boolean bool(JsonElement value) {
    if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean())
      throw new IllegalArgumentException("Expected true or false");
    return value.getAsBoolean();
  }

  private static double probability(JsonObject object, String key, double fallback) {
    if (!object.has(key)) return fallback;
    JsonElement value = object.get(key);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
      throw new IllegalArgumentException("Invalid " + key);
    double number = value.getAsDouble();
    if (!Double.isFinite(number) || number < 0 || number > 1)
      throw new IllegalArgumentException(key + " must be 0..1");
    return number;
  }

  static List<RawStack> readStacks(JsonArray entries) {
    if (entries == null || entries.size() == 0 || entries.size() > 128) return List.of();
    List<RawStack> result = new ArrayList<>();
    Set<String> ids = new HashSet<>();
    for (JsonElement element : entries) {
      JsonObject stack = element.getAsJsonObject();
      String item = string(stack.get("item")).trim();
      var itemId = net.minecraft.resources.ResourceLocation.tryParse(item);
      if (itemId == null) return List.of();
      item = itemId.toString();
      int count = integer(stack.get("count"));
      if (item.isEmpty() || count < 1 || count > 1_000_000 || !ids.add(item)) return List.of();
      result.add(new RawStack(item, count));
    }
    return List.copyOf(result);
  }

}
