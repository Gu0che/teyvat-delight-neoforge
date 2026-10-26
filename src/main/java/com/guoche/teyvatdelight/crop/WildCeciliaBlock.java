package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildCeciliaBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildCeciliaBlock> CODEC = simpleCodec(WildCeciliaBlock::new);

    public WildCeciliaBlock(BlockBehaviour.Properties properties) {
        super(properties, com.guoche.teyvatdelight.registry.ModItems.CECILIA, com.guoche.teyvatdelight.registry.ModItems.CECILIA_SEEDS, Surface.GRASS_OR_DIRT);
    }

    @Override
    protected MapCodec<? extends WildCeciliaBlock> codec() {
        return CODEC;
    }
}

