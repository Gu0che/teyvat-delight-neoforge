package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.mineral.ShipoCropBlock} for new integrations. */
@Deprecated
public class ShipoCropBlock extends com.guoche.teyvatdelight.mineral.ShipoCropBlock {
    public static final MapCodec<ShipoCropBlock> CODEC = simpleCodec(ShipoCropBlock::new);

    public ShipoCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public ShipoCropBlock(
                BlockBehaviour.Properties properties,
                int stage,
                Supplier<? extends Block> nextStage,
                double height
        ) {
        super(properties, stage, nextStage, height);
    }

    public ShipoCropBlock(
                BlockBehaviour.Properties properties,
                int stage,
                Supplier<? extends Block> nextStage,
                Supplier<? extends ItemLike> mineralItem,
                double height
        ) {
        super(properties, stage, nextStage, mineralItem, height);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
