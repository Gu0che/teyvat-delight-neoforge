package com.guoche.teyvatdelight;

import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** @deprecated Use the crop package for new crop implementations. */
@Deprecated
public abstract class WildTeyvatCropBlock extends com.guoche.teyvatdelight.crop.WildTeyvatCropBlock {
    protected WildTeyvatCropBlock(BlockBehaviour.Properties properties,
            Supplier<? extends ItemLike> cropItem, Supplier<? extends ItemLike> seedItem, Surface surface) {
        super(properties, cropItem, seedItem, surface);
    }

    public enum Surface {
        GRASS_OR_DIRT,
        ROCKY,
        GRASS_DIRT_OR_MUD,
        SAND_NEAR_WATER,
        MUFENG_BUILDING,
        GRAINFRUIT,
        FLUORESCENT_FUNGUS,
        DENDROBIUM,
        FROSTLAMP,
        STONE_OR_TERRACOTTA
    }
}
