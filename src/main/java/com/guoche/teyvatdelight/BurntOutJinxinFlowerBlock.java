package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.BurntOutJinxinFlowerBlock} for new integrations. */
@Deprecated
public class BurntOutJinxinFlowerBlock extends com.guoche.teyvatdelight.crop.BurntOutJinxinFlowerBlock {
    public static final MapCodec<BurntOutJinxinFlowerBlock> CODEC = simpleCodec(BurntOutJinxinFlowerBlock::new);

    public BurntOutJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BurntOutJinxinFlowerBlock> codec() {
        return CODEC;
    }
}
