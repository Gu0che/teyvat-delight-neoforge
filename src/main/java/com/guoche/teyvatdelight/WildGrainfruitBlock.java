package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildGrainfruitBlock} for new integrations. */
@Deprecated
public class WildGrainfruitBlock extends com.guoche.teyvatdelight.crop.WildGrainfruitBlock {
    public static final MapCodec<WildGrainfruitBlock> CODEC = simpleCodec(WildGrainfruitBlock::new);

    public WildGrainfruitBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildGrainfruitBlock> codec() {
        return CODEC;
    }
}
