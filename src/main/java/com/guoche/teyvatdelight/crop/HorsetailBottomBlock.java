package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.HorsetailTopBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

public class HorsetailBottomBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<HorsetailBottomBlock> CODEC = simpleCodec(HorsetailBottomBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0),
            Block.box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0),
            Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
    };

    public HorsetailBottomBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public MapCodec<? extends HorsetailBottomBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1) || level.getRawBrightness(pos.above(), 0) < 9) {
            return;
        }

        if (CommonHooks.canCropGrow(level, pos, state, random.nextInt(7) == 0)) {
            this.growOneStep(level, pos, state);
            CommonHooks.fireCropGrowPost(level, pos, state);
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
        if (this.tryHarvestTop(level, pos, player)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.tryHarvestTop(level, pos, player)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
            BlockState topState = level.getBlockState(pos.above());
            if (topState.is(TeyvatDelight.HORSETAIL_TOP.get())) {
                TeyvatCropDropTracker.skipNextDrop(level, pos.above());
                TeyvatCropDropTracker.rememberState(level, pos, topState);
            }
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
        BlockState topState = TeyvatCropDropTracker.consumeRememberedState(level, pos)
                .orElseGet(() -> level.getBlockState(pos.above()));
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        TeyvatCropDropTracker.consumeSkipDrop(level, pos);
        if (!level.isClientSide) {
            if (topState.is(TeyvatDelight.HORSETAIL_TOP.get())) {
                TeyvatCropDropTracker.skipNextDrop(level, pos.above());
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 35);
                TeyvatCropDropTracker.consumeSkipDrop(level, pos.above());
            }

            if (!player.isCreative()) {
                dropWholePlantForBreak(level, pos, state, topState, player, tool);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            BlockState topState = level.getBlockState(pos.above());
            if (topState.is(TeyvatDelight.HORSETAIL_TOP.get())) {
                TeyvatCropDropTracker.skipNextDrop(level, pos.above());
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 35);
            }

            dropWholePlantForBreak(level, pos, state, topState, null, ItemStack.EMPTY);
        }

        super.onRemove(state, level, pos, newState, isMoving);
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
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < 3) {
            return true;
        }

        BlockState topState = level.getBlockState(pos.above());
        return topState.isAir() || (topState.is(TeyvatDelight.HORSETAIL_TOP.get()) && !isMatureTop(topState));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.growOneStep(level, pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(TeyvatDelight.HORSETAIL_SEEDS.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private void growOneStep(ServerLevel level, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
            return;
        }

        BlockPos topPos = pos.above();
        BlockState topState = level.getBlockState(topPos);
        if (topState.is(TeyvatDelight.HORSETAIL_TOP.get())) {
            TeyvatDelight.HORSETAIL_TOP.get().growOneStep(level, topPos, topState);
        } else if (topState.isAir()) {
            BlockState newTopState = TeyvatDelight.HORSETAIL_TOP.get().defaultBlockState();
            if (newTopState.canSurvive(level, topPos)) {
                level.setBlock(topPos, newTopState, 2);
            }
        }
    }

    private boolean tryHarvestTop(Level level, BlockPos pos, Player player) {
        BlockPos topPos = pos.above();
        BlockState topState = level.getBlockState(topPos);
        if (!isMatureTop(topState)) {
            return false;
        }

        TeyvatDelight.HORSETAIL_TOP.get().harvestAndReset(level, topPos, topState, player);
        return true;
    }

    private static void dropWholePlantForBreak(Level level, BlockPos pos, BlockState state,
            BlockState topState, @Nullable Player player, ItemStack tool) {
        var context = com.guoche.teyvatdelight.api.harvest.HarvestContext.capture(
                (ServerLevel) level, pos, state, player, tool, Method.BREAK).withMaturity(isMatureTop(topState));
        var drops = HarvestDrops.create(context).base(TeyvatDelight.HORSETAIL_SEEDS.get(), 1);
        if (isMatureTop(topState)) {
            int bonus = level.getBlockState(pos.below()).is(TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS) ? 1 : 0;
            drops.base(TeyvatDelight.HORSETAIL.get(), 1).field(TeyvatDelight.HORSETAIL.get(), bonus)
                    .seed(TeyvatDelight.HORSETAIL_SEEDS.get());
            if (player != null) drops.cropMora();
        }
        drops.drop();
    }

    private static boolean isMatureTop(BlockState state) {
        return state.is(TeyvatDelight.HORSETAIL_TOP.get())
                && state.getValue(HorsetailTopBlock.AGE) >= 2;
    }
}
