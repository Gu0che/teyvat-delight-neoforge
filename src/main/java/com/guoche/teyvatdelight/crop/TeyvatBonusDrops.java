package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class TeyvatBonusDrops {
    private static final float MORA_CHANCE = 0.15F;

    private TeyvatBonusDrops() {
    }

    public static void dropMoraFromCrop(Level level, BlockPos pos, Player player, ItemStack tool) {
        dropWithFortune(level, pos, player, tool, new ItemStack(TeyvatDelight.MORA.get()), MORA_CHANCE);
    }

    /** Roll once while assembling a harvest, before any result modifiers. */
    public static boolean rollMora(Level level, Player player, ItemStack tool) {
        if (level.isClientSide || player.isCreative()) return false;
        float chance = Math.min(1.0F, MORA_CHANCE * (getFortuneLevel(level, tool) + 1));
        return level.random.nextFloat() < chance;
    }

    private static void dropWithFortune(Level level, BlockPos pos, Player player, ItemStack tool, ItemStack stack, float baseChance) {
        if (level.isClientSide || player.isCreative()) {
            return;
        }

        float chance = Math.min(1.0F, baseChance * (getFortuneLevel(level, tool) + 1));
        if (level.random.nextFloat() < chance) {
            Block.popResource(level, pos, stack);
        }
    }

    private static int getFortuneLevel(Level level, ItemStack tool) {
        if (tool.isEmpty()) {
            return 0;
        }

        return EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.FORTUNE),
                tool
        );
    }
}
