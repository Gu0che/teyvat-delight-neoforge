package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.CeciliaCropBlock} for new integrations. */
@Deprecated
public class CeciliaCropBlock extends com.guoche.teyvatdelight.crop.CeciliaCropBlock {
    public static final MapCodec<CeciliaCropBlock> CODEC = simpleCodec(CeciliaCropBlock::new);

    public CeciliaCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<CeciliaCropBlock> codec() {
        return CODEC;
    }
}
