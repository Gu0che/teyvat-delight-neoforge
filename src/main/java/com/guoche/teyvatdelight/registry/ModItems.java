package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.PortableNutritionBagItem;
import com.guoche.teyvatdelight.SeedDispensaryItem;
import com.guoche.teyvatdelight.StarconchItem;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WindWingsItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.item.KnifeItem;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.dishFood;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.dishFoodBuilder;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.food;

/** Owns non-dish item registrations; holders are resolved by deferred suppliers. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TeyvatDelight.MODID);

    public static final DeferredItem<BlockItem> XUAN_CI_JADE_FIELD_ITEM = ITEMS.registerSimpleBlockItem(
            "xuan_ci_jade_field",
            ModBlocks.XUAN_CI_JADE_FIELD
    );

    public static final DeferredItem<BlockItem> NI_CI_ZHI_FIELD_ITEM = ITEMS.registerSimpleBlockItem(
            "ni_ci_zhi_field",
            ModBlocks.NI_CI_ZHI_FIELD
    );

    public static final DeferredItem<BlockItem> CHU_CI_ZHU_FIELD_ITEM = ITEMS.registerSimpleBlockItem(
            "chu_ci_zhu_field",
            ModBlocks.CHU_CI_ZHU_FIELD
    );

    public static final DeferredItem<BlockItem> XUAN_CI_PU_FIELD_ITEM = ITEMS.registerSimpleBlockItem(
            "xuan_ci_pu_field",
            ModBlocks.XUAN_CI_PU_FIELD
    );

    public static final DeferredItem<BlockItem> NATURAL_SHIPO_ITEM = ITEMS.registerSimpleBlockItem(
            "natural_shipo",
            ModBlocks.NATURAL_SHIPO
    );

    public static final DeferredItem<BlockItem> NATURAL_YEBOSHI_ITEM = ITEMS.registerSimpleBlockItem(
            "natural_yeboshi",
            ModBlocks.NATURAL_YEBOSHI
    );

    public static final DeferredItem<BlockItem> NATURAL_DEEPSLATE_YEBOSHI_ITEM = ITEMS.registerSimpleBlockItem(
            "natural_deepslate_yeboshi",
            ModBlocks.NATURAL_DEEPSLATE_YEBOSHI
    );

    public static final DeferredItem<BlockItem> NATURAL_JINGHUAGUSUI_ITEM = ITEMS.registerSimpleBlockItem(
            "natural_jinghuagusui",
            ModBlocks.NATURAL_JINGHUAGUSUI
    );

    public static final DeferredItem<BlockItem> WILD_SMALL_LAMP_GRASS_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_small_lamp_grass",
            ModBlocks.WILD_SMALL_LAMP_GRASS
    );

    public static final DeferredItem<BlockItem> WILD_WINDWHEEL_ASTER_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_windwheel_aster",
            ModBlocks.WILD_WINDWHEEL_ASTER
    );

    public static final DeferredItem<BlockItem> WILD_CALLA_LILY_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_calla_lily",
            ModBlocks.WILD_CALLA_LILY
    );

    public static final DeferredItem<BlockItem> WILD_MINT_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_mint",
            ModBlocks.WILD_MINT
    );

    public static final DeferredItem<BlockItem> WILD_WOLFHOOK_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_wolfhook",
            ModBlocks.WILD_WOLFHOOK
    );

    public static final DeferredItem<BlockItem> WILD_SNAPDRAGON_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_snapdragon",
            ModBlocks.WILD_SNAPDRAGON
    );

    public static final DeferredItem<BlockItem> WILD_VALBERRY_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_valberry",
            ModBlocks.WILD_VALBERRY
    );

    public static final DeferredItem<BlockItem> WILD_DANDELION_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_dandelion",
            ModBlocks.WILD_DANDELION
    );

    public static final DeferredItem<BlockItem> WILD_CECILIA_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_cecilia",
            ModBlocks.WILD_CECILIA
    );

    public static final DeferredItem<BlockItem> WILD_RASPBERRY_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_raspberry",
            ModBlocks.WILD_RASPBERRY
    );

    public static final DeferredItem<BlockItem> WILD_SWEET_FLOWER_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_sweet_flower",
            ModBlocks.WILD_SWEET_FLOWER
    );

    public static final DeferredItem<BlockItem> PINK_WINDBLUME_ITEM = ITEMS.registerSimpleBlockItem(
            "pink_windblume",
            ModBlocks.PINK_WINDBLUME
    );

    public static final DeferredItem<BlockItem> YELLOW_WINDBLUME_ITEM = ITEMS.registerSimpleBlockItem(
            "yellow_windblume",
            ModBlocks.YELLOW_WINDBLUME
    );

    public static final DeferredItem<BlockItem> PURPLE_WINDBLUME_ITEM = ITEMS.registerSimpleBlockItem(
            "purple_windblume",
            ModBlocks.PURPLE_WINDBLUME
    );

    public static final DeferredItem<BlockItem> WILD_JUEYUN_CHILI_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_jueyun_chili",
            ModBlocks.WILD_JUEYUN_CHILI
    );

    public static final DeferredItem<BlockItem> WILD_GLAZE_LILY_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_glaze_lily",
            ModBlocks.WILD_GLAZE_LILY
    );

    public static final DeferredItem<BlockItem> WILD_MUFENG_MUSHROOM_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_mufeng_mushroom",
            ModBlocks.WILD_MUFENG_MUSHROOM
    );

    public static final DeferredItem<BlockItem> WILD_SUMERU_ROSE_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_sumeru_rose",
            ModBlocks.WILD_SUMERU_ROSE
    );

    public static final DeferredItem<BlockItem> WILD_GRAINFRUIT_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_grainfruit",
            ModBlocks.WILD_GRAINFRUIT
    );

    public static final DeferredItem<BlockItem> WILD_HORSETAIL_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_horsetail",
            ModBlocks.WILD_HORSETAIL
    );

    public static final DeferredItem<BlockItem> WILD_JINXIN_FLOWER_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_jinxin_flower",
            ModBlocks.WILD_JINXIN_FLOWER
    );

    public static final DeferredItem<BlockItem> WILD_FLUORESCENT_FUNGUS_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_fluorescent_fungus",
            ModBlocks.WILD_FLUORESCENT_FUNGUS
    );

    public static final DeferredItem<BlockItem> WILD_SEA_GANODERMA_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_sea_ganoderma",
            ModBlocks.WILD_SEA_GANODERMA
    );

    public static final DeferredItem<BlockItem> WILD_DENDROBIUM_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_dendrobium",
            ModBlocks.WILD_DENDROBIUM
    );

    public static final DeferredItem<BlockItem> WILD_FROSTLAMP_FLOWER_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_frostlamp_flower",
            ModBlocks.WILD_FROSTLAMP_FLOWER
    );

    public static final DeferredItem<Item> SMALL_LAMP_GRASS = ITEMS.registerSimpleItem(
            "small_lamp_grass",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> SMALL_LAMP_GRASS_SEEDS = ITEMS.register(
            "small_lamp_grass_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SMALL_LAMP_GRASS_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> WINDWHEEL_ASTER = ITEMS.registerSimpleItem(
            "windwheel_aster",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> WINDWHEEL_ASTER_SEEDS = ITEMS.register(
            "windwheel_aster_seeds",
            () -> new ItemNameBlockItem(ModBlocks.WINDWHEEL_ASTER_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> CALLA_LILY = ITEMS.registerSimpleItem(
            "calla_lily",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> CALLA_LILY_SEEDS = ITEMS.register(
            "calla_lily_seeds",
            () -> new ItemNameBlockItem(ModBlocks.CALLA_LILY_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> MINT = ITEMS.registerSimpleItem(
            "mint",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> MINT_SEEDS = ITEMS.register(
            "mint_seeds",
            () -> new ItemNameBlockItem(ModBlocks.MINT_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> WINDBLUME = ITEMS.registerSimpleItem(
            "windblume",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> WOLFHOOK = ITEMS.register(
            "wolfhook",
            () -> new ItemNameBlockItem(ModBlocks.WOLFHOOK_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> SNAPDRAGON = ITEMS.registerSimpleItem(
            "snapdragon",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> SNAPDRAGON_SEEDS = ITEMS.register(
            "snapdragon_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SNAPDRAGON_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> VALBERRY = ITEMS.registerSimpleItem(
            "valberry",
            new Item.Properties().food(dishFoodBuilder(1, 1.0F).fast().build())
    );

    public static final DeferredItem<ItemNameBlockItem> VALBERRY_SEEDS = ITEMS.register(
            "valberry_seeds",
            () -> new ItemNameBlockItem(ModBlocks.VALBERRY_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<ItemNameBlockItem> DANDELION_SEEDS = ITEMS.register(
            "dandelion_seeds",
            () -> new ItemNameBlockItem(ModBlocks.DANDELION_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> CECILIA = ITEMS.registerSimpleItem(
            "cecilia",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> CECILIA_SEEDS = ITEMS.register(
            "cecilia_seeds",
            () -> new ItemNameBlockItem(ModBlocks.CECILIA_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<ItemNameBlockItem> RASPBERRY = ITEMS.register(
            "raspberry",
            () -> new ItemNameBlockItem(ModBlocks.RASPBERRY_CROP.get(), new Item.Properties().food(dishFoodBuilder(1, 0.5F).fast().build()))
    );

    public static final DeferredItem<Item> SWEET_FLOWER = ITEMS.registerSimpleItem(
            "sweet_flower",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> SWEET_FLOWER_SEEDS = ITEMS.register(
            "sweet_flower_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SWEET_FLOWER_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> JUEYUN_CHILI = ITEMS.registerSimpleItem(
            "jueyun_chili",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> JUEYUN_CHILI_SEEDS = ITEMS.register(
            "jueyun_chili_seeds",
            () -> new ItemNameBlockItem(ModBlocks.JUEYUN_CHILI_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> GLAZE_LILY = ITEMS.registerSimpleItem(
            "glaze_lily",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> GLAZE_LILY_SEEDS = ITEMS.register(
            "glaze_lily_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GLAZE_LILY_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> MUFENG_MUSHROOM = ITEMS.registerSimpleItem(
            "mufeng_mushroom",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> MUFENG_MUSHROOM_SPORES = ITEMS.register(
            "mufeng_mushroom_spores",
            () -> new ItemNameBlockItem(ModBlocks.MUFENG_MUSHROOM_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> SUMERU_ROSE = ITEMS.registerSimpleItem(
            "sumeru_rose",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> SUMERU_ROSE_SEEDS = ITEMS.register(
            "sumeru_rose_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SUMERU_ROSE_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> GRAINFRUIT = ITEMS.registerSimpleItem(
            "grainfruit",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> GRAINFRUIT_SEEDS = ITEMS.register(
            "grainfruit_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GRAINFRUIT_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> FLUORESCENT_FUNGUS = ITEMS.registerSimpleItem(
            "fluorescent_fungus",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> FLUORESCENT_FUNGUS_SPORES = ITEMS.register(
            "fluorescent_fungus_spores",
            () -> new ItemNameBlockItem(ModBlocks.FLUORESCENT_FUNGUS_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> SEA_GANODERMA = ITEMS.registerSimpleItem(
            "sea_ganoderma",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> SEA_GANODERMA_SAMPLE = ITEMS.register(
            "sea_ganoderma_sample",
            () -> new ItemNameBlockItem(ModBlocks.SEA_GANODERMA_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> DENDROBIUM = ITEMS.registerSimpleItem(
            "dendrobium",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> DENDROBIUM_SEEDS = ITEMS.register(
            "dendrobium_seeds",
            () -> new ItemNameBlockItem(ModBlocks.DENDROBIUM_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> FROSTLAMP_FLOWER = ITEMS.registerSimpleItem(
            "frostlamp_flower",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> FROSTLAMP_FLOWER_SEEDS = ITEMS.register(
            "frostlamp_flower_seeds",
            () -> new ItemNameBlockItem(ModBlocks.FROSTLAMP_FLOWER_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> YUNYAN_LIEYE = ITEMS.registerSimpleItem(
            "yunyan_lieye",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> YUNYAN_LIEYE_SEEDS = ITEMS.register(
            "yunyan_lieye_seeds",
            () -> new ItemNameBlockItem(ModBlocks.YUNYAN_LIEYE_CROP.get(), new Item.Properties())
    );

    public static final DeferredItem<BlockItem> WILD_YUNYAN_LIEYE_ITEM = ITEMS.registerSimpleBlockItem(
            "wild_yunyan_lieye",
            ModBlocks.WILD_YUNYAN_LIEYE
    );

    public static final DeferredItem<ItemNameBlockItem> CORAL_SHELL = ITEMS.register(
            "coral_shell",
            () -> new ItemNameBlockItem(ModBlocks.GROWING_CORAL_SHELL.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> CORAL_PEARL = ITEMS.registerSimpleItem(
            "coral_pearl",
            new Item.Properties()
    );

    public static final DeferredItem<BlockItem> NATURAL_CORAL_PEARL_ITEM = ITEMS.registerSimpleBlockItem(
            "natural_coral_pearl",
            ModBlocks.NATURAL_CORAL_PEARL
    );

    public static final DeferredItem<Item> HORSETAIL = ITEMS.registerSimpleItem(
            "horsetail",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> HORSETAIL_SEEDS = ITEMS.register(
            "horsetail_seeds",
            () -> new ItemNameBlockItem(ModBlocks.HORSETAIL_BOTTOM.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> JINXIN_FLOWER = ITEMS.registerSimpleItem(
            "jinxin_flower",
            new Item.Properties()
    );

    public static final DeferredItem<ItemNameBlockItem> JINXIN_FLOWER_BUD = ITEMS.register(
            "jinxin_flower_bud",
            () -> new ItemNameBlockItem(ModBlocks.THIRSTING_JINXIN_FLOWER.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> PEPPER = ITEMS.registerSimpleItem(
            "pepper",
            new Item.Properties()
    );

    public static final DeferredItem<Item> SALT = ITEMS.registerSimpleItem(
            "salt",
            new Item.Properties()
    );

    public static final DeferredItem<Item> CHENYU_TEA = ITEMS.registerSimpleItem(
            "chenyu_tea",
            new Item.Properties()
    );

    public static final DeferredItem<Item> TOFU = ITEMS.registerSimpleItem(
            "tofu",
            new Item.Properties()
    );

    public static final DeferredItem<Item> GLABROUS_BEANS = ITEMS.registerSimpleItem(
            "glabrous_beans",
            new Item.Properties()
    );

    public static final DeferredItem<Item> SHRIMP_MEAT = ITEMS.registerSimpleItem(
            "shrimp_meat",
            new Item.Properties()
    );

    public static final DeferredItem<Item> ALMOND = ITEMS.registerSimpleItem(
            "almond",
            new Item.Properties()
    );

    public static final DeferredItem<Item> MATSUTAKE = ITEMS.registerSimpleItem(
            "matsutake",
            new Item.Properties()
    );

    public static final DeferredItem<Item> CRAB = ITEMS.registerSimpleItem(
            "crab",
            new Item.Properties()
    );

    public static final DeferredItem<Item> SAUSAGE = ITEMS.registerSimpleItem(
            "sausage",
            new Item.Properties()
    );

    public static final DeferredItem<Item> JAM = ITEMS.registerSimpleItem(
            "jam",
            new Item.Properties()
    );

    public static final DeferredItem<Item> BUTTER = ITEMS.registerSimpleItem(
            "butter",
            new Item.Properties()
    );

    public static final DeferredItem<Item> CHEESE = ITEMS.registerSimpleItem(
            "cheese",
            new Item.Properties().food(dishFood(3, 3.5F))
    );

    public static final DeferredItem<Item> CREAM = ITEMS.registerSimpleItem(
            "cream",
            new Item.Properties()
    );

    public static final DeferredItem<Item> SUNSETTIA = ITEMS.registerSimpleItem(
            "sunsettia",
            new Item.Properties().food(dishFood(2, 2.0F))
    );

    public static final DeferredItem<Item> PINECONE = ITEMS.registerSimpleItem(
            "pinecone",
            new Item.Properties()
    );

    public static final DeferredItem<Item> CRAB_ROE = ITEMS.registerSimpleItem(
            "crab_roe",
            new Item.Properties()
    );

    public static final DeferredItem<Item> SMOKED_FOWL = ITEMS.registerSimpleItem(
            "smoked_fowl",
            new Item.Properties().food(dishFood(3, 3.5F))
    );

    public static final DeferredItem<Item> MORA = ITEMS.registerSimpleItem(
            "mora",
            new Item.Properties()
    );

    public static final DeferredItem<Item> PRIMOGEM = ITEMS.registerSimpleItem(
            "primogem",
            new Item.Properties()
    );

    public static final DeferredItem<BlockItem> PRIMOGEM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "primogem_block",
            ModBlocks.PRIMOGEM_BLOCK
    );

    public static final DeferredItem<BlockItem> MORA_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "mora_block",
            ModBlocks.MORA_BLOCK
    );

    public static final DeferredItem<StarconchItem> STARCONCH_ITEM = ITEMS.register(
            "starconch",
            () -> new StarconchItem(new Item.Properties())
    );

    public static final DeferredItem<Item> EMPTY_FEATHER_MOTH_ITEM = ITEMS.registerSimpleItem(
            "empty_feather_moth", new Item.Properties()
    );

    public static final DeferredItem<DeferredSpawnEggItem> EMPTY_FEATHER_MOTH_SPAWN_EGG = ITEMS.register(
            "empty_feather_moth_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityTypes.EMPTY_FEATHER_MOTH, 0xEDC078, 0xFFF1B8, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> KATHERYNE_SPAWN_EGG = ITEMS.register(
            "katheryne_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityTypes.KATHERYNE, 0x333D43, 0xDFC89B, new Item.Properties())
    );

    private static final Tier PRIMOGEM_KNIFE_TIER = new Tier() {
        @Override
        public int getUses() {
            return Tiers.NETHERITE.getUses();
        }

        @Override
        public float getSpeed() {
            return Tiers.NETHERITE.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return Tiers.NETHERITE.getAttackDamageBonus();
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return Tiers.NETHERITE.getIncorrectBlocksForDrops();
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.NETHERITE.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(PRIMOGEM.get());
        }
    };

    public static final DeferredItem<KnifeItem> PRIMOGEM_KNIFE = ITEMS.register(
            "primogem_knife",
            () -> new KnifeItem(
                    PRIMOGEM_KNIFE_TIER,
                    new Item.Properties()
                            .attributes(KnifeItem.createAttributes(PRIMOGEM_KNIFE_TIER, 0.0F, -2.0F))
            )
    );

    public static final DeferredItem<ItemNameBlockItem> SHIPO = ITEMS.register(
            "shipo",
            () -> new ItemNameBlockItem(ModBlocks.BURIED_SHIPO_FRAGMENT.get(), new Item.Properties())
    );

    public static final DeferredItem<ItemNameBlockItem> JINGHUAGUSUI = ITEMS.register(
            "jinghuagusui",
            () -> new ItemNameBlockItem(ModBlocks.BURIED_JINGHUAGUSUI_FRAGMENT.get(), new Item.Properties())
    );

    public static final DeferredItem<ItemNameBlockItem> YEBOSHI = ITEMS.register(
            "yeboshi",
            () -> new ItemNameBlockItem(ModBlocks.BURIED_YEBOSHI_FRAGMENT.get(), new Item.Properties())
    );

    public static final DeferredItem<SeedDispensaryItem> SEED_DISPENSARY = ITEMS.register(
            "seed_dispensary",
            () -> new SeedDispensaryItem(new Item.Properties().stacksTo(1)
                    .component(ModDataComponents.STARS.get(), SeedDispensaryItem.DEFAULT_STARS))
    );

    public static final DeferredItem<WindWingsItem> WIND_WINGS = ITEMS.register(
            "wind_wings",
            () -> new WindWingsItem(new Item.Properties().stacksTo(1)
                    .component(ModDataComponents.STARS.get(), WindWingsItem.DEFAULT_STARS))
    );

    public static final DeferredItem<PortableNutritionBagItem> PORTABLE_NUTRITION_BAG = ITEMS.register(
            "portable_nutrition_bag",
            () -> new PortableNutritionBagItem(new Item.Properties().stacksTo(1)
                    .component(ModDataComponents.STARS.get(), PortableNutritionBagItem.DEFAULT_STARS))
    );

    public static final DeferredItem<BlockItem> KATHERYNE_FIGURINE_ITEM = ITEMS.register(
            "katheryne_figurine", () -> new com.guoche.teyvatdelight.item.KatheryneFigurineBlockItem(
                    ModBlocks.KATHERYNE_FIGURINE.get(), new Item.Properties()
                            .component(ModDataComponents.STARS.get(),
                                    com.guoche.teyvatdelight.item.KatheryneFigurineBlockItem.DEFAULT_STARS))
    );

    public static final DeferredItem<Item> SLOW_FALLING_ADVANCEMENT_ICON = ITEMS.registerSimpleItem("slow_falling_advancement_icon", new Item.Properties());

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ModFoods.init();
        ITEMS.register(modEventBus);
    }
}
