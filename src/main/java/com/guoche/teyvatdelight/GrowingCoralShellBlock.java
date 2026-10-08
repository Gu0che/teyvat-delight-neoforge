package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.GrowingCoralShellBlock} for new integrations. */
@Deprecated
public class GrowingCoralShellBlock extends com.guoche.teyvatdelight.crop.GrowingCoralShellBlock {
    public static final MapCodec<GrowingCoralShellBlock> CODEC = simpleCodec(GrowingCoralShellBlock::new);

    public GrowingCoralShellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
