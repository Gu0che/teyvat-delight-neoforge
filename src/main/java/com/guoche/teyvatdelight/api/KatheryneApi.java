package com.guoche.teyvatdelight.api;

import com.guoche.teyvatdelight.entity.katheryne.KatheryneData;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Stable server-thread facade; never use internal menus or SavedData as an integration API.
 * Notifications run synchronously after successful settlement and cannot change its outcome.
 * Listener RuntimeExceptions are logged and isolated. Closing a subscription is idempotent.
 * Register listeners once during setup and close world-scoped subscriptions on teardown.
 */
public final class KatheryneApi {
  public record Completion(ServerPlayer player, String instanceId, String templateId) {}

  public record Purchase(ServerPlayer player, String offerId, boolean daily) {}

  public record ShopPurchase(ServerPlayer player, String shopId, String offerId) {}

  private static final List<Consumer<Completion>> COMPLETED = new CopyOnWriteArrayList<>();
  private static final List<Consumer<Purchase>> PURCHASED = new CopyOnWriteArrayList<>();
  private static final List<Consumer<ShopPurchase>> SHOP_PURCHASED = new CopyOnWriteArrayList<>();

  private KatheryneApi() {}

  public static AutoCloseable onCompleted(Consumer<Completion> listener) {
    return subscribe(COMPLETED, listener);
  }

  /** Lifetime successful settlements; reading never assigns or refreshes a commission. */
  public static long getCompletedCommissionCount(ServerPlayer player) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Commission counts require the server thread");
    return KatheryneData.get(player.server).commissions.completedCount(player);
  }

  /** Lifetime successful settlements of a stable commission template, without dispatching work. */
  public static long getCompletedCommissionCount(ServerPlayer player, String templateId) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Commission counts require the server thread");
    if (templateId == null || templateId.length() > 128
        || net.minecraft.resources.ResourceLocation.tryParse(templateId) == null)
      throw new IllegalArgumentException("Invalid commission template ID");
    return KatheryneData.get(player.server).commissions.completedCount(player, templateId);
  }

  /** Lifetime successful shop transactions, without creating stock or changing its cycle. */
  public static long getTradeCount(ServerPlayer player) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Trade counts require the server thread");
    return KatheryneData.get(player.server).stores.tradeCount(player);
  }

  /** Per stable ShopPurchase.shopId; removed shops retain their lifetime count. */
  public static long getTradeCount(ServerPlayer player, String shopId) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Trade counts require the server thread");
    if (shopId == null || !shopId.matches("[a-z0-9_:/.-]{1,128}"))
      throw new IllegalArgumentException("Invalid shop ID");
    return KatheryneData.get(player.server).stores.tradeCount(player, shopId);
  }

  /** Grant an entire configured advancement/task/challenge once, on the server thread. */
  public static boolean awardAdvancement(ServerPlayer player, String advancementId) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Advancement awards require the server thread");
    var id = advancementId == null ? null : net.minecraft.resources.ResourceLocation.tryParse(advancementId);
    var advancement = id == null ? null : player.server.getAdvancements().get(id);
    if (advancement == null) {
      com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn(
          "Unknown commission completion advancement {}", advancementId);
      return false;
    }
    var progress = player.getAdvancements().getOrStartProgress(advancement);
    for (String criterion : java.util.stream.StreamSupport.stream(
        progress.getRemainingCriteria().spliterator(), false).toList())
      player.getAdvancements().award(advancement, criterion);
    return true;
  }

  public static AutoCloseable onPurchased(Consumer<Purchase> listener) {
    return subscribe(PURCHASED, listener);
  }

  /** Setup-only legacy registration; requires a unique root id. Prefer shops data-pack files. */
  public static void registerShop(String definitionJson) {
    com.guoche.teyvatdelight.entity.katheryne.ShopDefinitions.register(
        Objects.requireNonNull(definitionJson, "definitionJson"));
  }

  public static AutoCloseable onShopPurchased(Consumer<ShopPurchase> listener) {
    return subscribe(SHOP_PURCHASED, listener);
  }

  /** Internal settlement dispatch, not a supported way to simulate purchases. */
  public static void shopPurchased(ServerPlayer player, String shopId, String offerId) {
    notify(SHOP_PURCHASED, new ShopPurchase(player, shopId, offerId));
    purchased(player, offerId, shopId.equals("daily_shop"));
  }

  /** Positive reports update event history and active goals; nonpositive amounts are ignored. */
  public static void progress(ServerPlayer player, String eventId, int amount) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Commission progress requires the server thread");
    checkEventId(eventId);
    KatheryneData data = KatheryneData.get(player.server);
    data.commissions.advance(player, "event", eventId, amount);
  }

  /** Lifetime event count, including reports before a commission was accepted. */
  public static long getEventCount(ServerPlayer player, String eventId) {
    if (!player.server.isSameThread())
      throw new IllegalStateException("Commission event counts require the server thread");
    checkEventId(eventId);
    return KatheryneData.get(player.server).commissions.eventCount(player, eventId);
  }

  /** Internal notification dispatch. This does not settle, count or reward a commission. */
  public static void completed(ServerPlayer player, String instance, String template) {
    notify(COMPLETED, new Completion(player, instance, template));
  }

  /** Internal legacy notification dispatch. Prefer onShopPurchased for new integrations. */
  public static void purchased(ServerPlayer player, String id, boolean daily) {
    notify(PURCHASED, new Purchase(player, id, daily));
  }

  private static void checkEventId(String id) {
    if (id == null || id.length() > 256
        || net.minecraft.resources.ResourceLocation.tryParse(id) == null)
      throw new IllegalArgumentException("Invalid commission event ID");
  }

  private static <T> AutoCloseable subscribe(List<Consumer<T>> listeners, Consumer<T> listener) {
    Objects.requireNonNull(listener, "listener");
    Consumer<T> subscription = new Consumer<>() {
      @Override public void accept(T event) { listener.accept(event); }
    };
    listeners.add(subscription);
    return () -> listeners.remove(subscription);
  }

  private static <T> void notify(List<Consumer<T>> listeners, T event) {
    for (Consumer<T> listener : listeners)
      try {
        listener.accept(event);
      } catch (RuntimeException e) {
        com.guoche.teyvatdelight.TeyvatDelight.LOGGER.error(
            "Katheryne integration listener failed", e);
      }
  }
}
