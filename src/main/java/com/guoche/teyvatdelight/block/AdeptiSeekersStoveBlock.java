package com.guoche.teyvatdelight.block;

import com.guoche.teyvatdelight.registry.ModBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

/** Reuses Farmer's Delight interactions, heat rules, drops, waterlogging and comparator output. */
public final class AdeptiSeekersStoveBlock extends CookingPotBlock {
    public static final MapCodec<AdeptiSeekersStoveBlock> CODEC = simpleCodec(AdeptiSeekersStoveBlock::new);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15.5, 16);

    public AdeptiSeekersStoveBlock(Properties properties) {
        // The collision box includes empty space beneath the open frame. It must
        // never act as an opaque cube and hide the ground or adjacent blocks.
        super(properties.noOcclusion());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdeptiSeekersStoveBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.ADEPTI_SEEKERS_STOVE.get(),
                level.isClientSide ? CookingPotBlockEntity::animationTick : CookingPotBlockEntity::cookingTick);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos) instanceof AdeptiSeekersStoveBlockEntity stove
                ? stove.getAsItem() : new ItemStack(this);
    }
}
