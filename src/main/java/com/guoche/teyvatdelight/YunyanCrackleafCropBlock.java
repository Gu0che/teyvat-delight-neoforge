package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.YunyanCrackleafCropBlock} for new integrations. */
@Deprecated
public class YunyanCrackleafCropBlock extends com.guoche.teyvatdelight.crop.YunyanCrackleafCropBlock {
    public static final MapCodec<YunyanCrackleafCropBlock> CODEC = simpleCodec(YunyanCrackleafCropBlock::new);

    public YunyanCrackleafCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<YunyanCrackleafCropBlock> codec() {
        return CODEC;
    }
}
