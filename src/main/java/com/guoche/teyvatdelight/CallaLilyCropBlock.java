package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.CallaLilyCropBlock} for new integrations. */
@Deprecated
public class CallaLilyCropBlock extends com.guoche.teyvatdelight.crop.CallaLilyCropBlock {
    public static final MapCodec<CallaLilyCropBlock> CODEC = simpleCodec(CallaLilyCropBlock::new);

    public CallaLilyCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<CallaLilyCropBlock> codec() {
        return CODEC;
    }
}
