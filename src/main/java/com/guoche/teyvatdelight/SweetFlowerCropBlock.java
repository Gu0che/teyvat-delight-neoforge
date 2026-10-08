package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.SweetFlowerCropBlock} for new integrations. */
@Deprecated
public class SweetFlowerCropBlock extends com.guoche.teyvatdelight.crop.SweetFlowerCropBlock {
    public static final MapCodec<SweetFlowerCropBlock> CODEC = simpleCodec(SweetFlowerCropBlock::new);

    public SweetFlowerCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SweetFlowerCropBlock> codec() {
        return CODEC;
    }
}
