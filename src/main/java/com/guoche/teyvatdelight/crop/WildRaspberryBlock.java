package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildRaspberryBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildRaspberryBlock> CODEC = simpleCodec(WildRaspberryBlock::new);

    public WildRaspberryBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatDelight.RASPBERRY, TeyvatDelight.RASPBERRY, Surface.GRASS_OR_DIRT);
    }

    @Override
    protected MapCodec<? extends WildRaspberryBlock> codec() {
        return CODEC;
    }
}

