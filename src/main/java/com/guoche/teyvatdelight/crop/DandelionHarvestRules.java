package com.guoche.teyvatdelight.crop;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DandelionHarvestRules {
    private DandelionHarvestRules() {
    }

    public static boolean canHarvest(Player player, ItemStack tool) {
        return player.isShiftKeyDown();
    }
}

