package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.FluorescentFungusCropBlock} for new integrations. */
@Deprecated
public class FluorescentFungusCropBlock extends com.guoche.teyvatdelight.crop.FluorescentFungusCropBlock {
    public static final MapCodec<FluorescentFungusCropBlock> CODEC = simpleCodec(FluorescentFungusCropBlock::new);

    public FluorescentFungusCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<FluorescentFungusCropBlock> codec() {
        return CODEC;
    }
}
