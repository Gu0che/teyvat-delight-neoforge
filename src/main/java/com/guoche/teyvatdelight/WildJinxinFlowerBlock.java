package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildJinxinFlowerBlock} for new integrations. */
@Deprecated
public class WildJinxinFlowerBlock extends com.guoche.teyvatdelight.crop.WildJinxinFlowerBlock {
    public static final MapCodec<WildJinxinFlowerBlock> CODEC = simpleCodec(WildJinxinFlowerBlock::new);

    public WildJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildJinxinFlowerBlock> codec() {
        return CODEC;
    }
}
