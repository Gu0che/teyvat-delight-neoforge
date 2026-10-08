package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.SmallLampGrassCropBlock} for new integrations. */
@Deprecated
public class SmallLampGrassCropBlock extends com.guoche.teyvatdelight.crop.SmallLampGrassCropBlock {
    public static final MapCodec<SmallLampGrassCropBlock> CODEC = simpleCodec(SmallLampGrassCropBlock::new);

    public SmallLampGrassCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SmallLampGrassCropBlock> codec() {
        return CODEC;
    }
}
