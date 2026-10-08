package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildSumeruRoseBlock} for new integrations. */
@Deprecated
public class WildSumeruRoseBlock extends com.guoche.teyvatdelight.crop.WildSumeruRoseBlock {
    public static final MapCodec<WildSumeruRoseBlock> CODEC = simpleCodec(WildSumeruRoseBlock::new);

    public WildSumeruRoseBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildSumeruRoseBlock> codec() {
        return CODEC;
    }
}
