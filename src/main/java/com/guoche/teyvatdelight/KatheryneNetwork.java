package com.guoche.teyvatdelight;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;

/**
 * @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork} for new
 *     integrations.
 */
@Deprecated
public final class KatheryneNetwork {
  private KatheryneNetwork() {}

  public static void register(IEventBus bus) {
    com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.register(bus);
  }

  public static void sendSnapshot(ServerPlayer player, KatheryneSnapshot snapshot) {
    com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.sendSnapshot(player, snapshot);
  }

  public static void sendAction(int menuId, int kind, int index) {
    com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.sendAction(menuId, kind, index);
  }

  public record Sync(KatheryneSnapshot snapshot) implements CustomPacketPayload {
    public static final Type<Sync> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("teyvatdelight", "katheryne_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Sync> CODEC =
        StreamCodec.of(
            (buf, message) ->
                com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.writeSnapshot(
                    buf, message.snapshot()),
            buf ->
                new Sync(
                    com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.readSnapshot(buf)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }

  public record Action(int menuId, int kind, int index, long revision, String key)
      implements CustomPacketPayload {
    public Action(int menuId, int kind, int index) {
      this(menuId, kind, index, -1, "");
    }

    public static final Type<Action> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("teyvatdelight", "katheryne_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Action> CODEC =
        StreamCodec.of(
            (buf, message) -> {
              buf.writeVarInt(message.menuId());
              buf.writeVarInt(message.kind());
              buf.writeVarInt(message.index());
              buf.writeLong(message.revision());
              buf.writeUtf(message.key(), 128);
            },
            buf ->
                new Action(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readLong(),
                    buf.readUtf(128)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }
}
