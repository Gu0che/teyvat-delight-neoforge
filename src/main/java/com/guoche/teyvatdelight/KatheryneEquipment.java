package com.guoche.teyvatdelight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneEquipment} for new integrations. */
@Deprecated
public final class KatheryneEquipment {
    private KatheryneEquipment() {
    }

    public static ItemStack roll(ServerPlayer player, Item item) {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneEquipment.roll(player, item);
    }
}
