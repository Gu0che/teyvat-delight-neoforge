package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildSweetFlowerBlock} for new integrations. */
@Deprecated
public class WildSweetFlowerBlock extends com.guoche.teyvatdelight.crop.WildSweetFlowerBlock {
    public static final MapCodec<WildSweetFlowerBlock> CODEC = simpleCodec(WildSweetFlowerBlock::new);

    public WildSweetFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildSweetFlowerBlock> codec() {
        return CODEC;
    }
}
