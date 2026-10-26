package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildCallaLilyBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildCallaLilyBlock> CODEC = simpleCodec(WildCallaLilyBlock::new);

    public WildCallaLilyBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                com.guoche.teyvatdelight.registry.ModItems.CALLA_LILY,
                com.guoche.teyvatdelight.registry.ModItems.CALLA_LILY_SEEDS,
                Surface.SAND_NEAR_WATER
        );
    }

    @Override
    protected MapCodec<? extends WildCallaLilyBlock> codec() {
        return CODEC;
    }
}
