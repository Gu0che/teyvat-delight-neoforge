package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
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
                com.guoche.teyvatdelight.registry.ModItems.SUMERU_ROSE,
                com.guoche.teyvatdelight.registry.ModItems.SUMERU_ROSE_SEEDS,
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
