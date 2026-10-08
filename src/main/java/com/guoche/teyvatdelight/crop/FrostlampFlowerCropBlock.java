package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FrostlampFlowerCropBlock extends TeyvatCropBlock {
    public static final MapCodec<FrostlampFlowerCropBlock> CODEC = simpleCodec(FrostlampFlowerCropBlock::new);

    public FrostlampFlowerCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.FROSTLAMP_FLOWER,
                TeyvatDelight.FROSTLAMP_FLOWER_SEEDS
        );
    }

    @Override
    public MapCodec<? extends FrostlampFlowerCropBlock> codec() {
        return CODEC;
    }
}
