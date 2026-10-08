package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.PearlBearingCoralShellBlock} for new integrations. */
@Deprecated
public class PearlBearingCoralShellBlock extends com.guoche.teyvatdelight.crop.PearlBearingCoralShellBlock {
    public static final MapCodec<PearlBearingCoralShellBlock> CODEC = simpleCodec(PearlBearingCoralShellBlock::new);

    public PearlBearingCoralShellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
