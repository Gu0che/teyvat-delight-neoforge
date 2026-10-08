package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SumeruRoseCropBlock extends TeyvatCropBlock {
    public static final MapCodec<SumeruRoseCropBlock> CODEC = simpleCodec(SumeruRoseCropBlock::new);

    public SumeruRoseCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                TeyvatDelight.SUMERU_ROSE,
                TeyvatDelight.SUMERU_ROSE_SEEDS,
                4,
                1,
                0
        );
    }

    @Override
    public MapCodec<? extends SumeruRoseCropBlock> codec() {
        return CODEC;
    }
}
