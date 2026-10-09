package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.guoche.teyvatdelight.KatheryneRules;
import com.guoche.teyvatdelight.KatheryneSnapshot;
import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.*;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;

public class KatheryneMenu extends AbstractContainerMenu {
  private static final Gson JSON = new Gson();
  private final ContainerData timer;
  private final Inventory inventory;
  private final ServerPlayer serverPlayer;
  private final KatheryneEntity katheryne;
  private KatheryneSnapshot snapshot;
  private long lastQuestCycle = Long.MIN_VALUE,
      lastShopCycle = Long.MIN_VALUE,
      lastRules = -1,
      frame;
  private String lastCommissions = "", feedback = "", activeStore = "";
  private int commissionOffset;
  private List<String> shopKeys = List.of(), dailyKeys = List.of();
  private List<ItemStack> previewInventory = List.of();
  private CommissionBook.Snapshot commissionView =
      new CommissionBook.Snapshot(List.of(), 4, 0, 4, 0, List.of(), "", 0);

  public CommissionBook.Snapshot commissionView() {
    return commissionView;
  }

  public String shopKey(int index) {
    return index >= 0 && index < shopKeys.size() ? shopKeys.get(index) : "";
  }

  public KatheryneMenu(int id, Inventory inventory) {
    this(id, inventory, null, null);
  }

  public KatheryneMenu(int id, Inventory inventory, ServerPlayer player, KatheryneEntity entity) {
    super(TeyvatDelight.KATHERYNE_MENU.get(), id);
    this.inventory = inventory;
    serverPlayer = player;
    katheryne = entity;
    snapshot = KatheryneSnapshot.empty(id);
    timer =
        player == null
            ? new SimpleContainerData(2)
            : new ContainerData() {
              public int get(int index) {
                return index == 0
                    ? KatheryneData.secondsUntilRefresh(player.server)
                    : KatheryneRules.secondsUntil(player.server, KatheryneRules.shopRefreshTime());
              }

              public void set(int index, int value) {}

              public int getCount() {
                return 2;
              }
            };
    addDataSlots(timer);
  }

  public KatheryneSnapshot snapshot() {
    return snapshot;
  }

  public void receive(KatheryneSnapshot value) {
    if (value.menuId() == containerId
        && value.revision() > snapshot.revision()
        && value.partial()
        && value.patchKind() == 5
        && (!value.stores().active().equals(snapshot.stores().active())
            || value.patchIndex() < 0
            || value.patchIndex() >= snapshot.stores().rows().size()
            || value.stores().rows().size() != 1
            || !value
                .stores()
                .rows()
                .get(0)
                .id()
                .equals(snapshot.stores().rows().get(value.patchIndex()).id()))) {
      KatheryneNetwork.sendAction(containerId, 4, 0, snapshot.revision(), "");
      return;
    }
    if (value.menuId() == containerId
        && value.partial()
        && (snapshot.revision() == 0 || value.revision() > snapshot.revision() + 1)) {
      KatheryneNetwork.sendAction(containerId, 4, 0, snapshot.revision(), "");
      return;
    }
    if (value.menuId() == containerId && value.revision() >= snapshot.revision()) {
      snapshot = value.merge(snapshot);
      commissionView = snapshot.commissionData();
      shopKeys = snapshot.shop().stream().map(KatheryneSnapshot.Sale::key).toList();
    }
  }

  public int getSecondsUntilRefresh() {
    return timer.get(0) & 0xffff;
  }

  public int getShopSecondsUntilRefresh() {
    return timer.get(1) & 0xffff;
  }

  public int getMoraBalance() {
    return countItem(TeyvatDelight.MORA.get());
  }

  public int getPrimogemBalance() {
    return countItem(TeyvatDelight.PRIMOGEM.get());
  }

  public int countItem(Item item) {
    int count = 0;
    for (int i = 0; i < inventory.getContainerSize(); i++)
      if (inventory.getItem(i).is(item)) count += inventory.getItem(i).getCount();
    return count;
  }

