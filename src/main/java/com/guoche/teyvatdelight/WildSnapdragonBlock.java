package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildSnapdragonBlock} for new integrations. */
@Deprecated
public class WildSnapdragonBlock extends com.guoche.teyvatdelight.crop.WildSnapdragonBlock {
    public static final MapCodec<WildSnapdragonBlock> CODEC = simpleCodec(WildSnapdragonBlock::new);

    public WildSnapdragonBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildSnapdragonBlock> codec() {
        return CODEC;
    }
}
