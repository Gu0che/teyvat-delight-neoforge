package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SweetFlowerCropBlock extends TeyvatCropBlock {
    public static final MapCodec<SweetFlowerCropBlock> CODEC = simpleCodec(SweetFlowerCropBlock::new);

    public SweetFlowerCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS, TeyvatDelight.SWEET_FLOWER, TeyvatDelight.SWEET_FLOWER_SEEDS, 3, 1, 0);
    }

    @Override
    public MapCodec<? extends SweetFlowerCropBlock> codec() {
        return CODEC;
    }
}

