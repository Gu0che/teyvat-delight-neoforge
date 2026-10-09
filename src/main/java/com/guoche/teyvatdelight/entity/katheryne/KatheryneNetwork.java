package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneClientNetwork;
import com.guoche.teyvatdelight.KatheryneMenu;
import com.guoche.teyvatdelight.KatheryneNetwork.Action;
import com.guoche.teyvatdelight.KatheryneNetwork.Sync;
import com.guoche.teyvatdelight.KatheryneSnapshot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class KatheryneNetwork {
  private KatheryneNetwork() {}

  public static void register(IEventBus bus) {
    bus.addListener(KatheryneNetwork::registerPayloads);
  }

  private static void registerPayloads(RegisterPayloadHandlersEvent event) {
    var registrar = event.registrar("16");
    registrar.playToClient(
        Sync.TYPE,
        Sync.CODEC,
        (message, context) ->
            context.enqueueWork(() -> KatheryneClientNetwork.receive(message.snapshot())));
    registrar.playToServer(
        Action.TYPE,
        Action.CODEC,
        (message, context) ->
            context.enqueueWork(
                () -> {
                  if (context.player() instanceof ServerPlayer player
                      && player.containerMenu instanceof KatheryneMenu menu
                      && menu.containerId == message.menuId()) {
                    menu.handleAction(
                        player, message.kind(), message.index(), message.revision(), message.key());
                  }
                }));
  }

  public static void sendSnapshot(ServerPlayer player, KatheryneSnapshot snapshot) {
    snapshot = boundedSnapshot(player, snapshot);
    PacketDistributor.sendToPlayer(player, new Sync(snapshot));
  }

  /** Preflight actual encoded stacks, including NBT supplied by other mods. */
  private static KatheryneSnapshot boundedSnapshot(ServerPlayer player, KatheryneSnapshot value) {
    var buffer =
        new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), player.registryAccess());
    try {
      writeSnapshot(buffer, value);
      if (buffer.writerIndex() <= 1000000) return value;
      com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn(
          "Katheryne snapshot exceeds safe packet budget: {} bytes", buffer.writerIndex());
    } catch (RuntimeException exception) {
      com.guoche.teyvatdelight.TeyvatDelight.LOGGER.warn("Invalid Katheryne snapshot", exception);
    } finally {
      buffer.release();
    }
    return new KatheryneSnapshot(
        value.menuId(),
        ItemStack.EMPTY,
        false,
        value.secondsUntilRefresh(),
        value.immediateRefresh(),
        List.of(),
        List.of(),
        List.of(),
        "",
        "gui.teyvatdelight.katheryne.invalid_rules",
        value.revision(),
        false,
        0,
        -1);
  }

  public static void sendAction(int menuId, int kind, int index) {
    com.guoche.teyvatdelight.client.katheryne.KatheryneClientNetwork.sendAction(
        menuId, kind, index);
  }

  public static void sendAction(int menuId, int kind, int index, long revision, String key) {
    PacketDistributor.sendToServer(new Action(menuId, kind, index, revision, key));
  }

  public static void writeSnapshot(RegistryFriendlyByteBuf buf, KatheryneSnapshot value) {
    buf.writeVarInt(value.menuId());
    buf.writeUtf(value.commissions(), 262144);
    buf.writeUtf(value.feedback(), 1024);
    buf.writeLong(value.revision());
    buf.writeBoolean(value.partial());
    buf.writeVarInt(value.patchKind());
    buf.writeVarInt(value.patchIndex());
    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, value.target());
    buf.writeBoolean(value.completed());
    buf.writeVarInt(value.secondsUntilRefresh());
    buf.writeBoolean(value.immediateRefresh());
    buf.writeVarInt(value.rewards().size());
    for (KatheryneSnapshot.StackAmount reward : value.rewards()) {
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, reward.icon());
      buf.writeVarInt(reward.count());
    }
    buf.writeVarInt(value.shop().size());
    for (KatheryneSnapshot.Sale sale : value.shop()) {
      buf.writeUtf(sale.key(), 128);
      buf.writeVarInt(sale.outputs().size());
      for (KatheryneSnapshot.StackAmount output : sale.outputs()) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, output.icon());
        buf.writeVarInt(output.count());
      }
      buf.writeVarInt(sale.prices().size());
      for (KatheryneSnapshot.StackAmount price : sale.prices()) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, price.icon());
        buf.writeVarInt(price.count());
      }
      buf.writeVarInt(sale.remaining());
    }
    buf.writeVarInt(value.daily().size());
    for (KatheryneSnapshot.DailySale sale : value.daily()) {
      buf.writeUtf(sale.key(), 128);
      buf.writeUtf(sale.name(), 256);
      buf.writeVarInt(sale.outputs().size());
      for (ItemStack output : sale.outputs()) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, output);
      buf.writeVarInt(sale.prices().size());
      for (KatheryneSnapshot.StackAmount price : sale.prices()) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, price.icon());
        buf.writeVarInt(price.count());
      }
      buf.writeBoolean(sale.purchased());
    }
    writeStores(buf, value.stores());
  }

  public static KatheryneSnapshot readSnapshot(RegistryFriendlyByteBuf buf) {
    int menuId = buf.readVarInt();
    String commissions = buf.readUtf(262144), feedback = buf.readUtf(1024);
    long revision = buf.readLong();
    boolean partial = buf.readBoolean();
    int patchKind = buf.readVarInt(), patchIndex = buf.readVarInt();
    ItemStack target = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    boolean completed = buf.readBoolean();
    int seconds = buf.readVarInt();
    boolean immediate = buf.readBoolean();
    List<KatheryneSnapshot.StackAmount> rewards = new ArrayList<>();
    for (int i = boundedSize(buf); i > 0; i--) {
      rewards.add(
          new KatheryneSnapshot.StackAmount(
              ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt()));
    }
    List<KatheryneSnapshot.Sale> shop = new ArrayList<>();
    for (int i = boundedSize(buf); i > 0; i--) {
      String saleKey = buf.readUtf(128);
      int outputCount = boundedSize(buf);
      if (outputCount == 0 || outputCount > 128)
        throw new IllegalArgumentException("Invalid Katheryne output count");
      List<KatheryneSnapshot.StackAmount> outputs = new ArrayList<>();
      for (int j = 0; j < outputCount; j++) {
        outputs.add(
            new KatheryneSnapshot.StackAmount(
                ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt()));
      }
      int priceCount = boundedSize(buf);
      if (priceCount == 0 || priceCount > 128)
        throw new IllegalArgumentException("Invalid Katheryne price count");
      List<KatheryneSnapshot.StackAmount> prices = new ArrayList<>();
      for (int j = 0; j < priceCount; j++) {
        prices.add(
            new KatheryneSnapshot.StackAmount(
                ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt()));
      }
      shop.add(new KatheryneSnapshot.Sale(outputs, prices, buf.readVarInt(), saleKey));
    }
    List<KatheryneSnapshot.DailySale> daily = new ArrayList<>();
    for (int i = boundedSize(buf); i > 0; i--) {
      String key = buf.readUtf(128);
      String name = buf.readUtf(256);
      int count = boundedSize(buf);
      if (count == 0 || count > 128)
        throw new IllegalArgumentException("Invalid Katheryne daily output count");
      List<ItemStack> outputs = new ArrayList<>();
      for (int j = 0; j < count; j++) outputs.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
      int priceCount = boundedSize(buf);
      if (priceCount == 0 || priceCount > 128)
        throw new IllegalArgumentException("Invalid Katheryne daily price count");
      List<KatheryneSnapshot.StackAmount> prices = new ArrayList<>();
      for (int j = 0; j < priceCount; j++) {
        prices.add(
            new KatheryneSnapshot.StackAmount(
                ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt()));
      }
      daily.add(new KatheryneSnapshot.DailySale(key, outputs, prices, buf.readBoolean(), name));
    }
    return new KatheryneSnapshot(
        menuId,
        target,
        completed,
        seconds,
        immediate,
        rewards,
        shop,
        daily,
        commissions,
        feedback,
        revision,
        partial,
        patchKind,
        patchIndex,
        readStores(buf));
  }

  private static void writeStores(RegistryFriendlyByteBuf buf, KatheryneSnapshot.StoreView view) {
    buf.writeVarInt(view.headers().size());
    for (var header : view.headers()) {
      buf.writeUtf(header.id(), 128);
      buf.writeUtf(header.title(), 256);
      buf.writeBoolean(header.locked());
      net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.encode(buf, header.lockReason());
    }
    buf.writeUtf(view.active(), 128);
    buf.writeVarInt(view.seconds());
    buf.writeBoolean(view.replaceRows());
    buf.writeVarInt(view.rows().size());
    for (var row : view.rows()) {
      buf.writeUtf(row.id(), 128);
      buf.writeUtf(row.name(), 256);
      buf.writeVarInt(row.remaining());
      buf.writeBoolean(row.locked());
      net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.encode(buf, row.lockReason());
      writeAmounts(buf, row.outputs());
      writeAmounts(buf, row.prices());
    }
  }

  private static void writeAmounts(
      RegistryFriendlyByteBuf buf, List<KatheryneSnapshot.StackAmount> stacks) {
    if (stacks.isEmpty() || stacks.size() > 128)
      throw new IllegalArgumentException("Invalid store stack count");
    buf.writeVarInt(stacks.size());
    for (var stack : stacks) {
      if (stack.icon().isEmpty() || stack.count() < 1 || stack.count() > 1000000)
        throw new IllegalArgumentException("Invalid store product/payment");
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack.icon());
      buf.writeVarInt(stack.count());
    }
  }

  private static List<KatheryneSnapshot.StackAmount> readAmounts(RegistryFriendlyByteBuf buf) {
    int count = boundedSize(buf);
    if (count < 1 || count > 128) throw new IllegalArgumentException("Invalid store stack count");
    List<KatheryneSnapshot.StackAmount> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ItemStack icon = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
      int amount = buf.readVarInt();
      if (icon.isEmpty() || amount < 1 || amount > 1000000)
        throw new IllegalArgumentException("Invalid store product/payment");
      result.add(new KatheryneSnapshot.StackAmount(icon, amount));
    }
    return List.copyOf(result);
  }

  private static KatheryneSnapshot.StoreView readStores(RegistryFriendlyByteBuf buf) {
    int count = boundedSize(buf);
    if (count > 64) throw new IllegalArgumentException("Too many shop tabs");
    List<KatheryneSnapshot.StoreHeader> headers = new ArrayList<>();
    java.util.Set<String> ids = new java.util.HashSet<>();
    for (int i = 0; i < count; i++) {
      String id = buf.readUtf(128), title = buf.readUtf(256);
      if (id.isEmpty() || !ids.add(id)) throw new IllegalArgumentException("Invalid shop ID");
      headers.add(new KatheryneSnapshot.StoreHeader(id, title, buf.readBoolean(),
          net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.decode(buf)));
    }
    String active = buf.readUtf(128);
    int seconds = buf.readVarInt();
    boolean replace = buf.readBoolean();
    List<KatheryneSnapshot.StoreRow> rows = new ArrayList<>();
    ids.clear();
    for (int i = boundedSize(buf); i > 0; i--) {
      String id = buf.readUtf(128), name = buf.readUtf(256);
      int remaining = buf.readVarInt();
      if (id.isEmpty() || !ids.add(id) || remaining < -1 || remaining > 1000000)
        throw new IllegalArgumentException("Invalid store row");
      rows.add(
          readStoreRow(buf, id, name, remaining));
    }
    return new KatheryneSnapshot.StoreView(
        List.copyOf(headers), active, List.copyOf(rows), seconds, replace);
  }

  private static KatheryneSnapshot.StoreRow readStoreRow(RegistryFriendlyByteBuf buf, String id, String name, int remaining) {
    boolean locked = buf.readBoolean();
    var reason = net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.decode(buf);
    return new KatheryneSnapshot.StoreRow(id, name, readAmounts(buf), readAmounts(buf), remaining, locked, reason);
  }

  private static int boundedSize(RegistryFriendlyByteBuf buf) {
    int size = buf.readVarInt();
    if (size < 0 || size > 1024)
      throw new IllegalArgumentException("Katheryne list exceeds 1024 entries");
    return size;
  }
}
