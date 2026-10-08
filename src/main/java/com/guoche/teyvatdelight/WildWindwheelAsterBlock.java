package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildWindwheelAsterBlock} for new integrations. */
@Deprecated
public class WildWindwheelAsterBlock extends com.guoche.teyvatdelight.crop.WildWindwheelAsterBlock {
    public static final MapCodec<WildWindwheelAsterBlock> CODEC = simpleCodec(WildWindwheelAsterBlock::new);

    public WildWindwheelAsterBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildWindwheelAsterBlock> codec() {
        return CODEC;
    }
}
