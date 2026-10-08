package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildYunyanCrackleafBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildYunyanCrackleafBlock> CODEC = simpleCodec(WildYunyanCrackleafBlock::new);

    public WildYunyanCrackleafBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.YUNYAN_LIEYE,
                TeyvatDelight.YUNYAN_LIEYE_SEEDS,
                Surface.STONE_OR_TERRACOTTA
        );
    }

    @Override
    protected MapCodec<? extends WildYunyanCrackleafBlock> codec() {
        return CODEC;
    }
}
