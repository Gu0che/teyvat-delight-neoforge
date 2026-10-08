package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.block.TeyvatFieldBlock} for new integrations. */
@Deprecated
public class TeyvatFieldBlock extends com.guoche.teyvatdelight.block.TeyvatFieldBlock {
    public static final MapCodec<TeyvatFieldBlock> CODEC = simpleCodec(TeyvatFieldBlock::new);

    public TeyvatFieldBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<TeyvatFieldBlock> codec() {
        return CODEC;
    }
}
