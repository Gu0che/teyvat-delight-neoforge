package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildValberryBlock} for new integrations. */
@Deprecated
public class WildValberryBlock extends com.guoche.teyvatdelight.crop.WildValberryBlock {
    public static final MapCodec<WildValberryBlock> CODEC = simpleCodec(WildValberryBlock::new);

    public WildValberryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildValberryBlock> codec() {
        return CODEC;
    }
}
