package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.SumeruRoseCropBlock} for new integrations. */
@Deprecated
public class SumeruRoseCropBlock extends com.guoche.teyvatdelight.crop.SumeruRoseCropBlock {
    public static final MapCodec<SumeruRoseCropBlock> CODEC = simpleCodec(SumeruRoseCropBlock::new);

    public SumeruRoseCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SumeruRoseCropBlock> codec() {
        return CODEC;
    }
}
