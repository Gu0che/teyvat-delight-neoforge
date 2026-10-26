package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildGrainfruitBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildGrainfruitBlock> CODEC = simpleCodec(WildGrainfruitBlock::new);

    public WildGrainfruitBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                com.guoche.teyvatdelight.registry.ModItems.GRAINFRUIT,
                com.guoche.teyvatdelight.registry.ModItems.GRAINFRUIT_SEEDS,
                Surface.GRAINFRUIT
        );
    }

    @Override
    protected MapCodec<? extends WildGrainfruitBlock> codec() {
        return CODEC;
    }
}
