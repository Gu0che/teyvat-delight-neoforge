package com.guoche.teyvatdelight.api;

import com.guoche.teyvatdelight.api.internal.SpecialtyCatalog;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stable read-only crop queries after registry setup, on either logical side.
 * No query harvests, consumes randomness or loads chunks. Collections are immutable.
 * Maturity describes growth, not permission: soil, tools and player protection still apply.
 */
public final class TeyvatCropApi {
    /** SHARED means the same block is used both in nature and in cultivation. */
    public enum Form { CULTIVATED, WILD, SHARED }
    public enum Part { SINGLE, LOWER, UPPER }
    public enum Maturity { IMMATURE, MATURE, UNKNOWN }
    public enum HarvestMethod { RIGHT_CLICK, BREAK, SNEAKING, COOLING, KNIFE, BONEMEAL }

    /** Preferred field is a live tag, not an exhaustive list of valid planting surfaces. */
    public record Definition(ResourceLocation id, Item produce, Item plantingItem,
            TagKey<Block> preferredField, List<Block> cultivatedBlocks, List<Block> wildBlocks,
            HarvestMethod cultivatedHarvestMethod) {
        public Definition {
            cultivatedBlocks = List.copyOf(cultivatedBlocks);
            wildBlocks = List.copyOf(wildBlocks);
        }
    }

    /**
     * Age/maxAge describe this part and phase only; non-age phases use 0/0.
     * Use maturity rather than comparing ages: Jinxin becomes a different block when mature.
     * UNKNOWN means a second part is needed. The world overload resolves loaded partners.
     */
    public record State(Definition definition, Form form, Part part, int age, int maxAge,
            Maturity maturity, HarvestMethod harvestMethod) {
        public boolean isMature() { return maturity == Maturity.MATURE; }
    }

    private TeyvatCropApi() {}

    public static List<Definition> definitions() {
        return SpecialtyCatalog.get().crops();
    }

    /** Logical crop ID, e.g. teyvatdelight:small_lamp_grass, not a block stage ID. */
    public static Optional<Definition> findDefinition(ResourceLocation id) {
        return SpecialtyCatalog.get().crop(id);
    }

    /** Produce, planting materials and wild block items; Windblume colors share produce. */
    public static List<Definition> findDefinitions(ItemStack stack) {
        return stack.isEmpty() ? List.of() : SpecialtyCatalog.get().crops(stack.getItem());
    }

    public static Optional<State> inspect(BlockState state) {
        return SpecialtyCatalog.get().inspectCrop(state);
    }

    /**
     * Call on the owning world thread. Unloaded/out-of-height positions return empty.
     * Unloaded horsetail partners return UNKNOWN, without loading any chunk.
     */
    public static Optional<State> inspect(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos) || !level.hasChunkAt(pos)) return Optional.empty();
        return SpecialtyCatalog.get().inspectCrop(level, pos);
    }
}
