package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildSweetFlowerBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildSweetFlowerBlock> CODEC = simpleCodec(WildSweetFlowerBlock::new);

    public WildSweetFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties, com.guoche.teyvatdelight.registry.ModItems.SWEET_FLOWER, com.guoche.teyvatdelight.registry.ModItems.SWEET_FLOWER_SEEDS, Surface.ROCKY);
    }

    @Override
    protected MapCodec<? extends WildSweetFlowerBlock> codec() {
        return CODEC;
    }
}

