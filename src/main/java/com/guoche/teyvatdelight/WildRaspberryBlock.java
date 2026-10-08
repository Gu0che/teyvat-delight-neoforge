package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildRaspberryBlock} for new integrations. */
@Deprecated
public class WildRaspberryBlock extends com.guoche.teyvatdelight.crop.WildRaspberryBlock {
    public static final MapCodec<WildRaspberryBlock> CODEC = simpleCodec(WildRaspberryBlock::new);

    public WildRaspberryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildRaspberryBlock> codec() {
        return CODEC;
    }
}
