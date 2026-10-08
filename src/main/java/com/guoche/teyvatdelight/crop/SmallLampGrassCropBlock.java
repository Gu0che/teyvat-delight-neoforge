package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SmallLampGrassCropBlock extends TeyvatCropBlock {
    public static final MapCodec<SmallLampGrassCropBlock> CODEC = simpleCodec(SmallLampGrassCropBlock::new);
    private static final int MATURE_LIGHT_LEVEL = 7;

    public SmallLampGrassCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                TeyvatDelight.SMALL_LAMP_GRASS,
                TeyvatDelight.SMALL_LAMP_GRASS_SEEDS
        );
    }

    public static int getLightEmission(BlockState state) {
        return state.hasProperty(AGE) && state.getValue(AGE) >= 7 ? MATURE_LIGHT_LEVEL : 0;
    }

    @Override
    public MapCodec<? extends SmallLampGrassCropBlock> codec() {
        return CODEC;
    }
}
