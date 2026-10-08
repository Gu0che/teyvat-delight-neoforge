package com.guoche.teyvatdelight;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use {@link com.guoche.teyvatdelight.crop.TeyvatCropBlock} for new integrations. */
@Deprecated
public abstract class TeyvatCropBlock extends com.guoche.teyvatdelight.crop.TeyvatCropBlock {
    protected TeyvatCropBlock(
                BlockBehaviour.Properties properties,
                TagKey<Block> preferredFieldTag,
                Supplier<? extends ItemLike> cropItem,
                Supplier<? extends ItemLike> seedItem
        ) {
        super(properties, preferredFieldTag, cropItem, seedItem);
    }

    protected TeyvatCropBlock(
                BlockBehaviour.Properties properties,
                TagKey<Block> preferredFieldTag,
                Supplier<? extends ItemLike> cropItem,
                Supplier<? extends ItemLike> seedItem,
                int maxAge,
                int harvestCount,
                int preferredFieldHarvestBonus
        ) {
        super(properties, preferredFieldTag, cropItem, seedItem, maxAge, harvestCount, preferredFieldHarvestBonus);
    }
}
