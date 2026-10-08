package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.block.TeyvatWaterFieldBlock} for new integrations. */
@Deprecated
public class TeyvatWaterFieldBlock extends com.guoche.teyvatdelight.block.TeyvatWaterFieldBlock {
    public static final MapCodec<TeyvatWaterFieldBlock> CODEC = simpleCodec(TeyvatWaterFieldBlock::new);

    public TeyvatWaterFieldBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<TeyvatWaterFieldBlock> codec() {
        return CODEC;
    }
}
