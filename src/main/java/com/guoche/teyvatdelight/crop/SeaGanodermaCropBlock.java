package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SeaGanodermaCropBlock extends TeyvatCropBlock {
    public static final MapCodec<SeaGanodermaCropBlock> CODEC = simpleCodec(SeaGanodermaCropBlock::new);
    private static final int MATURE_LIGHT_LEVEL = 7;

    public SeaGanodermaCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS,
                com.guoche.teyvatdelight.registry.ModItems.SEA_GANODERMA,
                com.guoche.teyvatdelight.registry.ModItems.SEA_GANODERMA_SAMPLE
        );
    }

    public static int getLightEmission(BlockState state) {
        return state.hasProperty(AGE) && state.getValue(AGE) >= 7 ? MATURE_LIGHT_LEVEL : 0;
    }

    @Override
    public MapCodec<? extends SeaGanodermaCropBlock> codec() {
        return CODEC;
    }
}
