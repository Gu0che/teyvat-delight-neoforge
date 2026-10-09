package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
import com.guoche.teyvatdelight.KatheryneSnapshot;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** One stock namespace per player and stable shop ID; removed shops retain their saved records. */
public final class ShopBook {
  private static final class Entry {
    int bought;
    List<ItemStack> rolled;
  }

  private static final class Stock {
    long cycle;
    Map<String, Entry> entries = new HashMap<>();

    Stock(long cycle) {
      this.cycle = cycle;
    }
  }

  private static final class TradeCounts {
    long total;
    final Map<String, Long> shops = new HashMap<>();
  }

  private final Map<UUID, Map<String, Stock>> players = new HashMap<>();
  private final Map<UUID, TradeCounts> trades = new HashMap<>();
  private final Runnable dirty;

  public ShopBook(Runnable dirty) {
    this.dirty = dirty;
  }

  public void importRegular(UUID player, long cycle, Map<String, Integer> counts) {
    var shops = players.computeIfAbsent(player, k -> new HashMap<>());
    if (shops.containsKey("shop")) return;
    Stock stock = new Stock(cycle);
    counts.forEach(
        (key, count) -> {
          var entry = new Entry();
          entry.bought = count;
          stock.entries.put(key, entry);
        });
    shops.put("shop", stock);
  }

  public void importDaily(
      UUID player,
      long cycle,
      Map<String, com.guoche.teyvatdelight.KatheryneData.DailyOffer> offers) {
    var shops = players.computeIfAbsent(player, k -> new HashMap<>());
    if (shops.containsKey("daily_shop")) return;
    Stock stock = new Stock(cycle);
    offers.forEach(
        (key, offer) -> {
          var entry = new Entry();
          entry.bought = offer.bought() ? 1 : 0;
          entry.rolled = offer.items().stream().map(ItemStack::copy).toList();
          stock.entries.put(key, entry);
        });
    shops.put("daily_shop", stock);
  }

  private Stock stock(ServerPlayer player, ShopDefinitions.Shop shop) {
    var shops = players.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
    long cycle = ShopDefinitions.cycle(player.server, shop);
    Stock stock = shops.get(shop.id());
    if (stock == null || cycle > stock.cycle) {
      stock = new Stock(cycle);
      shops.put(shop.id(), stock);
      dirty.run();
    }
    return stock;
  }

  public void refresh(ServerPlayer player) {
    for (var shop : ShopDefinitions.shops()) refresh(player, shop);
  }

  public void refresh(ServerPlayer player, ShopDefinitions.Shop shop) {
    var shops = players.computeIfAbsent(player.getUUID(), key -> new HashMap<>());
    shops.put(shop.id(), new Stock(ShopDefinitions.cycle(player.server, shop)));
    for (var offer : shop.offers()) products(player, shop, offer);
    dirty.run();
  }

  private Entry entry(ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    var stock = stock(player, shop);
    Entry entry = stock.entries.get(offer.id());
    if (entry == null) {
      entry = new Entry();
      stock.entries.put(offer.id(), entry);
      dirty.run();
    }
    return entry;
  }

  public int remaining(
      ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    return offer.limit() < 0 ? -1 : Math.max(0, offer.limit() - entry(player, shop, offer).bought);
  }

  public void record(ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    Entry entry = entry(player, shop, offer);
    entry.bought = (int) Math.min(Integer.MAX_VALUE, (long) entry.bought + 1);
    dirty.run();
  }

