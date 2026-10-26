package com.guoche.teyvatdelight.network;

import com.guoche.teyvatdelight.item.PortableNutritionBagItem;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NutritionBagNetwork {
    public record Absorb(int menuId, int slot) implements CustomPacketPayload {
        public static final Type<Absorb> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "nutrition_bag_absorb"));
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

    private NutritionBagNetwork() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(NutritionBagNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Absorb.TYPE, Absorb.CODEC, (message, context) ->
                context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) handleAbsorb(player, message);
                }));
    }

    public static void sendAbsorb(int menuId, int slot) {
        PacketDistributor.sendToServer(new Absorb(menuId, slot));
    }

    public static void handleAbsorb(ServerPlayer player, Absorb message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != message.menuId() || message.slot() < 0 || message.slot() >= menu.slots.size()) return;
        Slot slot = menu.getSlot(message.slot());
        ItemStack bag = menu.getCarried();
        ItemStack food = slot.getItem();
        if (!slot.mayPickup(player) || !slot.mayPlace(food)
                || !bag.is(com.guoche.teyvatdelight.registry.ModItems.PORTABLE_NUTRITION_BAG.get()) || bag.getCount() != 1
                || food.isEmpty()) return;
        ItemStack remainder = PortableNutritionBagItem.containerFor(food, player);
        int count = food.getCount();
        if (!PortableNutritionBagItem.absorb(bag, food, player)) return;
        slot.set(ItemStack.EMPTY);
        slot.setChanged();
        menu.setCarried(bag.copy());
        if (!remainder.isEmpty()) giveContainers(player, remainder, count);
        menu.broadcastChanges();
        player.connection.send(new ClientboundContainerSetSlotPacket(-1, 0, -1, menu.getCarried().copy()));
        player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    private static void giveContainers(ServerPlayer player, ItemStack remainder, int count) {
        long remaining = (long) remainder.getCount() * count;
        while (remaining > 0) {
            ItemStack stack = remainder.copyWithCount((int) Math.min(remaining, remainder.getMaxStackSize()));
            remaining -= stack.getCount();
            player.getInventory().add(stack);
            if (!stack.isEmpty()) player.drop(stack, false);
        }
    }

    
}
