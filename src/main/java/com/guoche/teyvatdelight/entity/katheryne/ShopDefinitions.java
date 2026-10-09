package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawOffer;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
import java.util.*;
import net.minecraft.server.MinecraftServer;

/** Ordered definitions only. Stocks and rolls must never be kept in this registry. */
public final class ShopDefinitions {
  public record Offer(String id, String name, KatheryneShopConfig.DailyEntry goods, int limit,
      List<CommissionConditions.Condition> conditions, String lockedDisplay, int artifactStars) {
    public Offer {
      conditions = List.copyOf(conditions);
    }
    public Offer(String id, String name, KatheryneShopConfig.DailyEntry goods, int limit) {
      this(id, name, goods, limit, List.of(), "hide", 0);
    }
  }

  public record Shop(String id, String title, int refreshTime, List<Offer> offers,
      List<CommissionConditions.Condition> conditions, String lockedDisplay) {
    public Shop {
      offers = List.copyOf(offers);
      conditions = List.copyOf(conditions);
    }
    public Shop(String id, String title, int refreshTime, List<Offer> offers) {
      this(id, title, refreshTime, offers, List.of(), "hide");
    }
    public Offer offer(String id) {
      return offers.stream().filter(o -> o.id().equals(id)).findFirst().orElse(null);
    }
  }

  private static List<Shop> configured = List.of();
  private static final Map<String, JsonObject> extensions = new LinkedHashMap<>();
  private static Map<String, Shop> byId = Map.of();

  private ShopDefinitions() {}

  public static List<Shop> shops() {
    return configured;
  }

  public static Shop get(String id) {
    return byId.get(id);
  }

  public static void install(List<Shop> shops) {
    configured = List.copyOf(shops);
    Map<String, Shop> index = new LinkedHashMap<>();
    shops.forEach(s -> index.put(s.id(), s));
    byId = Map.copyOf(index);
  }

  /** Called during mod setup, before a server starts. JSON uses the same schema as config shops. */
  public static void register(String json) {
    if (KatheryneShopConfig.registriesReady)
      throw new IllegalStateException("Register shops before server startup");
    if (json.length() > 262144) throw new IllegalArgumentException("Shop definition too large");
    JsonObject value = JsonParser.parseString(json).getAsJsonObject();
    Shop parsed = parseShop(value);
    if (configured.size() >= 64
        || configured.stream().mapToInt(s -> s.offers().size()).sum() + parsed.offers().size()
            > 4096) throw new IllegalArgumentException("Too many shops or offers");
    if (extensions.containsKey(parsed.id()) || byId.containsKey(parsed.id()))
      throw new IllegalArgumentException("Duplicate shop: " + parsed.id());
    extensions.put(parsed.id(), value.deepCopy());
    List<Shop> all = new ArrayList<>(configured);
    all.add(parsed);
    install(all);
    CommissionConfig.invalidate();
  }

  public static List<Shop> parse(JsonObject root) {
    JsonArray array = root.getAsJsonArray("shops");
    if (array == null || array.size() + extensions.size() > 64)
      throw new IllegalArgumentException("shops must contain at most 64 entries");
    List<Shop> result = new ArrayList<>();
    Set<String> keys = new HashSet<>();
    List<JsonElement> definitions = new ArrayList<>();
    array.forEach(definitions::add);
    extensions.values().forEach(definitions::add);
    int total = 0;
    for (JsonElement element : definitions) {
      Shop shop = parseShop(element.getAsJsonObject());
      if (!keys.add(shop.id())) throw new IllegalArgumentException("Duplicate shop: " + shop.id());
      total += shop.offers().size();
      if (total > 4096) throw new IllegalArgumentException("Too many shop offers");
      result.add(shop);
    }
    return List.copyOf(result);
  }

