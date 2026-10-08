package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildMintBlock} for new integrations. */
@Deprecated
public class WildMintBlock extends com.guoche.teyvatdelight.crop.WildMintBlock {
    public static final MapCodec<WildMintBlock> CODEC = simpleCodec(WildMintBlock::new);

    public WildMintBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildMintBlock> codec() {
        return CODEC;
    }
}
