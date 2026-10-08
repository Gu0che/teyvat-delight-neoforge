package com.guoche.teyvatdelight.api;

import com.guoche.teyvatdelight.api.internal.SpecialtyCatalog;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Stable read-only mineral metadata and stage queries after registry setup, on either side. */
public final class TeyvatMineralApi {
    public enum Stage { BURIED, REPAIRING, INTACT, NATURAL }

    public record Definition(ResourceLocation id, Item produce, Item plantingItem,
            TagKey<Block> preferredField, TagKey<Item> miningTools,
            List<Block> cultivatedBlocks, List<Block> naturalBlocks) {
        public Definition {
            cultivatedBlocks = List.copyOf(cultivatedBlocks);
            naturalBlocks = List.copyOf(naturalBlocks);
        }
    }

    /** Maturity means intact/natural growth, not permission to mine or a prediction of loot. */
    public record State(Definition definition, Stage stage, boolean deepslate) {
        public boolean isMature() { return stage == Stage.INTACT || stage == Stage.NATURAL; }
    }

    private TeyvatMineralApi() {}

    public static List<Definition> definitions() {
        return SpecialtyCatalog.get().minerals();
    }

    public static Optional<Definition> findDefinition(ResourceLocation id) {
        return SpecialtyCatalog.get().mineral(id);
    }

    /** Supports mineral produce/planting items and natural block items. */
    public static Optional<Definition> findDefinition(ItemStack stack) {
        return stack.isEmpty() ? Optional.empty() : SpecialtyCatalog.get().mineral(stack.getItem());
    }

    public static Optional<State> inspect(BlockState state) {
        return SpecialtyCatalog.get().inspectMineral(state);
    }
}
