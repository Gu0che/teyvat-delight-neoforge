package com.guoche.teyvatdelight;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.mineral.NaturalNoctilucousJadeBlock} for new integrations. */
@Deprecated
public class NaturalNoctilucousJadeBlock extends com.guoche.teyvatdelight.mineral.NaturalNoctilucousJadeBlock {
    public static final MapCodec<NaturalNoctilucousJadeBlock> CODEC = simpleCodec(NaturalNoctilucousJadeBlock::new);

    public NaturalNoctilucousJadeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public NaturalNoctilucousJadeBlock(
                BlockBehaviour.Properties properties,
                Supplier<? extends ItemLike> mineralItem,
                boolean deepslateBase
        ) {
        super(properties, mineralItem, deepslateBase);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
