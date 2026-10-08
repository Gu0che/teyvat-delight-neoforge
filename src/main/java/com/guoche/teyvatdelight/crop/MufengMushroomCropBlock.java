package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MufengMushroomCropBlock extends TeyvatCropBlock {
    public static final MapCodec<MufengMushroomCropBlock> CODEC = simpleCodec(MufengMushroomCropBlock::new);

    public MufengMushroomCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.MUFENG_MUSHROOM,
                TeyvatDelight.MUFENG_MUSHROOM_SPORES,
                7,
                1,
                0
        );
    }

    @Override
    public MapCodec<? extends MufengMushroomCropBlock> codec() {
        return CODEC;
    }
}
