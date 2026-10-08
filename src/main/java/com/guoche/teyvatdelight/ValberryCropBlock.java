package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.ValberryCropBlock} for new integrations. */
@Deprecated
public class ValberryCropBlock extends com.guoche.teyvatdelight.crop.ValberryCropBlock {
    public static final MapCodec<ValberryCropBlock> CODEC = simpleCodec(ValberryCropBlock::new);

    public ValberryCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ValberryCropBlock> codec() {
        return CODEC;
    }
}
