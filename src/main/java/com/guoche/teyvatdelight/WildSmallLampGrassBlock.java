package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildSmallLampGrassBlock} for new integrations. */
@Deprecated
public class WildSmallLampGrassBlock extends com.guoche.teyvatdelight.crop.WildSmallLampGrassBlock {
    public static final MapCodec<WildSmallLampGrassBlock> CODEC = simpleCodec(WildSmallLampGrassBlock::new);

    public WildSmallLampGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildSmallLampGrassBlock> codec() {
        return CODEC;
    }
}
