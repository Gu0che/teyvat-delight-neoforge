package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class JinxinFlowerBehavior {
    private static final int BURN_TIME_PER_GROWTH_STEP = 100;
    public static final int NATURAL_GROWTH_STEPS = 16;

    private JinxinFlowerBehavior() {
    }

    public static boolean canGrowOn(BlockState state) {
        return state.is(TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS);
    }

    public static boolean isOnPreferredField(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS);
    }

    public static boolean isCoolingItem(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET)
                || stack.is(Items.ICE)
                || stack.is(Items.PACKED_ICE)
                || stack.is(Items.BLUE_ICE);
    }

    public static int getFuelBurnTime(ItemStack stack) {
        return stack.isEmpty() ? 0 : stack.getBurnTime(RecipeType.SMELTING);
    }

    public static int getFuelGrowthSteps(int burnTime) {
        if (burnTime <= 0) {
            return 0;
        }
        return burnTime / BURN_TIME_PER_GROWTH_STEP;
    }

    public static void consumeFuel(Player player, InteractionHand hand, ItemStack stack) {
        if (player.getAbilities().instabuild) {
            return;
        }

        ItemStack remainder = stack.getCraftingRemainingItem();
        stack.shrink(1);

        if (remainder.isEmpty()) {
            return;
        }

        if (stack.isEmpty()) {
            player.setItemInHand(hand, remainder);
        } else if (!player.getInventory().add(remainder)) {
            player.drop(remainder, false);
        }
    }

    public static void dropBud(Level level, BlockPos pos) {
        Block.popResource(level, pos, new ItemStack(TeyvatDelight.JINXIN_FLOWER_BUD.get()));
    }

    public static void dropHarvest(Level level, BlockPos pos) {
        int count = 1 + (isOnPreferredField(level, pos) ? 1 : 0);
        Block.popResource(level, pos, new ItemStack(TeyvatDelight.JINXIN_FLOWER.get(), count));
    }

    public static void playFuelSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void playBurntOutSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    public static void playCoolingSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1.0F);
    }
}
