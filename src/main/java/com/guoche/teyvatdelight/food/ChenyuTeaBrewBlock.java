package com.guoche.teyvatdelight.food;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChenyuTeaBrewBlock extends BushBlock {
    public static final MapCodec<ChenyuTeaBrewBlock> CODEC = simpleCodec(ChenyuTeaBrewBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 4, 13);

    public ChenyuTeaBrewBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
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
        return state.isFaceSturdy(level, pos, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(9) != 0) return;

        double x = 7.0 / 16.0;
        double z = 6.0 / 16.0;
        switch (state.getValue(FACING)) {
            case EAST -> {
                double oldX = x;
                x = 1.0 - z;
                z = oldX;
            }
            case SOUTH -> {
                x = 1.0 - x;
                z = 1.0 - z;
            }
            case WEST -> {
                double oldX = x;
                x = z;
                z = 1.0 - oldX;
            }
            default -> {
            }
        }
        level.addParticle(ParticleTypes.CLOUD,
                pos.getX() + x + (random.nextDouble() - 0.5) * 0.04,
                pos.getY() + 2.3 / 16.0,
                pos.getZ() + z + (random.nextDouble() - 0.5) * 0.04,
                (random.nextDouble() - 0.5) * 0.004,
                0.012,
                (random.nextDouble() - 0.5) * 0.004);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        drink(level, pos, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        drink(level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void drink(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || !player.canEat(false)) return;
        ItemStack serving = new ItemStack(TeyvatDelight.CHENYU_TEA_BREW.get());
        ItemStack container = serving.finishUsingItem(level, player);
        level.removeBlock(pos, false);
        if (!container.isEmpty()) popResource(level, pos, container);
    }
}
