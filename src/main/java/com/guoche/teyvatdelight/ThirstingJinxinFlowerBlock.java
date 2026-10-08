package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.ThirstingJinxinFlowerBlock} for new integrations. */
@Deprecated
public class ThirstingJinxinFlowerBlock extends com.guoche.teyvatdelight.crop.ThirstingJinxinFlowerBlock {
    public static final MapCodec<ThirstingJinxinFlowerBlock> CODEC = simpleCodec(ThirstingJinxinFlowerBlock::new);

    public ThirstingJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ThirstingJinxinFlowerBlock> codec() {
        return CODEC;
    }
}
