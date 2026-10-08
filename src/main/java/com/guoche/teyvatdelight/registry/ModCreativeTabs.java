package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.registry.food.BasicDishes;
import com.guoche.teyvatdelight.registry.food.MondstadtDishes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Owns creative tab registrations; holders are resolved by deferred suppliers. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TeyvatDelight.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TEYVAT_DELIGHT_TAB = CREATIVE_MODE_TABS.register(
            "teyvat_delight",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.teyvatdelight"))
                    .withTabsBefore(CreativeModeTabs.FOOD_AND_DRINKS)
                    .icon(() -> ModItems.PRIMOGEM_KNIFE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.XUAN_CI_JADE_FIELD_ITEM.get());
                        output.accept(ModItems.NI_CI_ZHI_FIELD_ITEM.get());
                        output.accept(ModItems.CHU_CI_ZHU_FIELD_ITEM.get());
                        output.accept(ModItems.XUAN_CI_PU_FIELD_ITEM.get());
                        output.accept(ModItems.SHIPO.get());
                        output.accept(ModItems.NATURAL_SHIPO_ITEM.get());
                        output.accept(ModItems.YEBOSHI.get());
                        output.accept(ModItems.NATURAL_YEBOSHI_ITEM.get());
                        output.accept(ModItems.NATURAL_DEEPSLATE_YEBOSHI_ITEM.get());
                        output.accept(ModItems.JINGHUAGUSUI.get());
                        output.accept(ModItems.NATURAL_JINGHUAGUSUI_ITEM.get());
                        output.accept(ModItems.SMALL_LAMP_GRASS.get());
                        output.accept(ModItems.SMALL_LAMP_GRASS_SEEDS.get());
                        output.accept(ModItems.WILD_SMALL_LAMP_GRASS_ITEM.get());
                        output.accept(ModItems.WINDWHEEL_ASTER.get());
                        output.accept(ModItems.WINDWHEEL_ASTER_SEEDS.get());
                        output.accept(ModItems.WILD_WINDWHEEL_ASTER_ITEM.get());
                        output.accept(ModItems.CALLA_LILY.get());
                        output.accept(ModItems.CALLA_LILY_SEEDS.get());
                        output.accept(ModItems.WILD_CALLA_LILY_ITEM.get());
                        output.accept(ModItems.MINT.get());
                        output.accept(ModItems.MINT_SEEDS.get());
                        output.accept(ModItems.WILD_MINT_ITEM.get());
                        output.accept(ModItems.WINDBLUME.get());
                        output.accept(ModItems.PINK_WINDBLUME_ITEM.get());
                        output.accept(ModItems.YELLOW_WINDBLUME_ITEM.get());
                        output.accept(ModItems.PURPLE_WINDBLUME_ITEM.get());
                        output.accept(ModItems.WOLFHOOK.get());
                        output.accept(ModItems.WILD_WOLFHOOK_ITEM.get());
                        output.accept(ModItems.SNAPDRAGON.get());
                        output.accept(ModItems.SNAPDRAGON_SEEDS.get());
                        output.accept(ModItems.WILD_SNAPDRAGON_ITEM.get());
                        output.accept(ModItems.VALBERRY.get());
                        output.accept(ModItems.VALBERRY_SEEDS.get());
                        output.accept(ModItems.WILD_VALBERRY_ITEM.get());
                        output.accept(ModItems.DANDELION_SEEDS.get());
                        output.accept(ModItems.WILD_DANDELION_ITEM.get());
                        output.accept(ModItems.CECILIA.get());
                        output.accept(ModItems.CECILIA_SEEDS.get());
                        output.accept(ModItems.WILD_CECILIA_ITEM.get());
                        output.accept(ModItems.RASPBERRY.get());
                        output.accept(ModItems.WILD_RASPBERRY_ITEM.get());
                        output.accept(ModItems.SWEET_FLOWER.get());
                        output.accept(ModItems.SWEET_FLOWER_SEEDS.get());
                        output.accept(ModItems.WILD_SWEET_FLOWER_ITEM.get());
                        output.accept(ModItems.JUEYUN_CHILI.get());
                        output.accept(ModItems.JUEYUN_CHILI_SEEDS.get());
                        output.accept(ModItems.WILD_JUEYUN_CHILI_ITEM.get());
                        output.accept(ModItems.GLAZE_LILY.get());
                        output.accept(ModItems.GLAZE_LILY_SEEDS.get());
                        output.accept(ModItems.WILD_GLAZE_LILY_ITEM.get());
                        output.accept(ModItems.MUFENG_MUSHROOM.get());
                        output.accept(ModItems.MUFENG_MUSHROOM_SPORES.get());
                        output.accept(ModItems.WILD_MUFENG_MUSHROOM_ITEM.get());
                        output.accept(ModItems.SUMERU_ROSE.get());
                        output.accept(ModItems.SUMERU_ROSE_SEEDS.get());
                        output.accept(ModItems.WILD_SUMERU_ROSE_ITEM.get());
                        output.accept(ModItems.GRAINFRUIT.get());
                        output.accept(ModItems.GRAINFRUIT_SEEDS.get());
                        output.accept(ModItems.WILD_GRAINFRUIT_ITEM.get());
                        output.accept(ModItems.HORSETAIL.get());
                        output.accept(ModItems.HORSETAIL_SEEDS.get());
                        output.accept(ModItems.WILD_HORSETAIL_ITEM.get());
                        output.accept(ModItems.JINXIN_FLOWER.get());
                        output.accept(ModItems.JINXIN_FLOWER_BUD.get());
                        output.accept(ModItems.WILD_JINXIN_FLOWER_ITEM.get());
                        output.accept(ModItems.FLUORESCENT_FUNGUS.get());
                        output.accept(ModItems.FLUORESCENT_FUNGUS_SPORES.get());
                        output.accept(ModItems.WILD_FLUORESCENT_FUNGUS_ITEM.get());
                        output.accept(ModItems.SEA_GANODERMA.get());
                        output.accept(ModItems.SEA_GANODERMA_SAMPLE.get());
                        output.accept(ModItems.WILD_SEA_GANODERMA_ITEM.get());
                        output.accept(ModItems.DENDROBIUM.get());
                        output.accept(ModItems.DENDROBIUM_SEEDS.get());
                        output.accept(ModItems.WILD_DENDROBIUM_ITEM.get());
                        output.accept(ModItems.FROSTLAMP_FLOWER.get());
                        output.accept(ModItems.FROSTLAMP_FLOWER_SEEDS.get());
                        output.accept(ModItems.WILD_FROSTLAMP_FLOWER_ITEM.get());
                        output.accept(ModItems.YUNYAN_LIEYE.get());
                        output.accept(ModItems.YUNYAN_LIEYE_SEEDS.get());
                        output.accept(ModItems.WILD_YUNYAN_LIEYE_ITEM.get());
                        output.accept(ModItems.CORAL_SHELL.get());
                        output.accept(ModItems.CORAL_PEARL.get());
                        output.accept(ModItems.NATURAL_CORAL_PEARL_ITEM.get());
                        output.accept(ModItems.PEPPER.get());
                        output.accept(ModItems.SALT.get());
                        output.accept(ModItems.CHENYU_TEA.get());
                        output.accept(ModItems.TOFU.get());
                        output.accept(ModItems.GLABROUS_BEANS.get());
                        output.accept(ModItems.SHRIMP_MEAT.get());
                        output.accept(ModItems.ALMOND.get());
                        output.accept(ModItems.MATSUTAKE.get());
                        output.accept(ModItems.CRAB.get());
                        output.accept(ModItems.SAUSAGE.get());
                        output.accept(ModItems.JAM.get());
                        output.accept(ModItems.BUTTER.get());
                        output.accept(ModItems.CHEESE.get());
                        output.accept(ModItems.CREAM.get());
                        output.accept(ModItems.SUNSETTIA.get());
                        output.accept(ModItems.PINECONE.get());
                        output.accept(ModItems.CRAB_ROE.get());
                        output.accept(ModItems.SMOKED_FOWL.get());
                        output.accept(ModItems.MORA.get());
                        output.accept(ModItems.MORA_BLOCK_ITEM.get());
                        output.accept(ModItems.PRIMOGEM.get());
                        output.accept(ModItems.PRIMOGEM_BLOCK_ITEM.get());
                        output.accept(ModItems.STARCONCH_ITEM.get());
                        output.accept(ModItems.EMPTY_FEATHER_MOTH_ITEM.get());
                        output.accept(ModItems.EMPTY_FEATHER_MOTH_SPAWN_EGG.get());
                        output.accept(ModItems.KATHERYNE_SPAWN_EGG.get());
                        output.accept(ModItems.KATHERYNE_FIGURINE_ITEM.get());
                        output.accept(ModItems.PRIMOGEM_KNIFE.get());
                        output.accept(ModItems.SEED_DISPENSARY.get());
                        output.accept(ModItems.WIND_WINGS.get());
                        output.accept(ModItems.PORTABLE_NUTRITION_BAG.get());
                        output.accept(BasicDishes.MUSHROOM_CHICKEN_SKEWER.get());
                        output.accept(BasicDishes.FRUITY_SKEWERS.get());
                        output.accept(BasicDishes.TEYVAT_FRIED_EGG.get());
                        output.accept(BasicDishes.GRILLED_STEAK.get());
                        output.accept(BasicDishes.RADISH_VEGGIE_SOUP.get());
                        output.accept(BasicDishes.MONDSTADT_GRILLED_FISH.get());
                        output.accept(BasicDishes.MORA_MEAT.get());
                        output.accept(BasicDishes.STIR_FRIED_FILET.get());
                        output.accept(BasicDishes.SURVIVAL_GRILLED_FISH.get());
                        output.accept(BasicDishes.TEYVAT_SCORCHED_EGG.get());
                        output.accept(BasicDishes.OUTRIDERS_CHAMPION_STEAK.get());
                        output.accept(BasicDishes.FLAMING_STIR_FRIED_FILET.get());
                        output.accept(BasicDishes.LARGE_BOWL_OF_TEA.get());
                        output.accept(BasicDishes.GRILLED_TIGER_FISH.get());
                        output.accept(BasicDishes.QIANKUN_MORA_MEAT.get());
                        output.accept(BasicDishes.DEFINITELY_NOT_BAR_FOOD.get());
                        output.accept(BasicDishes.MINT_JELLY.get());
                        output.accept(BasicDishes.RICE_BUNS.get());
                        output.accept(BasicDishes.HONEY_CHAR_SIU.get());
                        output.accept(BasicDishes.LEISURE_TEA.get());
                        output.accept(BasicDishes.CHENYU_TEA_BREW.get());
                        output.accept(BasicDishes.JADE_PATTERN_TEA_EGG.get());
                        output.accept(BasicDishes.BIRD_EGG_YAKI.get());
                        output.accept(BasicDishes.MISO_SOUP.get());
                        output.accept(BasicDishes.DRY_BRAISED_FISH.get());
                        output.accept(BasicDishes.RAIN_OR_SHINE.get());
                        output.accept(BasicDishes.SWEET_SHRIMP_SUSHI.get());
                        output.accept(BasicDishes.BIRD_EGG_SUSHI.get());
                        output.accept(BasicDishes.ETERNAL_FAITH.get());
                        output.accept(BasicDishes.SATIETY_GEL.get());
                        output.accept(BasicDishes.STRATAGEM.get());
                        output.accept(BasicDishes.SOBA_NOODLES.get());
                        output.accept(BasicDishes.WARMTH.get());
                        output.accept(BasicDishes.LAMBAD_FISH_ROLL.get());
                        output.accept(BasicDishes.MINT_BEAN_SOUP.get());
                        output.accept(BasicDishes.CONFIT_DUCK_LEG.get());
                        output.accept(BasicDishes.ALMOND_TROUT.get());
                        output.accept(BasicDishes.GRAINFRUIT_CUP.get());
                        output.accept(BasicDishes.MINT_SAUCE_GRILLED_FISH.get());
                        output.accept(BasicDishes.DARK_EGG.get());
                        output.accept(BasicDishes.CATCH_OF_THE_DARK_DEPTHS.get());
                        output.accept(BasicDishes.SMOKED_FISH_STEAK.get());
                        output.accept(BasicDishes.LONG_NIGHT_FLAME.get());
                        output.accept(BasicDishes.OLD_COURTYARD_SMOKED_SAUSAGE.get());
                        for (int stars = 2; stars <= 4; stars++) {
                            int currentStars = stars;
                            MondstadtDishes.ALL.stream()
                                    .map(DeferredItem::get)
                                    .filter(dish -> dish.getDefaultStars() == currentStars)
                                    .forEach(output::accept);
                        }
                        output.accept(BasicDishes.XIAN_TIAO_QIANG.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
