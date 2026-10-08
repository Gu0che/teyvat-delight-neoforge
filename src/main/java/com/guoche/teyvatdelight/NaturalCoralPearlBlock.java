package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.NaturalCoralPearlBlock} for new integrations. */
@Deprecated
public class NaturalCoralPearlBlock extends com.guoche.teyvatdelight.crop.NaturalCoralPearlBlock {
    public static final MapCodec<NaturalCoralPearlBlock> CODEC = simpleCodec(NaturalCoralPearlBlock::new);

    public NaturalCoralPearlBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