  private KatheryneData data() {
    return KatheryneData.get(serverPlayer.server);
  }

  private String commissions() {
    var d = data();
    var view = d.commissions.snapshot(serverPlayer, d.commissionState(serverPlayer), feedback,
        commissionOffset);
    commissionOffset = view.offset();
    commissionView = view;
    return JSON.toJson(view);
  }

  private List<KatheryneSnapshot.StoreHeader> headers() {
    return ShopDefinitions.shops().stream()
        .filter(s -> ShopDefinitions.available(serverPlayer, s) || s.lockedDisplay().equals("show"))
        .map(
            s ->
                new KatheryneSnapshot.StoreHeader(s.id(), s.title().isEmpty() ? s.id() : s.title(),
                    !ShopDefinitions.available(serverPlayer, s),
                    CommissionConditions.description(serverPlayer, s.conditions())))
        .toList();
  }

  private KatheryneSnapshot.StoreView storeView(boolean replace, int index) {
    var shop = ShopDefinitions.get(activeStore);
    List<KatheryneSnapshot.StoreRow> rows = new ArrayList<>();
    if (shop != null && CommissionConfig.runtimeValid) {
      if (replace)
        for (var offer : ShopDefinitions.visibleOffers(serverPlayer, shop)) rows.add(data().stores.row(serverPlayer, shop, offer));
      else if (index >= 0 && index < ShopDefinitions.visibleOffers(serverPlayer, shop).size())
        rows.add(data().stores.row(serverPlayer, shop, ShopDefinitions.visibleOffers(serverPlayer, shop).get(index)));
    }
    return new KatheryneSnapshot.StoreView(
        replace ? headers() : List.of(),
        activeStore,
        List.copyOf(rows),
        shop == null ? -1 : ShopDefinitions.seconds(serverPlayer.server, shop),
        replace);
  }

  public void sendSnapshot() {
    if (serverPlayer == null) return;
    if (!activeStore.isEmpty() && !ShopDefinitions.available(serverPlayer, ShopDefinitions.get(activeStore))) activeStore = "";
    lastConditions = conditionSignature();
    shopKeys = KatheryneRules.shopOffers().stream().map(o -> o.key()).toList();
    dailyKeys = KatheryneRules.dailySlots().stream().map(o -> o.key()).toList();
    lastRules = CommissionConfig.revision();
    lastQuestCycle =
        KatheryneRules.cycle(
            serverPlayer.server,
            KatheryneRules.questRefreshTime() == 0 ? 22000 : KatheryneRules.questRefreshTime());
    var shop = ShopDefinitions.get(activeStore);
    lastShopCycle =
        shop == null ? Long.MIN_VALUE : ShopDefinitions.cycle(serverPlayer.server, shop);
    send(false, 0, -1, true);
  }

  public void sendCommissionUpdate() {
    if (serverPlayer == null) return;
    if (lastRules != CommissionConfig.revision()) {
      sendSnapshot();
      return;
    }
    if (!lastConditions.equals(conditionSignature())) {
      sendSnapshot();
      return;
    }
    String current = commissions();
    if (!current.equals(lastCommissions) || inventoryChanged()) send(true, 0, -1, false);
  }

  private List<Boolean> lastConditions = List.of();

  private List<Boolean> conditionSignature() {
    List<Boolean> result = new ArrayList<>();
    for (var shop : ShopDefinitions.shops()) {
      if (!shop.conditions().isEmpty()) result.add(ShopDefinitions.available(serverPlayer, shop));
      for (var offer : shop.offers())
        if (!offer.conditions().isEmpty()) result.add(ShopDefinitions.available(serverPlayer, shop, offer));
    }
    return result;
  }

