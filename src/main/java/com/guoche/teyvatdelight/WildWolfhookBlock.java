package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildWolfhookBlock} for new integrations. */
@Deprecated
public class WildWolfhookBlock extends com.guoche.teyvatdelight.crop.WildWolfhookBlock {
    public static final MapCodec<WildWolfhookBlock> CODEC = simpleCodec(WildWolfhookBlock::new);

    public WildWolfhookBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildWolfhookBlock> codec() {
        return CODEC;
    }
}
