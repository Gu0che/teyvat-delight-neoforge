package com.guoche.teyvatdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.TeyvatBonusDrops} for new integrations. */
@Deprecated
public final class TeyvatBonusDrops {
    private TeyvatBonusDrops() {
    }

    public static void dropMoraFromCrop(Level level, BlockPos pos, Player player, ItemStack tool) {
        com.guoche.teyvatdelight.crop.TeyvatBonusDrops.dropMoraFromCrop(level, pos, player, tool);
    }
}
