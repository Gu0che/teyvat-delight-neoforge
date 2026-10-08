package com.guoche.teyvatdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.JinxinFlowerBehavior} for new integrations. */
@Deprecated
public final class JinxinFlowerBehavior {
    private JinxinFlowerBehavior() {
    }

    public static final int NATURAL_GROWTH_STEPS = com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.NATURAL_GROWTH_STEPS;

    public static boolean canGrowOn(BlockState state) {
        return com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.canGrowOn(state);
    }

    public static boolean isOnPreferredField(LevelReader level, BlockPos pos) {
        return com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.isOnPreferredField(level, pos);
    }

    public static boolean isCoolingItem(ItemStack stack) {
        return com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.isCoolingItem(stack);
    }

    public static int getFuelBurnTime(ItemStack stack) {
        return com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.getFuelBurnTime(stack);
    }

    public static int getFuelGrowthSteps(int burnTime) {
        return com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.getFuelGrowthSteps(burnTime);
    }

    public static void consumeFuel(Player player, InteractionHand hand, ItemStack stack) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.consumeFuel(player, hand, stack);
    }

    public static void dropBud(Level level, BlockPos pos) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.dropBud(level, pos);
    }

    public static void dropHarvest(Level level, BlockPos pos) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.dropHarvest(level, pos);
    }

    public static void playFuelSound(Level level, BlockPos pos) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.playFuelSound(level, pos);
    }

    public static void playBurntOutSound(Level level, BlockPos pos) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.playBurntOutSound(level, pos);
    }

    public static void playCoolingSound(Level level, BlockPos pos) {
        com.guoche.teyvatdelight.crop.JinxinFlowerBehavior.playCoolingSound(level, pos);
    }
}
