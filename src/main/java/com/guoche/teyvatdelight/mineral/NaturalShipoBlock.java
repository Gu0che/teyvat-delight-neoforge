package com.guoche.teyvatdelight.mineral;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.harvest.CollectionTools;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NaturalShipoBlock extends BushBlock {
    public static final MapCodec<NaturalShipoBlock> CODEC = simpleCodec(NaturalShipoBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 1.0, 16.0, 8.0, 16.0);
    private static final java.util.Map<Direction, VoxelShape> SHAPES = java.util.Map.of(
            Direction.UP, SHAPE,
            Direction.DOWN, Block.box(0, 8, 0, 16, 16, 15),
            Direction.NORTH, Block.box(0, 0, 8, 16, 15, 16),
            Direction.SOUTH, Block.box(0, 0, 0, 16, 15, 8),
            Direction.WEST, Block.box(8, 0, 0, 16, 15, 16),
            Direction.EAST, Block.box(0, 0, 0, 8, 15, 16));
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private final Supplier<? extends ItemLike> mineralItem;
    private final TagKey<Block> surfaceTag;

    public NaturalShipoBlock(BlockBehaviour.Properties properties) {
        this(properties, TeyvatDelight.SHIPO, TeyvatTags.Blocks.NATURAL_SHIPO_SURFACES);
    }

    public NaturalShipoBlock(
            BlockBehaviour.Properties properties,
            Supplier<? extends ItemLike> mineralItem,
            TagKey<Block> surfaceTag
    ) {
        super(properties);
        this.mineralItem = mineralItem;
        this.surfaceTag = surfaceTag;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return NaturalMineralPlacement.hasFullSupport(level, pos.relative(facing.getOpposite()), facing);
    }

    public boolean canGenerateAt(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING) == Direction.UP && level.getBlockState(pos.below()).is(this.surfaceTag);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(this.surfaceTag);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
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
        if (!level.isClientSide && !player.isCreative() && CollectionTools.isPickaxe(tool)) {
            var drops = HarvestDrops.create(level, pos, state, player, tool, Method.BREAK);
            if (CollectionTools.hasSilkTouch(level, tool)) drops.base(this, 1);
            else drops.base(this.mineralItem.get(), 1).mineralMora();
            drops.drop();
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
