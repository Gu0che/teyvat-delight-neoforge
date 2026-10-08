package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WindwheelAsterCropBlock} for new integrations. */
@Deprecated
public class WindwheelAsterCropBlock extends com.guoche.teyvatdelight.crop.WindwheelAsterCropBlock {
    public static final MapCodec<WindwheelAsterCropBlock> CODEC = simpleCodec(WindwheelAsterCropBlock::new);

    public WindwheelAsterCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<WindwheelAsterCropBlock> codec() {
        return CODEC;
    }
}