  private static Shop parseShop(JsonObject value) {
    String id = text(value, "id", "", 128);
    checkId(id);
    if (id.equals("commissions"))
      throw new IllegalArgumentException("Reserved shop ID: commissions");
    String title = text(value, "title", "", 256);
    int time = CommissionConfig.number(value, "refreshTime", -1, -2, 23999);
    JsonArray array = value.getAsJsonArray("offers");
    if (array == null || array.size() > 1024)
      throw new IllegalArgumentException("offers must contain at most 1024 entries");
    List<Offer> result = new ArrayList<>();
    Set<String> keys = new HashSet<>();
    int stacks = 0;
    for (JsonElement element : array) {
      JsonObject offer = element.getAsJsonObject();
      String key = text(offer, "id", "", 128), name = text(offer, "name", "", 256);
      checkId(key);
      if (!keys.add(key))
        throw new IllegalArgumentException("Duplicate offer in " + id + ": " + key);
      String type = text(offer, "type", "fixed", 16);
      boolean fixed = type.equals("fixed");
      if (!fixed && !type.equals("random"))
        throw new IllegalArgumentException("Unknown offer type: " + type);
      List<RawStack> cost = KatheryneShopConfig.readStacks(offer.getAsJsonArray("cost"));
      if (!offer.has("cost") || !offer.get("cost").isJsonArray())
        throw new IllegalArgumentException("Missing/invalid cost in " + key);
      int limit = CommissionConfig.number(offer, "dailyLimit", fixed ? -1 : 1, -1, 1000000);
      List<RawStack> sell = List.of();
      List<String> pool = List.of();
      int draws = 1, count = 1;
      boolean enchant = false, repeat = false;
      if (fixed) {
        sell = KatheryneShopConfig.readStacks(offer.getAsJsonArray("sell"));
        if (sell.isEmpty()) throw new IllegalArgumentException("Missing/invalid sell in " + key);
        draws = sell.size();
      } else {
        JsonArray entries = offer.getAsJsonArray("pool");
        if (entries == null || entries.isEmpty() || entries.size() > 4096)
          throw new IllegalArgumentException("Invalid pool in " + key);
        List<String> selectors = new ArrayList<>();
        for (JsonElement entry : entries) {
          if (!entry.isJsonPrimitive()
              || !entry.getAsJsonPrimitive().isString()
              || entry.getAsString().isBlank()
              || entry.getAsString().length() > 256)
            throw new IllegalArgumentException("Invalid selector in " + key);
          selectors.add(entry.getAsString().trim());
        }
        pool = List.copyOf(selectors);
        draws = CommissionConfig.number(offer, "drawCount", 1, 1, 128);
        count = CommissionConfig.number(offer, "count", 1, 1, 64);
        enchant = booleanValue(offer, "enchantEquipment", true);
        repeat = booleanValue(offer, "repeat", false);
      }
      stacks += draws + cost.size();
      if (stacks > 4096)
        throw new IllegalArgumentException("Shop exceeds 4096 item/price entries: " + id);
      var goods =
          new KatheryneShopConfig.DailyEntry(
              key, name, fixed, sell, pool, draws, count, enchant, cost, repeat);
      result.add(new Offer(key, name, goods, limit,
          CommissionConditions.parse(offer.get("conditions")), lockedDisplay(offer),
          CommissionConfig.number(offer, "artifactStars", 0, 0, 5)));
    }
    return new Shop(id, title, time, List.copyOf(result),
        CommissionConditions.parse(value.get("conditions")), lockedDisplay(value));
  }

