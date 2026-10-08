package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildMintBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildMintBlock> CODEC = simpleCodec(WildMintBlock::new);

    public WildMintBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.MINT,
                TeyvatDelight.MINT_SEEDS,
                Surface.ROCKY
        );
    }

    @Override
    protected MapCodec<? extends WildMintBlock> codec() {
        return CODEC;
    }
}