  private void send(boolean partial, int kind, int index, boolean replaceStore) {
    if (!CommissionConfig.runtimeValid) feedback = "gui.teyvatdelight.katheryne.invalid_rules";
    lastCommissions = commissions();
    previewInventory = inventorySnapshot();
    frame++;
    KatheryneNetwork.sendSnapshot(
        serverPlayer,
        new KatheryneSnapshot(
            containerId,
            ItemStack.EMPTY,
            false,
            KatheryneData.secondsUntilRefresh(serverPlayer.server),
            KatheryneRules.questRefreshTime() == 0,
            List.of(),
            List.of(),
            List.of(),
            lastCommissions,
            feedback,
            frame,
            partial,
            kind,
            index,
            storeView(replaceStore, kind == 5 ? index : -1)));
  }

  public void showNotice(String key) {
    feedback = key;
    sendCommissionUpdate();
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    if (serverPlayer == null) return;
    long q =
        KatheryneRules.cycle(
            serverPlayer.server,
            KatheryneRules.questRefreshTime() == 0 ? 22000 : KatheryneRules.questRefreshTime());
    var definition = ShopDefinitions.get(activeStore);
    long shop =
        definition == null
            ? Long.MIN_VALUE
            : ShopDefinitions.cycle(serverPlayer.server, definition);
    if (q != lastQuestCycle || shop != lastShopCycle || lastRules != CommissionConfig.revision()) {
      sendSnapshot();
      if (katheryne != null) katheryne.updateQuestVisual(serverPlayer);
    } else if (inventoryChanged()) {
      sendCommissionUpdate();
    }
  }

  public void handleAction(ServerPlayer p, int kind, int index, long revision, String key) {
    if (p != serverPlayer || p.containerMenu != this || !stillValid(p)) return;
    if (kind == 4) {
      sendSnapshot();
      return;
    }
    if (revision != frame || lastRules != CommissionConfig.revision()) {
      feedback = "gui.teyvatdelight.katheryne.stale";
      sendSnapshot();
      return;
    }
    if (kind == 6) {
      if (!key.isEmpty() && !ShopDefinitions.available(p, ShopDefinitions.get(key))) {
        feedback = "gui.teyvatdelight.katheryne.stale";
        sendSnapshot();
        return;
      }
      activeStore = key;
      feedback = "";
      sendSnapshot();
      return;
    }
    if (kind == 8) {
      commissionOffset = Math.max(0, index);
      feedback = "";
      send(true, 0, -1, false);
      return;
    }
    if (kind == 0 || kind == 7 || kind == 9) {
      var state = data().commissionState(p);
      index =
          java.util.stream.IntStream.range(0, state.quests.size())
              .filter(i -> state.quests.get(i).id.equals(key))
              .findFirst()
              .orElse(-1);
      if (index < 0) {
        feedback = "gui.teyvatdelight.katheryne.stale";
        sendSnapshot();
        return;
      }
      if (kind == 9) {
        feedback = data().commissions.viewInformation(p, state, key);
        send(true, 0, -1, false);
        return;
      }
      if (kind == 0 && !matchesSubmissionPreview(state.quests.get(index))) {
        feedback = "gui.teyvatdelight.katheryne.stale";
        sendSnapshot();
        return;
      }
    } else if (kind == 5) {
      var shop = ShopDefinitions.get(activeStore);
      var visible = shop == null ? List.<ShopDefinitions.Offer>of() : ShopDefinitions.visibleOffers(p, shop);
      if (shop == null
          || lastShopCycle != ShopDefinitions.cycle(p.server, shop)
          || index < 0
          || index >= visible.size()
          || !visible.get(index).id().equals(key)) {
        feedback = "gui.teyvatdelight.katheryne.stale";
        sendSnapshot();
        return;
      }
      index = shop.offers().indexOf(visible.get(index));
    } else if (kind == 1 || kind == 2) {
      var keys = kind == 1 ? shopKeys : dailyKeys;
      if (index < 0 || index >= keys.size() || !keys.get(index).equals(key)) {
        feedback = "gui.teyvatdelight.katheryne.stale";
        sendSnapshot();
        return;
      }
    } else if (kind != 3) return;
    handleAction(p, kind, index);
  }

