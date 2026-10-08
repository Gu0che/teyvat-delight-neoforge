package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.SnapdragonCropBlock} for new integrations. */
@Deprecated
public class SnapdragonCropBlock extends com.guoche.teyvatdelight.crop.SnapdragonCropBlock {
    public static final MapCodec<SnapdragonCropBlock> CODEC = simpleCodec(SnapdragonCropBlock::new);

    public SnapdragonCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SnapdragonCropBlock> codec() {
        return CODEC;
    }
}
