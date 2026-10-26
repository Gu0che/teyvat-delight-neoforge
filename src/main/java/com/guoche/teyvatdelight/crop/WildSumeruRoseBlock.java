package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildSumeruRoseBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildSumeruRoseBlock> CODEC = simpleCodec(WildSumeruRoseBlock::new);

    public WildSumeruRoseBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                com.guoche.teyvatdelight.registry.ModItems.SUMERU_ROSE,
                com.guoche.teyvatdelight.registry.ModItems.SUMERU_ROSE_SEEDS,
                Surface.GRASS_OR_DIRT
        );
    }

    @Override
    protected MapCodec<? extends WildSumeruRoseBlock> codec() {
        return CODEC;
    }
}
