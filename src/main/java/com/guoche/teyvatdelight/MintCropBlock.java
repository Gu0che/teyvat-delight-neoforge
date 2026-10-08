package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.MintCropBlock} for new integrations. */
@Deprecated
public class MintCropBlock extends com.guoche.teyvatdelight.crop.MintCropBlock {
    public static final MapCodec<MintCropBlock> CODEC = simpleCodec(MintCropBlock::new);

    public MintCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<MintCropBlock> codec() {
        return CODEC;
    }
}
