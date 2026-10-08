package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.WolfhookCropBlock} for new integrations. */
@Deprecated
public class WolfhookCropBlock extends com.guoche.teyvatdelight.crop.WolfhookCropBlock {
    public static final MapCodec<WolfhookCropBlock> CODEC = simpleCodec(WolfhookCropBlock::new);

    public WolfhookCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<WolfhookCropBlock> codec() {
        return CODEC;
    }
}
