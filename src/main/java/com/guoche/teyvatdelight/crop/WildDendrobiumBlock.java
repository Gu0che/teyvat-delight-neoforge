package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildDendrobiumBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildDendrobiumBlock> CODEC = simpleCodec(WildDendrobiumBlock::new);

    public WildDendrobiumBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.DENDROBIUM,
                TeyvatDelight.DENDROBIUM_SEEDS,
                Surface.DENDROBIUM
        );
    }

    @Override
    protected MapCodec<? extends WildDendrobiumBlock> codec() {
        return CODEC;
    }
}
