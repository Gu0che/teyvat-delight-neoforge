package com.guoche.teyvatdelight;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record KatheryneSnapshot(
    int menuId,
    ItemStack target,
    boolean completed,
    int secondsUntilRefresh,
    boolean immediateRefresh,
    List<StackAmount> rewards,
    List<Sale> shop,
    List<DailySale> daily,
    String commissions,
    String feedback,
    long revision,
    boolean partial,
    int patchKind,
    int patchIndex,
    StoreView stores) {
  public KatheryneSnapshot(
      int menuId,
      ItemStack target,
      boolean completed,
      int secondsUntilRefresh,
      boolean immediateRefresh,
      List<StackAmount> rewards,
      List<Sale> shop,
      List<DailySale> daily,
      String commissions,
      String feedback,
      long revision,
      boolean partial,
      int patchKind,
      int patchIndex) {
    this(
        menuId,
        target,
        completed,
        secondsUntilRefresh,
        immediateRefresh,
        rewards,
        shop,
        daily,
        commissions,
        feedback,
        revision,
        partial,
        patchKind,
        patchIndex,
        StoreView.empty());
  }

  public KatheryneSnapshot(
      int menuId,
      ItemStack target,
      boolean completed,
      int secondsUntilRefresh,
      boolean immediateRefresh,
      List<StackAmount> rewards,
      List<Sale> shop,
      List<DailySale> daily) {
    this(
        menuId,
        target,
        completed,
        secondsUntilRefresh,
        immediateRefresh,
        rewards,
        shop,
        daily,
        "",
        "",
        0,
        false,
        0,
        -1,
        StoreView.empty());
  }

  public com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Snapshot commissionData() {
    return commissions.isEmpty()
        ? new com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Snapshot(
            List.of(), 4, 0, 4, 0, List.of(), "", 0)
        : new com.google.gson.Gson()
            .fromJson(
                commissions,
                com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Snapshot.class);
  }

  public KatheryneSnapshot merge(KatheryneSnapshot previous) {
    if (!partial) return this;
    java.util.ArrayList<Sale> shops = new java.util.ArrayList<>(previous.shop());
    java.util.ArrayList<DailySale> dailies = new java.util.ArrayList<>(previous.daily());
    if (patchKind == 1 && patchIndex >= 0 && patchIndex < shops.size() && shop.size() == 1)
      shops.set(patchIndex, shop.get(0));
    if (patchKind == 2 && patchIndex >= 0 && patchIndex < dailies.size() && daily.size() == 1)
      dailies.set(patchIndex, daily.get(0));
    return new KatheryneSnapshot(
        menuId,
        target,
        completed,
        secondsUntilRefresh,
        immediateRefresh,
        rewards,
        List.copyOf(shops),
        List.copyOf(dailies),
        commissions,
        feedback,
        revision,
        false,
        0,
        -1,
        stores.merge(previous.stores(), patchKind, patchIndex));
  }

  public static KatheryneSnapshot empty(int menuId) {
    return new KatheryneSnapshot(
        menuId, ItemStack.EMPTY, false, 0, false, List.of(), List.of(), List.of());
  }

  public record StackAmount(ItemStack icon, int count) {}

  public record StoreHeader(String id, String title, boolean locked,
      net.minecraft.network.chat.Component lockReason) {
    public StoreHeader(String id, String title) {
      this(id, title, false, net.minecraft.network.chat.Component.empty());
    }
  }

  public record StoreRow(
      String id, String name, List<StackAmount> outputs, List<StackAmount> prices, int remaining,
      boolean locked, net.minecraft.network.chat.Component lockReason) {
    public StoreRow(String id, String name, List<StackAmount> outputs, List<StackAmount> prices, int remaining) {
      this(id, name, outputs, prices, remaining, false, net.minecraft.network.chat.Component.empty());
    }
  }

  public record StoreView(
      List<StoreHeader> headers,
      String active,
      List<StoreRow> rows,
      int seconds,
      boolean replaceRows) {
    public static StoreView empty() {
      return new StoreView(List.of(), "", List.of(), -1, false);
    }

    private StoreView merge(StoreView previous, int kind, int index) {
      List<StoreHeader> names = headers.isEmpty() ? previous.headers() : headers;
      if (replaceRows) return new StoreView(headers, active, rows, seconds, false);
      List<StoreRow> merged = new java.util.ArrayList<>(previous.rows());
      if (kind == 5
          && active.equals(previous.active())
          && index >= 0
          && index < merged.size()
          && rows.size() == 1) merged.set(index, rows.get(0));
      return new StoreView(names, previous.active(), List.copyOf(merged), seconds, false);
    }
  }

  public record Sale(
      List<StackAmount> outputs, List<StackAmount> prices, int remaining, String key) {
    public Sale(List<StackAmount> outputs, List<StackAmount> prices, int remaining) {
      this(outputs, prices, remaining, "");
    }
  }

  public record DailySale(
      String key,
      List<ItemStack> outputs,
      List<StackAmount> prices,
      boolean purchased,
      String name) {
    public DailySale(
        String key, List<ItemStack> outputs, List<StackAmount> prices, boolean purchased) {
      this(key, outputs, prices, purchased, "");
    }
  }
}
