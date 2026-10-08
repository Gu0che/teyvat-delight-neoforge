package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.TeyvatDelight;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildHorsetailBlock extends DoublePlantBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public WildHorsetailBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, true));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockState belowState = level.getBlockState(pos.below());
            return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }

        FluidState fluidState = level.getFluidState(pos);
        return fluidState.is(FluidTags.WATER)
                && fluidState.getAmount() == 8
                && canGrowOn(level.getBlockState(pos.below()));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return canGrowOn(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        if (pos.getY() >= context.getLevel().getMaxBuildHeight() - 1
                || !fluidState.is(FluidTags.WATER)
                || fluidState.getAmount() != 8
                || !context.getLevel().getBlockState(pos.above()).isAir()) {
            return null;
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.defaultBlockState()
                .setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, false), 3);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            if ((direction == Direction.UP && !isMatchingHalf(neighborState, DoubleBlockHalf.UPPER))
                    || (direction == Direction.DOWN && !state.canSurvive(level, pos))) {
                return Blocks.WATER.defaultBlockState();
            }
        } else if (direction == Direction.DOWN && !isMatchingHalf(neighborState, DoubleBlockHalf.LOWER)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? Fluids.WATER.getSource(false)
                : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canPlaceLiquid(Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            TeyvatCropDropTracker.skipNextDrop(level, lowerPos);
            TeyvatCropDropTracker.skipNextDrop(level, lowerPos.above());
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
        if (!level.isClientSide) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = lowerPos.above();
            TeyvatCropDropTracker.consumeSkipDrop(level, lowerPos);
            TeyvatCropDropTracker.consumeSkipDrop(level, upperPos);
            TeyvatCropDropTracker.skipNextDrop(level, lowerPos);
            TeyvatCropDropTracker.skipNextDrop(level, upperPos);
            level.setBlock(lowerPos, Blocks.WATER.defaultBlockState(), 35);
            level.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 35);
            TeyvatCropDropTracker.consumeSkipDrop(level, lowerPos);
            TeyvatCropDropTracker.consumeSkipDrop(level, upperPos);

            if (!player.isCreative()) {
                HarvestDrops.create(level, lowerPos, state.setValue(HALF, DoubleBlockHalf.LOWER),
                        player, tool, Method.BREAK, true).base(TeyvatDelight.HORSETAIL.get(), 1)
                        .seed(TeyvatDelight.HORSETAIL_SEEDS.get()).cropMora().drop();
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = lowerPos.above();
            TeyvatCropDropTracker.skipNextDrop(level, lowerPos);
            TeyvatCropDropTracker.skipNextDrop(level, upperPos);
            level.setBlock(lowerPos, Blocks.WATER.defaultBlockState(), 35);
            level.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 35);
            HarvestDrops.create(level, lowerPos, state.setValue(HALF, DoubleBlockHalf.LOWER),
                    null, ItemStack.EMPTY, Method.BREAK, true).base(TeyvatDelight.HORSETAIL.get(), 1).drop();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    public static boolean canGrowOn(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND);
    }

    private static boolean isMatchingHalf(BlockState state, DoubleBlockHalf half) {
        return state.is(TeyvatDelight.WILD_HORSETAIL.get()) && state.getValue(HALF) == half;
    }
}
