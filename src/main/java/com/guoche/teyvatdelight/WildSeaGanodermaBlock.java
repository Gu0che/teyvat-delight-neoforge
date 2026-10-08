package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildSeaGanodermaBlock} for new integrations. */
@Deprecated
public class WildSeaGanodermaBlock extends com.guoche.teyvatdelight.crop.WildSeaGanodermaBlock {
    public static final MapCodec<WildSeaGanodermaBlock> CODEC = simpleCodec(WildSeaGanodermaBlock::new);

    public WildSeaGanodermaBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
