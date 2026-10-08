package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.HorsetailTopBlock} for new integrations. */
@Deprecated
public class HorsetailTopBlock extends com.guoche.teyvatdelight.crop.HorsetailTopBlock {
    public static final MapCodec<HorsetailTopBlock> CODEC = simpleCodec(HorsetailTopBlock::new);

    public HorsetailTopBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<HorsetailTopBlock> codec() {
        return CODEC;
    }
}
