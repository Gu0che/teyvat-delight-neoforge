package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneData.DailyOffer;
import com.guoche.teyvatdelight.KatheryneData.Quest;
import com.guoche.teyvatdelight.KatheryneRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;

public class KatheryneData extends SavedData {
  public final CommissionBook commissions = new CommissionBook(this::setDirty);
  public final ShopBook stores = new ShopBook(this::setDirty);

  public CommissionBook.State commissionState(ServerPlayer player) {
    return commissions.state(
        player,
        quests.get(player.getUUID()),
        questHistory.getOrDefault(player.getUUID(), List.of()).stream()
            .map(ResourceLocation::toString)
            .toList());
  }

  private final Map<UUID, Quest> quests = new HashMap<>();
  private final Map<UUID, List<ResourceLocation>> questHistory = new HashMap<>();
  private final Map<UUID, ShopStock> shops = new HashMap<>();
  private final Map<UUID, DailyStock> dailyShops = new HashMap<>();

  public static KatheryneData get(MinecraftServer server) {
    return server
        .overworld()
        .getDataStorage()
        .computeIfAbsent(
            new Factory<>(com.guoche.teyvatdelight.KatheryneData::new, KatheryneData::load),
            "teyvatdelight_katheryne");
  }

  private static KatheryneData load(CompoundTag tag, HolderLookup.Provider registries) {
    KatheryneData data = new com.guoche.teyvatdelight.KatheryneData();
    data.commissions.load(tag.getCompound("Commissions"));
    data.stores.load(tag.getCompound("Stores"), registries);
    data.stores.loadTradeCounts(tag.getCompound("TradeCounts"));
    CompoundTag players = tag.getCompound("Players");
    for (String id : players.getAllKeys()) {
      try {
        CompoundTag entry = players.getCompound(id);
        UUID player = UUID.fromString(id);
        data.quests.put(
            player,
            new Quest(
                entry.getLong("Cycle"),
                ResourceLocation.parse(entry.getString("Dish")),
                entry.getBoolean("Completed")));
        ListTag seen = entry.getList("SeenDishes", Tag.TAG_STRING);
        List<ResourceLocation> history = new ArrayList<>();
        for (int index = 0; index < seen.size(); index++) {
          ResourceLocation dish = ResourceLocation.tryParse(seen.getString(index));
          if (dish != null) history.add(dish);
        }
        data.questHistory.put(player, history);
      } catch (IllegalArgumentException ignored) {
        // Ignore one malformed player record without discarding the others.
      }
    }
    CompoundTag savedShops = tag.getCompound("Shops");
    for (String id : savedShops.getAllKeys()) {
      try {
        CompoundTag entry = savedShops.getCompound(id);
        CompoundTag counts = entry.getCompound("Bought");
        Map<String, Integer> bought = new HashMap<>();
        for (String key : counts.getAllKeys()) bought.put(key, Math.max(0, counts.getInt(key)));
        data.shops.put(UUID.fromString(id), new ShopStock(entry.getLong("Cycle"), bought));
      } catch (IllegalArgumentException ignored) {
      }
    }
    CompoundTag savedDaily = tag.getCompound("DailyShop");
    for (String id : savedDaily.getAllKeys()) {
      try {
        CompoundTag entry = savedDaily.getCompound(id);
        CompoundTag slots = entry.getCompound("Slots");
        Map<String, DailyOffer> offers = new HashMap<>();
        for (String key : slots.getAllKeys()) {
          CompoundTag saved = slots.getCompound(key);
          List<ItemStack> items = new ArrayList<>();
          ListTag savedItems = saved.getList("Items", Tag.TAG_COMPOUND);
          for (int index = 0; index < savedItems.size(); index++) {
            ItemStack item = ItemStack.parseOptional(registries, savedItems.getCompound(index));
            if (!item.isEmpty()) items.add(item);
          }
          if (items.isEmpty()) {
            ItemStack legacy = ItemStack.parseOptional(registries, saved.getCompound("Item"));
            if (!legacy.isEmpty()) items.add(legacy);
          }
          if (!items.isEmpty())
            offers.put(key, new DailyOffer(List.copyOf(items), saved.getBoolean("Bought")));
        }
        data.dailyShops.put(UUID.fromString(id), new DailyStock(entry.getLong("Cycle"), offers));
      } catch (IllegalArgumentException ignored) {
      }
    }
    data.shops.forEach((id, stock) -> data.stores.importRegular(id, stock.cycle(), stock.bought()));
    data.dailyShops.forEach(
        (id, stock) -> data.stores.importDaily(id, stock.cycle(), stock.offers()));
    if (!data.shops.isEmpty() || !data.dailyShops.isEmpty()) data.setDirty();
    return data;
  }

