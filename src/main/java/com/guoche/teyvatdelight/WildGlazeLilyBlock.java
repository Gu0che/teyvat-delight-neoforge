package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildGlazeLilyBlock} for new integrations. */
@Deprecated
public class WildGlazeLilyBlock extends com.guoche.teyvatdelight.crop.WildGlazeLilyBlock {
    public static final MapCodec<WildGlazeLilyBlock> CODEC = simpleCodec(WildGlazeLilyBlock::new);

    public WildGlazeLilyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildGlazeLilyBlock> codec() {
        return CODEC;
    }
}