  public static void validate(
      CommissionConfig.Settings candidate,
      List<Shop> shops,
      KatheryneShopConfig.Equipment equipment) {
    for (Shop shop : shops) {
      if (!CommissionConditions.possible(shop.conditions())) continue;
      CommissionConditions.validate(shop.conditions());
      for (var offer : shop.offers()) {
        if (!CommissionConditions.possible(offer.conditions())) continue;
        CommissionConditions.validate(offer.conditions());
        if (offer.artifactStars() > 0) {
          com.guoche.teyvatdelight.integration.artifacts.ArtifactShopIntegration.validate();
          List<String> items = offer.goods().fixed()
              ? offer.goods().sell().stream().map(RawStack::item).toList()
              : CommissionConfig.itemChoices(candidate, offer.goods().pool()).stream().map(CommissionConfig.Choice::id).toList();
          for (String item : items)
            if (!com.guoche.teyvatdelight.integration.artifacts.ArtifactShopIntegration.isArtifact(
                net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.tryParse(item))))
              throw new IllegalArgumentException("Shop " + shop.id() + ", trade " + offer.id()
                  + ": artifactStars source contains a non-artifact: " + item);
        }
      }
      CommissionConfig.validateShop(
          candidate,
          List.of(),
          new KatheryneShopConfig.Settings(
              -1, equipment, shop.offers().stream()
                  .filter(o -> CommissionConditions.possible(o.conditions())).map(Offer::goods).toList()));
    }
  }

  public static boolean available(net.minecraft.server.level.ServerPlayer player, Shop shop) {
    return shop != null && CommissionConditions.matches(player, shop.conditions());
  }

  public static boolean available(net.minecraft.server.level.ServerPlayer player, Shop shop, Offer offer) {
    return available(player, shop) && CommissionConditions.matches(player, offer.conditions());
  }

  public static List<Offer> visibleOffers(net.minecraft.server.level.ServerPlayer player, Shop shop) {
    if (!available(player, shop)) return List.of();
    return shop.offers().stream().filter(o -> available(player, shop, o)
        || o.lockedDisplay().equals("show")).toList();
  }

  private static String lockedDisplay(JsonObject object) {
    String mode = text(object, "lockedDisplay", "hide", 8);
    if (!Set.of("hide", "show").contains(mode))
      throw new IllegalArgumentException("lockedDisplay must be hide or show");
    return mode;
  }

  public static int refreshTime(Shop shop) {
    return shop.refreshTime() == -1 ? KatheryneRules.shopRefreshTime() : shop.refreshTime();
  }

  public static long cycle(MinecraftServer server, Shop shop) {
    int time = refreshTime(shop);
    return time == -2 ? Long.MIN_VALUE : KatheryneRules.cycle(server, time);
  }

  public static int seconds(MinecraftServer server, Shop shop) {
    int time = refreshTime(shop);
    return time == -2 ? -1 : KatheryneRules.secondsUntil(server, time);
  }

  public static List<RawOffer> ordinaryOffers(List<Shop> shops) {
    return shops.stream()
        .filter(s -> s.id().equals("shop"))
        .flatMap(s -> s.offers().stream())
        .filter(o -> o.goods().fixed())
        .map(o -> new RawOffer(o.id(), o.goods().sell(), o.goods().cost(), o.limit()))
        .toList();
  }

  public static List<KatheryneShopConfig.DailyEntry> dailyEntries(List<Shop> shops) {
    return shops.stream()
        .filter(s -> s.id().equals("daily_shop"))
        .flatMap(s -> s.offers().stream())
        .map(Offer::goods)
        .toList();
  }

  private static String text(JsonObject object, String key, String fallback, int max) {
    if (!object.has(key)) return fallback;
    JsonElement value = object.get(key);
    if (!value.isJsonPrimitive()
        || !value.getAsJsonPrimitive().isString()
        || value.getAsString().length() > max) throw new IllegalArgumentException("Invalid " + key);
    return value.getAsString().trim();
  }

  private static boolean booleanValue(JsonObject object, String key, boolean fallback) {
    if (!object.has(key)) return fallback;
    JsonElement value = object.get(key);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean())
      throw new IllegalArgumentException("Invalid " + key);
    return value.getAsBoolean();
  }

  private static void checkId(String value) {
    if (!value.matches("[a-z0-9_:/.-]{1,128}"))
      throw new IllegalArgumentException("Invalid stable ID: " + value);
  }
}