  public List<KatheryneSnapshot.StackAmount> products(
      ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    if (!ShopDefinitions.available(player, shop, offer)) return List.of();
    Entry entry = entry(player, shop, offer);
    var goods = offer.goods();
    if (entry.rolled == null && goods.fixed() && offer.artifactStars() > 0) {
      entry.rolled = goods.sell().stream().map(a -> {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(a.item())), a.count());
        return com.guoche.teyvatdelight.integration.artifacts.ArtifactShopIntegration
            .initialize(stack, offer.artifactStars(), player.getRandom());
      }).toList();
      dirty.run();
    }
    if (entry.rolled == null && !goods.fixed()) {
      entry.rolled =
          CommissionConfig.drawChoices(
                  player,
                  CommissionConfig.itemChoices(goods.pool()),
                  goods.drawCount(),
                  goods.repeat())
              .stream()
              .map(
                  id -> {
                    var item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));
                    ItemStack stack =
                        goods.enchantEquipment()
                            ? KatheryneEquipment.roll(player, item)
                            : new ItemStack(item);
                    stack.setCount(goods.count());
                    return com.guoche.teyvatdelight.integration.artifacts.ArtifactShopIntegration
                        .initialize(stack, offer.artifactStars(), player.getRandom());
                  })
              .toList();
      dirty.run();
    }
    return entry.rolled == null
        ? stacks(goods.sell())
        : entry.rolled.stream()
            .map(
                stack -> {
                  ItemStack icon = stack.copy();
                  icon.setCount(1);
                  return new KatheryneSnapshot.StackAmount(icon, stack.getCount());
                })
            .toList();
  }

  public KatheryneSnapshot.StoreRow row(
      ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    if (!ShopDefinitions.available(player, shop, offer))
      return new KatheryneSnapshot.StoreRow(offer.id(), offer.name(),
          offer.goods().fixed() ? stacks(offer.goods().sell())
              : List.of(new KatheryneSnapshot.StackAmount(new ItemStack(net.minecraft.world.item.Items.BOOK), 1)),
          stacks(offer.goods().cost()), 0, true,
          CommissionConditions.description(player, offer.conditions()));
    return new KatheryneSnapshot.StoreRow(
        offer.id(),
        offer.name(),
        products(player, shop, offer),
        stacks(offer.goods().cost()),
        remaining(player, shop, offer));
  }

  public String buy(ServerPlayer player, ShopDefinitions.Shop shop, ShopDefinitions.Offer offer) {
    if (!CommissionConfig.runtimeValid) return "gui.teyvatdelight.katheryne.invalid_rules";
    if (!ShopDefinitions.available(player, shop, offer)) return "gui.teyvatdelight.katheryne.locked";
    if (remaining(player, shop, offer) == 0) return "gui.teyvatdelight.katheryne.sold_out";
    List<KatheryneSnapshot.StackAmount> goods = products(player, shop, offer);
    if (goods.isEmpty() || goods.stream().anyMatch(g -> g.icon().isEmpty()))
      return "gui.teyvatdelight.katheryne.invalid_rules";
    int[] plan = new int[player.getInventory().getContainerSize()];
    for (RawStack price : offer.goods().cost()) {
      var item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(price.item()));
      int left = price.count();
      for (int i = 0; i < plan.length && left > 0; i++) {
        ItemStack stack = player.getInventory().getItem(i);
        if (stack.is(item)) {
          int take = Math.min(left, Math.max(0, stack.getCount() - plan[i]));
          plan[i] += take;
          left -= take;
        }
      }
      if (left > 0) return "gui.teyvatdelight.katheryne.missing_payment";
    }
    for (int i = 0; i < plan.length; i++)
      if (plan[i] > 0) player.getInventory().getItem(i).shrink(plan[i]);
    record(player, shop, offer);
    for (var product : goods) {
      int left = product.count();
      while (left > 0) {
        ItemStack stack = product.icon().copy();
        stack.setCount(Math.min(left, stack.getMaxStackSize()));
        left -= stack.getCount();
        player.getInventory().add(stack);
        if (!stack.isEmpty()) player.drop(stack, false);
      }
    }
    recordTrade(player, shop.id());
    KatheryneData.get(player.server).commissions.checkUrgent(player);
    com.guoche.teyvatdelight.api.KatheryneApi.shopPurchased(player, shop.id(), offer.id());
    return "";
  }

  private void recordTrade(ServerPlayer player, String shopId) {
    TradeCounts counts = trades.computeIfAbsent(player.getUUID(), key -> new TradeCounts());
    if (counts.total < Long.MAX_VALUE) counts.total++;
    counts.shops.merge(shopId, 1L, (old, one) -> old == Long.MAX_VALUE ? old : old + one);
    dirty.run();
  }

  /** Lifetime settlements only; reading does not create stock or roll products. */
  public long tradeCount(ServerPlayer player) {
    TradeCounts counts = trades.get(player.getUUID());
    return counts == null ? 0L : counts.total;
  }

  public long tradeCount(ServerPlayer player, String shopId) {
    TradeCounts counts = trades.get(player.getUUID());
    return counts == null ? 0L : counts.shops.getOrDefault(shopId, 0L);
  }

  public void loadTradeCounts(CompoundTag saved) {
    trades.clear();
    for (String id : saved.getAllKeys()) {
      try {
        UUID player = UUID.fromString(id);
        CompoundTag tag = saved.getCompound(id);
        TradeCounts counts = new TradeCounts();
        counts.total = Math.max(0L, tag.getLong("Total"));
        CompoundTag shops = tag.getCompound("Shops");
        for (String key : shops.getAllKeys())
          if (key.matches("[a-z0-9_:/.-]{1,128}"))
            counts.shops.put(key, Math.max(0L, shops.getLong(key)));
        trades.put(player, counts);
      } catch (IllegalArgumentException exception) {
        com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn("Invalid trade counts {}", id);
      }
    }
  }

  public CompoundTag saveTradeCounts() {
    CompoundTag saved = new CompoundTag();
    trades.forEach((id, counts) -> {
      CompoundTag tag = new CompoundTag(), shops = new CompoundTag();
      tag.putLong("Total", counts.total);
      counts.shops.forEach(shops::putLong);
      tag.put("Shops", shops);
      saved.put(id.toString(), tag);
    });
    return saved;
  }

  public static List<KatheryneSnapshot.StackAmount> stacks(List<RawStack> items) {
    return items.stream()
        .map(
            raw ->
                new KatheryneSnapshot.StackAmount(
                    new ItemStack(
                        BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(raw.item()))),
                    raw.count()))
        .toList();
  }

  public void load(CompoundTag saved, HolderLookup.Provider registries) {
    for (String id : saved.getAllKeys())
      try {
        UUID player = UUID.fromString(id);
        Map<String, Stock> shops = new HashMap<>();
        CompoundTag shopTags = saved.getCompound(id);
        for (String key : shopTags.getAllKeys()) {
          CompoundTag tag = shopTags.getCompound(key);
          Stock stock = new Stock(tag.getLong("Cycle"));
          CompoundTag entries = tag.getCompound("Entries");
          for (String offer : entries.getAllKeys()) {
            CompoundTag record = entries.getCompound(offer);
            Entry entry = new Entry();
            entry.bought = Math.max(0, record.getInt("Bought"));
            if (record.contains("Items", Tag.TAG_LIST)) {
              List<ItemStack> items = new ArrayList<>();
              ListTag list = record.getList("Items", Tag.TAG_COMPOUND);
              boolean valid = list.size() <= 128;
              for (int i = 0; i < Math.min(128, list.size()); i++) {
                ItemStack item = ItemStack.parseOptional(registries, list.getCompound(i));
                if (!item.isEmpty()) items.add(item);
                else valid = false;
              }
              // A removed item must not silently reroll today's remaining products.
              entry.rolled = valid ? List.copyOf(items) : List.of();
            }
            stock.entries.put(offer, entry);
          }
          shops.put(key, stock);
        }
        players.put(player, shops);
      } catch (RuntimeException exception) {
        com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn("Invalid shop stock {}", id);
      }
  }

  public CompoundTag save(HolderLookup.Provider registries) {
    CompoundTag saved = new CompoundTag();
    players.forEach(
        (id, shops) -> {
          CompoundTag shopTags = new CompoundTag();
          shops.forEach(
              (key, stock) -> {
                CompoundTag tag = new CompoundTag(), entries = new CompoundTag();
                tag.putLong("Cycle", stock.cycle);
                stock.entries.forEach(
                    (offer, entry) -> {
                      CompoundTag record = new CompoundTag();
                      record.putInt("Bought", entry.bought);
                      if (entry.rolled != null) {
                        ListTag items = new ListTag();
                        entry.rolled.forEach(item -> items.add(item.save(registries)));
                        record.put("Items", items);
                      }
                      entries.put(offer, record);
                    });
                tag.put("Entries", entries);
                shopTags.put(key, tag);
              });
          saved.put(id.toString(), shopTags);
        });
    return saved;
  }
}
