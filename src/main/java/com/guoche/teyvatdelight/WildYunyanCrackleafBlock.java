package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildYunyanCrackleafBlock} for new integrations. */
@Deprecated
public class WildYunyanCrackleafBlock extends com.guoche.teyvatdelight.crop.WildYunyanCrackleafBlock {
    public static final MapCodec<WildYunyanCrackleafBlock> CODEC = simpleCodec(WildYunyanCrackleafBlock::new);

    public WildYunyanCrackleafBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildYunyanCrackleafBlock> codec() {
        return CODEC;
    }
}
