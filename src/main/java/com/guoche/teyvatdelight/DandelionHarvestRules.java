package com.guoche.teyvatdelight;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.DandelionHarvestRules} for new integrations. */
@Deprecated
public final class DandelionHarvestRules {
    private DandelionHarvestRules() {
    }

    public static boolean canHarvest(Player player, ItemStack tool) {
        return com.guoche.teyvatdelight.crop.DandelionHarvestRules.canHarvest(player, tool);
    }
}
