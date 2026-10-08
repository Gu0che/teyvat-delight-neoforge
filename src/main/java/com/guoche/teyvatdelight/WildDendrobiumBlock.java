package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildDendrobiumBlock} for new integrations. */
@Deprecated
public class WildDendrobiumBlock extends com.guoche.teyvatdelight.crop.WildDendrobiumBlock {
    public static final MapCodec<WildDendrobiumBlock> CODEC = simpleCodec(WildDendrobiumBlock::new);

    public WildDendrobiumBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildDendrobiumBlock> codec() {
        return CODEC;
    }
}
