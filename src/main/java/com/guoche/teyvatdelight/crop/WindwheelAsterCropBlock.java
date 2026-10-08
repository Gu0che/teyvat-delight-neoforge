package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WindwheelAsterCropBlock extends TeyvatCropBlock {
    public static final MapCodec<WindwheelAsterCropBlock> CODEC = simpleCodec(WindwheelAsterCropBlock::new);

    public WindwheelAsterCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.WINDWHEEL_ASTER,
                TeyvatDelight.WINDWHEEL_ASTER_SEEDS
        );
    }

    @Override
    public MapCodec<? extends WindwheelAsterCropBlock> codec() {
        return CODEC;
    }
}
