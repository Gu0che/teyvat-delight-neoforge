package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SnapdragonCropBlock extends TeyvatCropBlock {
    public static final MapCodec<SnapdragonCropBlock> CODEC = simpleCodec(SnapdragonCropBlock::new);

    public SnapdragonCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS, TeyvatDelight.SNAPDRAGON, TeyvatDelight.SNAPDRAGON_SEEDS, 3, 1, 1);
    }

    @Override
    public MapCodec<? extends SnapdragonCropBlock> codec() {
        return CODEC;
    }
}