  @Override
  public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
    tag.put("Commissions", commissions.save());
    tag.put("Stores", stores.save(registries));
    tag.put("TradeCounts", stores.saveTradeCounts());
    CompoundTag players = new CompoundTag();
    quests.forEach(
        (id, quest) -> {
          CompoundTag entry = new CompoundTag();
          entry.putLong("Cycle", quest.cycle());
          entry.putString("Dish", quest.dish().toString());
          entry.putBoolean("Completed", quest.completed());
          ListTag seen = new ListTag();
          questHistory
              .getOrDefault(id, List.of())
              .forEach(dish -> seen.add(StringTag.valueOf(dish.toString())));
          entry.put("SeenDishes", seen);
          players.put(id.toString(), entry);
        });
    tag.put("Players", players);
    CompoundTag savedShops = new CompoundTag();
    shops.forEach(
        (id, stock) -> {
          CompoundTag entry = new CompoundTag();
          entry.putLong("Cycle", stock.cycle());
          CompoundTag counts = new CompoundTag();
          stock.bought().forEach(counts::putInt);
          entry.put("Bought", counts);
          savedShops.put(id.toString(), entry);
        });
    tag.put("Shops", savedShops);
    CompoundTag savedDaily = new CompoundTag();
    dailyShops.forEach(
        (id, stock) -> {
          CompoundTag entry = new CompoundTag();
          entry.putLong("Cycle", stock.cycle());
          CompoundTag slots = new CompoundTag();
          stock
              .offers()
              .forEach(
                  (key, offer) -> {
                    CompoundTag saved = new CompoundTag();
                    ListTag items = new ListTag();
                    offer.items().forEach(item -> items.add(item.save(registries)));
                    saved.put("Items", items);
                    saved.putBoolean("Bought", offer.bought());
                    slots.put(key, saved);
                  });
          entry.put("Slots", slots);
          savedDaily.put(id.toString(), entry);
        });
    tag.put("DailyShop", savedDaily);
    return tag;
  }

  public Quest questFor(ServerPlayer player) {
    CommissionBook.State state = commissionState(player);
    CommissionBook.Instance first =
        state.quests.stream()
            .filter(q -> !q.done)
            .findFirst()
            .orElse(state.quests.isEmpty() ? null : state.quests.get(0));
    ResourceLocation target =
        first == null || !first.objectives.get(0).type.equals("item")
            ? BuiltInRegistries.ITEM.getKey(Items.AIR)
            : ResourceLocation.tryParse(first.objectives.get(0).target);
    return new Quest(state.cycle, target, first == null || first.done);
  }

  public boolean submit(ServerPlayer player) {
    CommissionBook.State state = commissionState(player);
    CommissionBook.Instance first =
        state.quests.stream().filter(q -> !q.done).findFirst().orElse(null);
    return first != null && commissions.submit(player, state, first.id).isEmpty();
  }

  public int remainingFor(ServerPlayer player, KatheryneRules.ShopOffer offer) {
    var shop = ShopDefinitions.get("shop");
    var definition = shop == null ? null : shop.offer(offer.key());
    return definition == null ? 0 : stores.remaining(player, shop, definition);
  }

  public void recordPurchase(ServerPlayer player, KatheryneRules.ShopOffer offer) {
    var shop = ShopDefinitions.get("shop");
    if (shop != null && shop.offer(offer.key()) != null)
      stores.record(player, shop, shop.offer(offer.key()));
  }

  public DailyOffer dailyOfferFor(ServerPlayer player, KatheryneRules.DailySlot slot) {
    var shop = ShopDefinitions.get("daily_shop");
    var offer = shop == null ? null : shop.offer(slot.key());
    if (offer == null) return new DailyOffer(List.of(), true);
    return new DailyOffer(
        stores.products(player, shop, offer).stream()
            .map(
                p -> {
                  ItemStack stack = p.icon().copy();
                  stack.setCount(p.count());
                  return stack;
                })
            .toList(),
        stores.remaining(player, shop, offer) == 0);
  }

  public void recordDailyPurchase(ServerPlayer player, KatheryneRules.DailySlot slot) {
    var shop = ShopDefinitions.get("daily_shop");
    if (shop != null && shop.offer(slot.key()) != null)
      stores.record(player, shop, shop.offer(slot.key()));
  }

  public static long shopCycle(MinecraftServer server) {
    return KatheryneRules.cycle(server, KatheryneRules.shopRefreshTime());
  }

  public static int secondsUntilRefresh(MinecraftServer server) {
    return KatheryneRules.questRefreshTime() == 0
        ? 0
        : KatheryneRules.secondsUntil(server, KatheryneRules.questRefreshTime());
  }

  public static void give(ServerPlayer player, Item item, int amount) {
    int remaining = amount;
    while (remaining > 0) {
      ItemStack stack = new ItemStack(item, Math.min(remaining, item.getDefaultMaxStackSize()));
      remaining -= stack.getCount();
      player.getInventory().add(stack);
      if (!stack.isEmpty()) player.drop(stack, false);
    }
  }

  private record ShopStock(long cycle, Map<String, Integer> bought) {}

  private record DailyStock(long cycle, Map<String, DailyOffer> offers) {}
}
