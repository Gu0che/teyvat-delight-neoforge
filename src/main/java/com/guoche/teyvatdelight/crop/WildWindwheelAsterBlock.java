package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildWindwheelAsterBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildWindwheelAsterBlock> CODEC = simpleCodec(WildWindwheelAsterBlock::new);

    public WildWindwheelAsterBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.WINDWHEEL_ASTER,
                TeyvatDelight.WINDWHEEL_ASTER_SEEDS,
                Surface.GRASS_OR_DIRT
        );
    }

    @Override
    protected MapCodec<? extends WildWindwheelAsterBlock> codec() {
        return CODEC;
    }
}
