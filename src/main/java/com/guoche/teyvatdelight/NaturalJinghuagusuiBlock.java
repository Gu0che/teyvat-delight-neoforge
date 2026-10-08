package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.mineral.NaturalJinghuagusuiBlock} for new integrations. */
@Deprecated
public class NaturalJinghuagusuiBlock extends com.guoche.teyvatdelight.mineral.NaturalJinghuagusuiBlock {
    public static final MapCodec<NaturalJinghuagusuiBlock> CODEC = simpleCodec(NaturalJinghuagusuiBlock::new);

    public NaturalJinghuagusuiBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
