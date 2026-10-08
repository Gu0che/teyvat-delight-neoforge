package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.common.Tags;

public class WildMufengMushroomBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildMufengMushroomBlock> CODEC = simpleCodec(WildMufengMushroomBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final net.minecraft.tags.TagKey<Block> STRIPPED_WOOD = net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("teyvatdelight", "mufeng_stripped_wood"));

    public WildMufengMushroomBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.MUFENG_MUSHROOM,
                TeyvatDelight.MUFENG_MUSHROOM_SPORES,
                Surface.MUFENG_BUILDING
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends WildMufengMushroomBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockState state = this.defaultBlockState().setValue(FACING, facing);
        return isAllowedAttachmentDirection(facing) && state.canSurvive(context.getLevel(), context.getClickedPos())
                ? state
                : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return (isAllowedAttachmentDirection(facing) || isLegacyTopFacing(facing))
                && canAttachTo(level.getBlockState(getSupportPos(pos, facing)));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return canAttachTo(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public static boolean isAllowedAttachmentDirection(Direction direction) {
        return direction != Direction.DOWN;
    }

    private static boolean isLegacyTopFacing(Direction direction) {
        return direction == Direction.DOWN;
    }

    private static BlockPos getSupportPos(BlockPos pos, Direction facing) {
        return isLegacyTopFacing(facing) ? pos.below() : pos.relative(facing.getOpposite());
    }

    public static boolean canAttachTo(BlockState state) {
        return isStrippedWoodAttachment(state)
                || state.is(net.minecraft.tags.BlockTags.PLANKS)
                || state.is(Tags.Blocks.COBBLESTONES_NORMAL)
                || state.is(Tags.Blocks.COBBLESTONES_MOSSY)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Tags.Blocks.GLAZED_TERRACOTTAS)
                || isTerracotta(state);
    }

    public static boolean isStrippedWoodAttachment(BlockState state) {
        return state.is(STRIPPED_WOOD)
                || state.is(Tags.Blocks.STRIPPED_LOGS)
                || state.is(Tags.Blocks.STRIPPED_WOODS);
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
