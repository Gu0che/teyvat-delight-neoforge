package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneRules.DailySlot;
import com.guoche.teyvatdelight.KatheryneRules.ShopOffer;
import com.guoche.teyvatdelight.KatheryneRules.StackAmount;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawOffer;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.TeyvatMerchantCatalog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class KatheryneRules {
  private static List<ShopOffer> cachedShop;
  private static long ruleBuilds;

  static long ruleBuildCount() {
    return ruleBuilds;
  }

  private static List<DailySlot> cachedDaily;

  public static void invalidate() {
    cachedShop = null;
    cachedDaily = null;
    WARNED.clear();
  }

  private static final Set<String> WARNED = new HashSet<>();

  private KatheryneRules() {}

  public static int questRefreshTime() {
    return CommissionConfig.settings().refreshTime();
  }

  public static int shopRefreshTime() {
    int configured = KatheryneShopConfig.settings().refreshTime();
    return configured >= 0 ? configured : questRefreshTime() == 0 ? 22000 : questRefreshTime();
  }

  public static long cycle(MinecraftServer server, int refreshTime) {
    return Math.floorDiv(server.overworld().getDayTime() + 24000L - refreshTime, 24000L);
  }

  public static int secondsUntil(MinecraftServer server, int refreshTime) {
    long elapsed = Math.floorMod(server.overworld().getDayTime() - refreshTime, 24000L);
    return (int) ((24000L - elapsed + 19L) / 20L);
  }

  public static boolean rotateQuests() {
    return CommissionConfig.settings().rotation();
  }

  public static List<Item> questTargets() {
    try {
      return CommissionConfig.pool("dishes").stream()
          .map(c -> BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(c.id())))
          .toList();
    } catch (IllegalArgumentException e) {
      return List.of();
    }
  }

  public static List<StackAmount> questRewards() {
    return CommissionConfig.settings().reward().fixed().stream()
        .map(r -> parseStack(r.item() + "*" + r.count()))
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  public static List<ShopOffer> shopOffers() {
    if (cachedShop != null) return cachedShop;
    ruleBuilds++;
    List<ShopOffer> result = new ArrayList<>();
    for (RawOffer configured : KatheryneShopConfig.offers()) {
      List<StackAmount> outputs = new ArrayList<>();
      for (RawStack stack : configured.sell()) {
        StackAmount amount = parseStack(stack.item() + "*" + stack.count());
        if (amount != null) outputs.add(amount);
      }
      List<StackAmount> prices = new ArrayList<>();
      for (RawStack stack : configured.cost()) {
        StackAmount price = parseStack(stack.item() + "*" + stack.count());
        if (price != null) prices.add(price);
      }
      if (outputs.size() == configured.sell().size() && prices.size() == configured.cost().size()) {
        result.add(
            new ShopOffer(
                configured.id(),
                List.copyOf(outputs),
                List.copyOf(prices),
                configured.dailyLimit()));
      }
    }
    cachedShop = List.copyOf(result);
    return cachedShop;
  }

  public static List<DailySlot> dailySlots() {
    if (cachedDaily != null) return cachedDaily;
    ruleBuilds++;
    List<DailySlot> result = new ArrayList<>();
    for (KatheryneShopConfig.DailyEntry entry : KatheryneShopConfig.settings().dailySlots()) {
      LinkedHashSet<Item> pool = new LinkedHashSet<>();
      boolean valid = true;
      if (entry.fixed()) {
        for (RawStack stack : entry.sell()) {
          StackAmount item = parseStack(stack.item() + "*" + stack.count());
          if (item == null) valid = false;
          else pool.add(item.item());
        }
      } else {
        try {
          CommissionConfig.itemChoices(entry.pool())
              .forEach(
                  c -> pool.add(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(c.id()))));
        } catch (IllegalArgumentException exception) {
          valid = false;
          warn(entry.id() + ": " + exception.getMessage());
        }
      }
      List<StackAmount> prices = new ArrayList<>();
      for (RawStack stack : entry.cost()) {
        StackAmount price = parseStack(stack.item() + "*" + stack.count());
        if (price != null) prices.add(price);
      }
      if (valid
          && !pool.isEmpty()
          && !prices.isEmpty()
          && prices.size() == entry.cost().size()
          && prices.stream().map(StackAmount::item).distinct().count() == prices.size()
          && (entry.repeat() || entry.drawCount() <= pool.size())) {
        result.add(
            new DailySlot(entry.id(), List.copyOf(pool), List.copyOf(prices), entry.drawCount()));
        continue;
      }
      warn(
          "dailySlots["
              + entry.id()
              + "]: missing items, empty tag, duplicate cost, or drawCount exceeds unique pool");
    }
    cachedDaily = List.copyOf(result);
    return cachedDaily;
  }

  public static String dailySlotName(String key) {
    var entry = KatheryneShopConfig.dailyEntry(key);
    return entry == null ? "" : entry.name();
  }

  public static List<ItemStack> rollDailyItems(ServerPlayer player, DailySlot slot) {
    var entry = KatheryneShopConfig.dailyEntry(slot.key());
    if (entry != null && entry.fixed()) {
      return entry.sell().stream()
          .map(
              stack ->
                  new ItemStack(
                      parseStack(stack.item() + "*" + stack.count()).item(), stack.count()))
          .toList();
    }
    List<CommissionConfig.Choice> choices =
        entry == null
            ? slot.pool().stream()
                .map(
                    item ->
                        new CommissionConfig.Choice(
                            BuiltInRegistries.ITEM.getKey(item).toString(), 1))
                .toList()
            : CommissionConfig.itemChoices(entry.pool());
    return CommissionConfig.drawChoices(
            player, choices, slot.drawCount(), entry != null && entry.repeat())
        .stream()
        .map(
            id -> {
              Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));
              ItemStack stack =
                  entry == null || entry.enchantEquipment()
                      ? KatheryneEquipment.roll(player, item)
                      : new ItemStack(item);
              stack.setCount(entry == null ? 1 : entry.count());
              return stack;
            })
        .toList();
  }

  private static void resolveSelector(Set<Item> items, String text) {
    if (text.isBlank()) return;
    if (text.startsWith("$")) {
      try {
        CommissionConfig.pool(text.substring(1))
            .forEach(c -> items.add(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(c.id()))));
      } catch (IllegalArgumentException e) {
        warn(text);
      }
      return;
    }
    if (text.equals("@merchant_ingredients")) {
      TeyvatMerchantCatalog.INGREDIENTS.stream()
          .map(java.util.function.Supplier::get)
          .forEach(items::add);
      return;
    }
    boolean tag = text.startsWith("#");
    ResourceLocation id = ResourceLocation.tryParse(tag ? text.substring(1) : text);
    if (id == null) {
      warn(text);
    } else if (tag) {
      BuiltInRegistries.ITEM
          .getTag(TagKey.create(Registries.ITEM, id))
          .ifPresent(
              found ->
                  found.stream()
                      .map(Holder::value)
                      .filter(item -> item != Items.AIR)
                      .forEach(items::add));
    } else {
      BuiltInRegistries.ITEM
          .getOptional(id)
          .filter(item -> item != Items.AIR)
          .ifPresentOrElse(items::add, () -> warn(text));
    }
  }

  private static StackAmount parseStack(String entry) {
    String text = entry.trim();
    int split = text.lastIndexOf('*');
    ResourceLocation id =
        ResourceLocation.tryParse(split < 0 ? text : text.substring(0, split).trim());
    try {
      int amount = split < 0 ? 1 : Integer.parseInt(text.substring(split + 1).trim());
      if (id != null && amount > 0 && amount <= 1_000_000) {
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
        if (item != Items.AIR) return new StackAmount(item, amount);
      }
    } catch (NumberFormatException ignored) {
    }
    warn(entry);
    return null;
  }

  private static void warn(String entry) {
    if (WARNED.add(entry))
      TeyvatDelight.LOGGER.warn("Ignoring invalid Katheryne configuration entry: {}", entry);
  }
}
