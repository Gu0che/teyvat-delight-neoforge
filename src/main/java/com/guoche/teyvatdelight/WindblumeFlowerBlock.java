package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WindblumeFlowerBlock} for new integrations. */
@Deprecated
public class WindblumeFlowerBlock extends com.guoche.teyvatdelight.crop.WindblumeFlowerBlock {
    public static final MapCodec<WindblumeFlowerBlock> CODEC = simpleCodec(WindblumeFlowerBlock::new);

    public WindblumeFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WindblumeFlowerBlock> codec() {
        return CODEC;
    }
}
