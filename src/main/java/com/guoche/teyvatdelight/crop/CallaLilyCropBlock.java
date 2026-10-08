package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CallaLilyCropBlock extends TeyvatCropBlock {
    public static final MapCodec<CallaLilyCropBlock> CODEC = simpleCodec(CallaLilyCropBlock::new);

    public CallaLilyCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS,
                TeyvatDelight.CALLA_LILY,
                TeyvatDelight.CALLA_LILY_SEEDS
        );
    }

    @Override
    public MapCodec<? extends CallaLilyCropBlock> codec() {
        return CODEC;
    }
}
