package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.guoche.teyvatdelight.crop.WolfhookCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WildWolfhookBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildWolfhookBlock> CODEC = simpleCodec(WildWolfhookBlock::new);

    public WildWolfhookBlock(BlockBehaviour.Properties properties) {
        super(properties, com.guoche.teyvatdelight.registry.ModItems.WOLFHOOK, com.guoche.teyvatdelight.registry.ModItems.WOLFHOOK, Surface.GRASS_OR_DIRT);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        WolfhookCropBlock.hurtMovingEntity(state, level, entity);
    }

    @Override
    protected MapCodec<? extends WildWolfhookBlock> codec() {
        return CODEC;
    }
}
