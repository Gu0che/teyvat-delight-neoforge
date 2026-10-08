package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.FrostlampFlowerCropBlock} for new integrations. */
@Deprecated
public class FrostlampFlowerCropBlock extends com.guoche.teyvatdelight.crop.FrostlampFlowerCropBlock {
    public static final MapCodec<FrostlampFlowerCropBlock> CODEC = simpleCodec(FrostlampFlowerCropBlock::new);

    public FrostlampFlowerCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<FrostlampFlowerCropBlock> codec() {
        return CODEC;
    }
}
