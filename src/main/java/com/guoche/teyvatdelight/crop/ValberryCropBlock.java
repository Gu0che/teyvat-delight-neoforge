package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ValberryCropBlock extends TeyvatCropBlock {
    public static final MapCodec<ValberryCropBlock> CODEC = simpleCodec(ValberryCropBlock::new);

    public ValberryCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS, com.guoche.teyvatdelight.registry.ModItems.VALBERRY, com.guoche.teyvatdelight.registry.ModItems.VALBERRY_SEEDS, 7, 4, 1);
    }

    @Override
    protected int getAgeAfterHarvest() {
        return 5;
    }

    @Override
    public MapCodec<? extends ValberryCropBlock> codec() {
        return CODEC;
    }
}

