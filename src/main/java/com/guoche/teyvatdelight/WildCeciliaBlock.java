package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildCeciliaBlock} for new integrations. */
@Deprecated
public class WildCeciliaBlock extends com.guoche.teyvatdelight.crop.WildCeciliaBlock {
    public static final MapCodec<WildCeciliaBlock> CODEC = simpleCodec(WildCeciliaBlock::new);

    public WildCeciliaBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildCeciliaBlock> codec() {
        return CODEC;
    }
}
