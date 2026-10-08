package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.BlazingJinxinFlowerBlock} for new integrations. */
@Deprecated
public class BlazingJinxinFlowerBlock extends com.guoche.teyvatdelight.crop.BlazingJinxinFlowerBlock {
    public static final MapCodec<BlazingJinxinFlowerBlock> CODEC = simpleCodec(BlazingJinxinFlowerBlock::new);

    public BlazingJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BlazingJinxinFlowerBlock> codec() {
        return CODEC;
    }
}
