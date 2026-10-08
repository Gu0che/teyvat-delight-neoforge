package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildFrostlampFlowerBlock} for new integrations. */
@Deprecated
public class WildFrostlampFlowerBlock extends com.guoche.teyvatdelight.crop.WildFrostlampFlowerBlock {
    public static final MapCodec<WildFrostlampFlowerBlock> CODEC = simpleCodec(WildFrostlampFlowerBlock::new);

    public WildFrostlampFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildFrostlampFlowerBlock> codec() {
        return CODEC;
    }
}
