package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MintCropBlock extends TeyvatCropBlock {
    public static final MapCodec<MintCropBlock> CODEC = simpleCodec(MintCropBlock::new);

    public MintCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                com.guoche.teyvatdelight.registry.ModItems.MINT,
                com.guoche.teyvatdelight.registry.ModItems.MINT_SEEDS,
                5,
                1,
                0
        );
    }

    @Override
    public MapCodec<? extends MintCropBlock> codec() {
        return CODEC;
    }
}
