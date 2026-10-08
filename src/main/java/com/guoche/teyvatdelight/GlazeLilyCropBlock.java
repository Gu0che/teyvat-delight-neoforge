package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.GlazeLilyCropBlock} for new integrations. */
@Deprecated
public class GlazeLilyCropBlock extends com.guoche.teyvatdelight.crop.GlazeLilyCropBlock {
    public static final MapCodec<GlazeLilyCropBlock> CODEC = simpleCodec(GlazeLilyCropBlock::new);

    public GlazeLilyCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GlazeLilyCropBlock> codec() {
        return CODEC;
    }
}
