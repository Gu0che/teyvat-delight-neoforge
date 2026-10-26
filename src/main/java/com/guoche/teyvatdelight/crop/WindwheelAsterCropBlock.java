package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
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
                com.guoche.teyvatdelight.registry.ModItems.WINDWHEEL_ASTER,
                com.guoche.teyvatdelight.registry.ModItems.WINDWHEEL_ASTER_SEEDS
        );
    }

    @Override
    public MapCodec<? extends WindwheelAsterCropBlock> codec() {
        return CODEC;
    }
}
