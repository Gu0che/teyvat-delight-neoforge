package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.JinxinFlowerBehavior;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildJinxinFlowerBlock extends BushBlock {
    public static final MapCodec<WildJinxinFlowerBlock> CODEC = simpleCodec(WildJinxinFlowerBlock::new);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public WildJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends WildJinxinFlowerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return canGrowOn(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!JinxinFlowerBehavior.isCoolingItem(stack)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide) {
            HarvestDrops.create(level, pos, state, player, stack, Method.COOLING)
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1).base(TeyvatDelight.JINXIN_FLOWER.get(), 1)
                    .seed(TeyvatDelight.JINXIN_FLOWER_BUD.get()).cropMora().drop();
            TeyvatCropDropTracker.skipNextDrop(level, pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            JinxinFlowerBehavior.playCoolingSound(level, pos);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            ItemStack tool
    ) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        TeyvatCropDropTracker.consumeSkipDrop(level, pos);
        if (!level.isClientSide && !player.isCreative()) {
            HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1)
                    .seed(TeyvatDelight.JINXIN_FLOWER_BUD.get()).drop();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            HarvestDrops.create(level, pos, state, null, ItemStack.EMPTY, Method.BREAK)
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1).drop();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    public static boolean canGrowOn(BlockState state) {
        return state.is(Blocks.RED_SAND)
                || state.is(Blocks.NETHERRACK)
                || state.is(Blocks.CRIMSON_NYLIUM)
                || state.is(Blocks.CRIMSON_ROOTS)
                || state.is(Blocks.CRIMSON_FUNGUS)
                || isTerracotta(state);
    }

    private static boolean isTerracotta(BlockState state) {
        return state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.WHITE_TERRACOTTA)
                || state.is(Blocks.ORANGE_TERRACOTTA)
                || state.is(Blocks.MAGENTA_TERRACOTTA)
                || state.is(Blocks.LIGHT_BLUE_TERRACOTTA)
                || state.is(Blocks.YELLOW_TERRACOTTA)
                || state.is(Blocks.LIME_TERRACOTTA)
                || state.is(Blocks.PINK_TERRACOTTA)
                || state.is(Blocks.GRAY_TERRACOTTA)
                || state.is(Blocks.LIGHT_GRAY_TERRACOTTA)
                || state.is(Blocks.CYAN_TERRACOTTA)
                || state.is(Blocks.PURPLE_TERRACOTTA)
                || state.is(Blocks.BLUE_TERRACOTTA)
                || state.is(Blocks.BROWN_TERRACOTTA)
                || state.is(Blocks.GREEN_TERRACOTTA)
                || state.is(Blocks.RED_TERRACOTTA)
                || state.is(Blocks.BLACK_TERRACOTTA);
    }
}
