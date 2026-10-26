package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildRaspberryBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildRaspberryBlock> CODEC = simpleCodec(WildRaspberryBlock::new);

    public WildRaspberryBlock(BlockBehaviour.Properties properties) {
        super(properties, com.guoche.teyvatdelight.registry.ModItems.RASPBERRY, com.guoche.teyvatdelight.registry.ModItems.RASPBERRY, Surface.GRASS_OR_DIRT);
    }

    @Override
    protected MapCodec<? extends WildRaspberryBlock> codec() {
        return CODEC;
    }
}

