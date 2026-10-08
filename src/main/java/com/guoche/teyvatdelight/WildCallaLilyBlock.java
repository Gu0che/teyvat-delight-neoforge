package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildCallaLilyBlock} for new integrations. */
@Deprecated
public class WildCallaLilyBlock extends com.guoche.teyvatdelight.crop.WildCallaLilyBlock {
    public static final MapCodec<WildCallaLilyBlock> CODEC = simpleCodec(WildCallaLilyBlock::new);

    public WildCallaLilyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildCallaLilyBlock> codec() {
        return CODEC;
    }
}
