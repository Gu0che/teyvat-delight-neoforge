package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.SeaGanodermaCropBlock} for new integrations. */
@Deprecated
public class SeaGanodermaCropBlock extends com.guoche.teyvatdelight.crop.SeaGanodermaCropBlock {
    public static final MapCodec<SeaGanodermaCropBlock> CODEC = simpleCodec(SeaGanodermaCropBlock::new);

    public SeaGanodermaCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SeaGanodermaCropBlock> codec() {
        return CODEC;
    }
}
