package com.guoche.teyvatdelight;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** @deprecated Use {@link com.guoche.teyvatdelight.api.TeyvatTags} for new integrations. */
@Deprecated
public final class TeyvatTags {
    private TeyvatTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> CROPS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.CROPS;
        public static final TagKey<Block> TEYVAT_FIELDS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.TEYVAT_FIELDS;
        public static final TagKey<Block> XUAN_CI_JADE_FIELDS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS;
        public static final TagKey<Block> NI_CI_ZHI_FIELDS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.NI_CI_ZHI_FIELDS;
        public static final TagKey<Block> CHU_CI_ZHU_FIELDS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS;
        public static final TagKey<Block> XUAN_CI_PU_FIELDS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.XUAN_CI_PU_FIELDS;
        public static final TagKey<Block> TEYVAT_MINERAL_BLOCKS = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.TEYVAT_MINERAL_BLOCKS;
        public static final TagKey<Block> NATURAL_SHIPO_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.NATURAL_SHIPO_SURFACES;
        public static final TagKey<Block> NATURAL_NOCTILUCOUS_JADE_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.NATURAL_NOCTILUCOUS_JADE_SURFACES;
        public static final TagKey<Block> WILD_GRASS_OR_DIRT_CROP_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_GRASS_OR_DIRT_CROP_SURFACES;
        public static final TagKey<Block> WILD_ROCKY_CROP_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_ROCKY_CROP_SURFACES;
        public static final TagKey<Block> WILD_GRASS_DIRT_OR_MUD_CROP_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_GRASS_DIRT_OR_MUD_CROP_SURFACES;
        public static final TagKey<Block> WILD_FLUORESCENT_FUNGUS_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_FLUORESCENT_FUNGUS_SURFACES;
        public static final TagKey<Block> WILD_DENDROBIUM_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_DENDROBIUM_SURFACES;
        public static final TagKey<Block> WILD_FROSTLAMP_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_FROSTLAMP_SURFACES;
        public static final TagKey<Block> WILD_CORAL_PEARL_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_CORAL_PEARL_SURFACES;
        public static final TagKey<Block> WILD_YUNYAN_CRACKLEAF_SURFACES = com.guoche.teyvatdelight.api.TeyvatTags.Blocks.WILD_YUNYAN_CRACKLEAF_SURFACES;
    }

    public static final class Items {
        public static final TagKey<Item> TEYVAT_FOOD_ITEMS = com.guoche.teyvatdelight.api.TeyvatTags.Items.TEYVAT_FOOD_ITEMS;
        public static final TagKey<Item> TEYVAT_CROP_ITEMS = com.guoche.teyvatdelight.api.TeyvatTags.Items.TEYVAT_CROP_ITEMS;
        public static final TagKey<Item> TEYVAT_MINERAL_ITEMS = com.guoche.teyvatdelight.api.TeyvatTags.Items.TEYVAT_MINERAL_ITEMS;
        public static final TagKey<Item> TEYVAT_SPECIALTIES = com.guoche.teyvatdelight.api.TeyvatTags.Items.TEYVAT_SPECIALTIES;
        public static final TagKey<Item> TEYVAT_DISHES = com.guoche.teyvatdelight.api.TeyvatTags.Items.TEYVAT_DISHES;
        public static final TagKey<Item> MONDSTADT_DISHES = com.guoche.teyvatdelight.api.TeyvatTags.Items.MONDSTADT_DISHES;
    }
}
