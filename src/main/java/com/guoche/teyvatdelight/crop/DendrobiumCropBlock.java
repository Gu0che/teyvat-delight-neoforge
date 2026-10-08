package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DendrobiumCropBlock extends TeyvatCropBlock {
    public static final MapCodec<DendrobiumCropBlock> CODEC = simpleCodec(DendrobiumCropBlock::new);

    public DendrobiumCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.DENDROBIUM,
                TeyvatDelight.DENDROBIUM_SEEDS
        );
    }

    @Override
    public MapCodec<? extends DendrobiumCropBlock> codec() {
        return CODEC;
    }
}
