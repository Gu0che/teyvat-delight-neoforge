package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.crop.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WildFluorescentFungusBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildFluorescentFungusBlock> CODEC = simpleCodec(WildFluorescentFungusBlock::new);

    public WildFluorescentFungusBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                com.guoche.teyvatdelight.registry.ModItems.FLUORESCENT_FUNGUS,
                com.guoche.teyvatdelight.registry.ModItems.FLUORESCENT_FUNGUS_SPORES,
                Surface.FLUORESCENT_FUNGUS
        );
    }

    @Override
    protected MapCodec<? extends WildFluorescentFungusBlock> codec() {
        return CODEC;
    }
}
