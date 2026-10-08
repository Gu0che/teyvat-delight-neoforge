package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildFluorescentFungusBlock} for new integrations. */
@Deprecated
public class WildFluorescentFungusBlock extends com.guoche.teyvatdelight.crop.WildFluorescentFungusBlock {
    public static final MapCodec<WildFluorescentFungusBlock> CODEC = simpleCodec(WildFluorescentFungusBlock::new);

    public WildFluorescentFungusBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildFluorescentFungusBlock> codec() {
        return CODEC;
    }
}
