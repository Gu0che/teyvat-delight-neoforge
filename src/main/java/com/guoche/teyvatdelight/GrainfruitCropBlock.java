package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.GrainfruitCropBlock} for new integrations. */
@Deprecated
public class GrainfruitCropBlock extends com.guoche.teyvatdelight.crop.GrainfruitCropBlock {
    public static final MapCodec<GrainfruitCropBlock> CODEC = simpleCodec(GrainfruitCropBlock::new);

    public GrainfruitCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GrainfruitCropBlock> codec() {
        return CODEC;
    }
}
