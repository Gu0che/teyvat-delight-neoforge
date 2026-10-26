package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildSnapdragonBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildSnapdragonBlock> CODEC = simpleCodec(WildSnapdragonBlock::new);

    public WildSnapdragonBlock(BlockBehaviour.Properties properties) {
        super(properties, com.guoche.teyvatdelight.registry.ModItems.SNAPDRAGON, com.guoche.teyvatdelight.registry.ModItems.SNAPDRAGON_SEEDS, Surface.SAND_NEAR_WATER);
    }

    @Override
    protected MapCodec<? extends WildSnapdragonBlock> codec() {
        return CODEC;
    }
}

