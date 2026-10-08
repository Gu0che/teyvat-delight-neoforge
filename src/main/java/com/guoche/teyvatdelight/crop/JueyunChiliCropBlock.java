package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class JueyunChiliCropBlock extends TeyvatCropBlock {
    public static final MapCodec<JueyunChiliCropBlock> CODEC = simpleCodec(JueyunChiliCropBlock::new);

    public JueyunChiliCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                TeyvatDelight.JUEYUN_CHILI,
                TeyvatDelight.JUEYUN_CHILI_SEEDS,
                7,
                3,
                0
        );
    }

    @Override
    public MapCodec<? extends JueyunChiliCropBlock> codec() {
        return CODEC;
    }
}
