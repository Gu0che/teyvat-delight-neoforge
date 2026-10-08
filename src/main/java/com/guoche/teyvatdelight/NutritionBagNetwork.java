package com.guoche.teyvatdelight;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

/** @deprecated Use {@link com.guoche.teyvatdelight.network.NutritionBagNetwork} for new integrations. */
@Deprecated
public final class NutritionBagNetwork {
    private NutritionBagNetwork() {
    }

    public static void register(IEventBus bus) {
        com.guoche.teyvatdelight.network.NutritionBagNetwork.register(bus);
    }

    public static void sendAbsorb(int menuId, int slot) {
        com.guoche.teyvatdelight.network.NutritionBagNetwork.sendAbsorb(menuId, slot);
    }

    public record Absorb(int menuId, int slot) implements CustomPacketPayload {
        public static final Type<Absorb> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
                TeyvatDelight.MODID, "nutrition_bag_absorb"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Absorb> CODEC = StreamCodec.of(
                (buf, message) -> {
                    buf.writeVarInt(message.menuId());
                    buf.writeVarInt(message.slot());
                }, buf -> new Absorb(buf.readVarInt(), buf.readVarInt()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
