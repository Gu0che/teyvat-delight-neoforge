package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.harvest.CollectionTools;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildSeaGanodermaBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<WildSeaGanodermaBlock> CODEC = simpleCodec(WildSeaGanodermaBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public WildSeaGanodermaBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, true));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.SAND) || state.is(BlockTags.CORAL_BLOCKS);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        BlockState below = level.getBlockState(pos.below());
        return fluidState.is(FluidTags.WATER)
                && fluidState.getAmount() == 8
                && (below.is(BlockTags.SAND) || below.is(BlockTags.CORAL_BLOCKS));
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER));
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
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : Fluids.EMPTY.defaultFluidState();
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
        if (!level.isClientSide) {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 35);

            if (!player.isCreative()) {
                if (CollectionTools.isShears(tool)) {
                    HarvestDrops.create(level, pos, state, player, tool, Method.BREAK).base(this, 1).drop();
                    return;
                }
                HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                        .base(TeyvatDelight.SEA_GANODERMA.get(), 1)
                        .seed(TeyvatDelight.SEA_GANODERMA_SAMPLE.get()).cropMora().drop();
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            if (state.getValue(WATERLOGGED) && !newState.is(Blocks.WATER)) {
                level.setBlock(pos, Blocks.WATER.defaultBlockState(), 35);
            }

            HarvestDrops.create(level, pos, state, null, ItemStack.EMPTY, Method.BREAK)
                    .base(TeyvatDelight.SEA_GANODERMA.get(), 1).drop();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}
