package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GrainfruitCropBlock extends TeyvatCropBlock {
    public static final MapCodec<GrainfruitCropBlock> CODEC = simpleCodec(GrainfruitCropBlock::new);

    public GrainfruitCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                TeyvatDelight.GRAINFRUIT,
                TeyvatDelight.GRAINFRUIT_SEEDS,
                7,
                1,
                0
        );
    }

    @Override
    public MapCodec<? extends GrainfruitCropBlock> codec() {
        return CODEC;
    }
}
