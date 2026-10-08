package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.JinxinFlowerBehavior;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

public class BlazingJinxinFlowerBlock extends BushBlock {
    public static final MapCodec<BlazingJinxinFlowerBlock> CODEC = simpleCodec(BlazingJinxinFlowerBlock::new);
    public static final int MAX_AGE = 112;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE - 1);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public BlazingJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BlazingJinxinFlowerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return JinxinFlowerBehavior.canGrowOn(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1) || level.getRawBrightness(pos, 0) < 9) {
            return;
        }

        if (state.getValue(AGE) >= MAX_AGE) {
            becomeBurntOut(level, pos);
            return;
        }

        if (CommonHooks.canCropGrow(level, pos, state, random.nextInt(7) == 0)) {
            advance(level, pos, state, JinxinFlowerBehavior.NATURAL_GROWTH_STEPS);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(8) == 0) {
            level.playLocalSound(
                    (double)pos.getX() + 0.5,
                    (double)pos.getY() + 0.5,
                    (double)pos.getZ() + 0.5,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.45F,
                    0.8F + random.nextFloat() * 0.4F,
                    false
            );
        }
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
        int burnTime = JinxinFlowerBehavior.getFuelBurnTime(stack);
        if (burnTime <= 0) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide) {
            JinxinFlowerBehavior.consumeFuel(player, hand, stack);
            JinxinFlowerBehavior.playFuelSound(level, pos);
            advance(level, pos, state, JinxinFlowerBehavior.getFuelGrowthSteps(burnTime));
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(TeyvatDelight.JINXIN_FLOWER_BUD.get());
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
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1).drop();
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private static void advance(ServerLevel level, BlockPos pos, BlockState state, int steps) {
        int age = state.getValue(AGE);
        int nextAge = age + steps;
        if (nextAge >= MAX_AGE) {
            becomeBurntOut(level, pos);
        } else {
            level.setBlock(pos, state.setValue(AGE, nextAge), 2);
        }
    }

    private static void advance(Level level, BlockPos pos, BlockState state, int steps) {
        int age = state.getValue(AGE);
        int nextAge = age + steps;
        if (nextAge >= MAX_AGE) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
            level.setBlock(pos, TeyvatDelight.BURNT_OUT_JINXIN_FLOWER.get().defaultBlockState(), 2);
            JinxinFlowerBehavior.playBurntOutSound(level, pos);
        } else {
            level.setBlock(pos, state.setValue(AGE, nextAge), 2);
        }
    }

    private static void becomeBurntOut(ServerLevel level, BlockPos pos) {
        TeyvatCropDropTracker.skipNextDrop(level, pos);
        level.setBlock(pos, TeyvatDelight.BURNT_OUT_JINXIN_FLOWER.get().defaultBlockState(), 2);
        JinxinFlowerBehavior.playBurntOutSound(level, pos);
    }
}
