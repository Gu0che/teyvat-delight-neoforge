package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.DandelionCropBlock} for new integrations. */
@Deprecated
public class DandelionCropBlock extends com.guoche.teyvatdelight.crop.DandelionCropBlock {
    public static final MapCodec<DandelionCropBlock> CODEC = simpleCodec(DandelionCropBlock::new);

    public DandelionCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<DandelionCropBlock> codec() {
        return CODEC;
    }
}
