package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.MufengMushroomCropBlock} for new integrations. */
@Deprecated
public class MufengMushroomCropBlock extends com.guoche.teyvatdelight.crop.MufengMushroomCropBlock {
    public static final MapCodec<MufengMushroomCropBlock> CODEC = simpleCodec(MufengMushroomCropBlock::new);

    public MufengMushroomCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<MufengMushroomCropBlock> codec() {
        return CODEC;
    }
}
