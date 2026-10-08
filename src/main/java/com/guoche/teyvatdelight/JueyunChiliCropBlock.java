package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.JueyunChiliCropBlock} for new integrations. */
@Deprecated
public class JueyunChiliCropBlock extends com.guoche.teyvatdelight.crop.JueyunChiliCropBlock {
    public static final MapCodec<JueyunChiliCropBlock> CODEC = simpleCodec(JueyunChiliCropBlock::new);

    public JueyunChiliCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<JueyunChiliCropBlock> codec() {
        return CODEC;
    }
}
