package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WildMufengMushroomBlock} for new integrations. */
@Deprecated
public class WildMufengMushroomBlock extends com.guoche.teyvatdelight.crop.WildMufengMushroomBlock {
    public static final MapCodec<WildMufengMushroomBlock> CODEC = simpleCodec(WildMufengMushroomBlock::new);

    public WildMufengMushroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildMufengMushroomBlock> codec() {
        return CODEC;
    }
}