  /** Legacy numeric actions remain source-compatible; the network uses stable active shop IDs. */
  public void handleAction(ServerPlayer player, int kind, int index) {
    if (player != serverPlayer || player.containerMenu != this || !stillValid(player)) return;
    if (!CommissionConfig.runtimeValid) {
      feedback = "gui.teyvatdelight.katheryne.invalid_rules";
      sendSnapshot();
      return;
    }
    feedback = "";
    List<ItemStack> before = inventorySnapshot();
    boolean success = false;
    try {
      var d = data();
      if (kind == 0 || kind == 7) {
        var state = d.commissionState(player);
        if (index < 0 || index >= state.quests.size())
          feedback = "gui.teyvatdelight.katheryne.stale";
        else feedback = kind == 7
            ? d.commissions.claimCommission(player, state, state.quests.get(index).id)
            : d.commissions.submit(player, state, state.quests.get(index).id);
        success = feedback.isEmpty();
        if (success && katheryne != null) {
          if (kind == 7) katheryne.playThanks();
          katheryne.updateQuestVisual(player);
        }
      } else if (kind == 3) {
        feedback = d.commissions.claim(player, d.commissionState(player));
        success = feedback.isEmpty();
      } else if (kind == 1 || kind == 2 || kind == 5) {
        var shop = ShopDefinitions.get(kind == 1 ? "shop" : kind == 2 ? "daily_shop" : activeStore);
        if (shop == null || index < 0 || index >= shop.offers().size()) return;
        feedback = d.stores.buy(player, shop, shop.offers().get(index));
        success = feedback.isEmpty();
      } else return;
    } catch (RuntimeException e) {
      TeyvatDelight.LOGGER.error("Katheryne settlement failed", e);
      feedback = "gui.teyvatdelight.katheryne.invalid_rules";
    }
    if (success) {
      syncInventory(player, before);
      if (kind > 0 && kind != 7 && katheryne != null) katheryne.playHappy();
    }
    if (lastRules != CommissionConfig.revision()) sendSnapshot();
    else if (kind == 1 || kind == 2 || kind == 5) sendSnapshot();
    else send(true, 0, -1, false);
  }

  private List<ItemStack> inventorySnapshot() {
    List<ItemStack> result = new ArrayList<>();
    for (int i = 0; i < inventory.getContainerSize(); i++) result.add(inventory.getItem(i).copy());
    return result;
  }

  private boolean inventoryChanged() {
    if (previewInventory.size() != inventory.getContainerSize()) return true;
    for (int i = 0; i < previewInventory.size(); i++)
      if (!ItemStack.matches(previewInventory.get(i), inventory.getItem(i))) return true;
    return false;
  }

  private boolean matchesSubmissionPreview(CommissionBook.Instance quest) {
    var shown = commissionView.quests().stream().filter(q -> q.id().equals(quest.id)).findFirst();
    var plan = CommissionItemPlan.create(inventory, quest.objectives);
    if (shown.isEmpty() || !plan.complete || !shown.get().payment().equals(plan.payment)) return false;
    for (var payment : plan.payment)
      if (payment.slot() >= previewInventory.size()
          || !ItemStack.matches(previewInventory.get(payment.slot()), inventory.getItem(payment.slot())))
        return false;
    return true;
  }

  private void syncInventory(ServerPlayer player, List<ItemStack> before) {
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!ItemStack.matches(stack, before.get(i)))
        player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, i, stack.copy()));
    }
  }

  @Override
  public ItemStack quickMoveStack(Player p, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player p) {
    return katheryne == null || katheryne.isAlive() && p.distanceToSqr(katheryne) <= 64;
  }
}
