package com.guoche.teyvatdelight.api;

import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class TeyvatTags {
    public static final class Blocks {
        public static final TagKey<Block> CROPS = blockTag("crops");
        public static final TagKey<Block> TEYVAT_FIELDS = blockTag("teyvat_fields");
        public static final TagKey<Block> XUAN_CI_JADE_FIELDS = blockTag("xuan_ci_jade_fields");
        public static final TagKey<Block> NI_CI_ZHI_FIELDS = blockTag("ni_ci_zhi_fields");
        public static final TagKey<Block> CHU_CI_ZHU_FIELDS = blockTag("chu_ci_zhu_fields");
        public static final TagKey<Block> XUAN_CI_PU_FIELDS = blockTag("xuan_ci_pu_fields");
        public static final TagKey<Block> TEYVAT_MINERAL_BLOCKS = blockTag("teyvat_mineral_blocks");
        public static final TagKey<Block> NATURAL_SHIPO_SURFACES = blockTag("natural_shipo_surfaces");
        public static final TagKey<Block> NATURAL_NOCTILUCOUS_JADE_SURFACES = blockTag("natural_noctilucous_jade_surfaces");
        public static final TagKey<Block> WILD_GRASS_OR_DIRT_CROP_SURFACES = blockTag("wild_grass_or_dirt_crop_surfaces");
        public static final TagKey<Block> WILD_ROCKY_CROP_SURFACES = blockTag("wild_rocky_crop_surfaces");
        public static final TagKey<Block> WILD_GRASS_DIRT_OR_MUD_CROP_SURFACES = blockTag("wild_grass_dirt_or_mud_crop_surfaces");
        public static final TagKey<Block> WILD_FLUORESCENT_FUNGUS_SURFACES = blockTag("wild_fluorescent_fungus_surfaces");
        public static final TagKey<Block> WILD_DENDROBIUM_SURFACES = blockTag("wild_dendrobium_surfaces");
        public static final TagKey<Block> WILD_FROSTLAMP_SURFACES = blockTag("wild_frostlamp_surfaces");
        public static final TagKey<Block> WILD_CORAL_PEARL_SURFACES = blockTag("wild_coral_pearl_surfaces");
        public static final TagKey<Block> WILD_YUNYAN_CRACKLEAF_SURFACES = blockTag("wild_yunyan_crackleaf_surfaces");
    }

    public static final class Items {
        public static final TagKey<Item> TEYVAT_FOOD_ITEMS = itemTag("teyvat_food_items");
        public static final TagKey<Item> TEYVAT_CROP_ITEMS = itemTag("teyvat_crop_items");
        public static final TagKey<Item> TEYVAT_MINERAL_ITEMS = itemTag("teyvat_mineral_items");
        public static final TagKey<Item> TEYVAT_SPECIALTIES = itemTag("teyvat_specialties");
        public static final TagKey<Item> TEYVAT_DISHES = itemTag("teyvat_dishes");
        public static final TagKey<Item> MONDSTADT_DISHES = itemTag("mondstadt_dishes");
    }

    private TeyvatTags() {
    }

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, name));
    }

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, name));
    }
}
