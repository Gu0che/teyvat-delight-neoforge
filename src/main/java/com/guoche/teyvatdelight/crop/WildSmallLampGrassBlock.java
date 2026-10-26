package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildSmallLampGrassBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildSmallLampGrassBlock> CODEC = simpleCodec(WildSmallLampGrassBlock::new);

    public WildSmallLampGrassBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                com.guoche.teyvatdelight.registry.ModItems.SMALL_LAMP_GRASS,
                com.guoche.teyvatdelight.registry.ModItems.SMALL_LAMP_GRASS_SEEDS,
                Surface.GRASS_OR_DIRT
        );
    }

    @Override
    protected MapCodec<? extends WildSmallLampGrassBlock> codec() {
        return CODEC;
    }
}
