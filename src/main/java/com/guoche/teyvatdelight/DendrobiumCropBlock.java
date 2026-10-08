package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.DendrobiumCropBlock} for new integrations. */
@Deprecated
public class DendrobiumCropBlock extends com.guoche.teyvatdelight.crop.DendrobiumCropBlock {
    public static final MapCodec<DendrobiumCropBlock> CODEC = simpleCodec(DendrobiumCropBlock::new);

    public DendrobiumCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<DendrobiumCropBlock> codec() {
        return CODEC;
    }
}
