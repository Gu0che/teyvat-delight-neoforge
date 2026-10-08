package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.RaspberryCropBlock} for new integrations. */
@Deprecated
public class RaspberryCropBlock extends com.guoche.teyvatdelight.crop.RaspberryCropBlock {
    public static final MapCodec<RaspberryCropBlock> CODEC = simpleCodec(RaspberryCropBlock::new);

    public RaspberryCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<RaspberryCropBlock> codec() {
        return CODEC;
    }
}
