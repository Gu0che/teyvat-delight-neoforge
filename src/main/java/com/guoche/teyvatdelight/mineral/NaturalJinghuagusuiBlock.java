package com.guoche.teyvatdelight.mineral;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.harvest.CollectionTools;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NaturalJinghuagusuiBlock extends BushBlock {
    public static final MapCodec<NaturalJinghuagusuiBlock> CODEC = simpleCodec(NaturalJinghuagusuiBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE = Block.box(3.0, 3.0, 3.0, 13.0, 13.0, 13.0);

    public NaturalJinghuagusuiBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
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
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return NaturalMineralPlacement.hasFullSupport(level, supportPos, facing);
    }

    public boolean canGenerateAt(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).is(Blocks.AMETHYST_BLOCK);
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
            else drops.base(TeyvatDelight.JINGHUAGUSUI.get(), 1).mineralMora();
            drops.drop();
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
