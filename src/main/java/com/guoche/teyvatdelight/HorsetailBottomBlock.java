package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.HorsetailBottomBlock} for new integrations. */
@Deprecated
public class HorsetailBottomBlock extends com.guoche.teyvatdelight.crop.HorsetailBottomBlock {
    public static final MapCodec<HorsetailBottomBlock> CODEC = simpleCodec(HorsetailBottomBlock::new);

    public HorsetailBottomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<HorsetailBottomBlock> codec() {
        return CODEC;
    }
}
