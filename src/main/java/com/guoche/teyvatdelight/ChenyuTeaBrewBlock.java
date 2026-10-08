package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.food.ChenyuTeaBrewBlock} for new integrations. */
@Deprecated
public class ChenyuTeaBrewBlock extends com.guoche.teyvatdelight.food.ChenyuTeaBrewBlock {
    public static final MapCodec<ChenyuTeaBrewBlock> CODEC = simpleCodec(ChenyuTeaBrewBlock::new);

    public ChenyuTeaBrewBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
