package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildDandelionBlock} for new integrations. */
@Deprecated
public class WildDandelionBlock extends com.guoche.teyvatdelight.crop.WildDandelionBlock {
    public static final MapCodec<WildDandelionBlock> CODEC = simpleCodec(WildDandelionBlock::new);

    public WildDandelionBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildDandelionBlock> codec() {
        return CODEC;
    }
}
