package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CeciliaCropBlock extends TeyvatCropBlock {
    public static final MapCodec<CeciliaCropBlock> CODEC = simpleCodec(CeciliaCropBlock::new);

    public CeciliaCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.NI_CI_ZHI_FIELDS, com.guoche.teyvatdelight.registry.ModItems.CECILIA, com.guoche.teyvatdelight.registry.ModItems.CECILIA_SEEDS, 7, 1, 1);
    }

    @Override
    public MapCodec<? extends CeciliaCropBlock> codec() {
        return CODEC;
    }
}

