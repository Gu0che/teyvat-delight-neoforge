package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildJueyunChiliBlock} for new integrations. */
@Deprecated
public class WildJueyunChiliBlock extends com.guoche.teyvatdelight.crop.WildJueyunChiliBlock {
    public static final MapCodec<WildJueyunChiliBlock> CODEC = simpleCodec(WildJueyunChiliBlock::new);

    public WildJueyunChiliBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildJueyunChiliBlock> codec() {
        return CODEC;
    }
}
