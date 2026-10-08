package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildJueyunChiliBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildJueyunChiliBlock> CODEC = simpleCodec(WildJueyunChiliBlock::new);

    public WildJueyunChiliBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.JUEYUN_CHILI,
                TeyvatDelight.JUEYUN_CHILI_SEEDS,
                Surface.ROCKY
        );
    }

    @Override
    protected MapCodec<? extends WildJueyunChiliBlock> codec() {
        return CODEC;
    }
}
