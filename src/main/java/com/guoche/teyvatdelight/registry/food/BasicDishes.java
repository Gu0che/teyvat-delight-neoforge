package com.guoche.teyvatdelight.registry.food;

import com.guoche.teyvatdelight.PlaceableTeaItem;
import com.guoche.teyvatdelight.TeyvatDishItem;
import com.guoche.teyvatdelight.registry.ModBlocks;
import com.guoche.teyvatdelight.registry.ModDataComponents;
import com.guoche.teyvatdelight.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.registries.DeferredItem;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.dishItem;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.dishFood;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.dishFoodBuilder;
import static com.guoche.teyvatdelight.food.FoodPropertiesHelper.food;

/** Owns legacy dish registrations; holders are resolved by deferred suppliers. */
public final class BasicDishes {
    public static final DeferredItem<TeyvatDishItem> TEYVAT_FRIED_EGG = registerDish(
            "teyvat_fried_egg",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFood(2, 2.0F))
    );

    public static final DeferredItem<TeyvatDishItem> TEYVAT_SCORCHED_EGG = registerDish(
            "teyvat_scorched_egg",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFoodBuilder(3, 3.0F)
                            .effect(new MobEffectInstance(MobEffects.HEAL, 1, 0), 1.0F)
                            .build())
    );

    public static final DeferredItem<TeyvatDishItem> MUSHROOM_CHICKEN_SKEWER = registerDish(
            "mushroom_chicken_skewer",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFood(3, 4.0F))
    );

    public static final DeferredItem<TeyvatDishItem> FRUITY_SKEWERS = registerDish(
            "fruity_skewers",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFoodBuilder(4, 5.0F)
                            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 0), 1.0F)
                            .build())
    );

    public static final DeferredItem<TeyvatDishItem> GRILLED_STEAK = registerDish(
            "grilled_steak",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFood(4.5F, 7.0F))
    );

    public static final DeferredItem<TeyvatDishItem> OUTRIDERS_CHAMPION_STEAK = registerDish(
            "outriders_champion_steak",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFoodBuilder(5.5F, 8.0F)
                            .effect(new MobEffectInstance(MobEffects.JUMP, 60 * 20, 1), 1.0F)
                            .build())
    );

    public static final DeferredItem<TeyvatDishItem> STIR_FRIED_FILET = registerDish(
            "stir_fried_filet",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFoodBuilder(3, 4.0F)
                            .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                            .build())
    );

    public static final DeferredItem<TeyvatDishItem> RADISH_VEGGIE_SOUP = registerDish(
            "radish_veggie_soup",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFoodBuilder(2, 3.0F)
                            .effect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0), 1.0F)
                            .usingConvertsTo(Items.BOWL)
                            .build())
    );

    public static final DeferredItem<TeyvatDishItem> MONDSTADT_GRILLED_FISH = registerDish(
            "mondstadt_grilled_fish",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFood(4, 5.5F))
    );

    public static final DeferredItem<TeyvatDishItem> MORA_MEAT = registerDish(
            "mora_meat",
            new Item.Properties()
                    .stacksTo(16)
                    .food(dishFood(3, 4.0F))
    );

    public static final DeferredItem<TeyvatDishItem> LONG_NIGHT_FLAME = registerDish(
            "long_night_flame",
            dishItem(dishFoodBuilder(4, 5.0F)
                    .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 30 * 60 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> SMOKED_FISH_STEAK = registerDish(
            "smoked_fish_steak",
            dishItem(dishFoodBuilder(3, 4.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> CATCH_OF_THE_DARK_DEPTHS = registerDish(
            "catch_of_the_dark_depths",
            dishItem(dishFoodBuilder(4, 6.5F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 4), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> DARK_EGG = registerDish(
            "dark_egg",
            dishItem(dishFoodBuilder(3.5F, 3.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> MINT_SAUCE_GRILLED_FISH = registerDish(
            "mint_sauce_grilled_fish",
            dishItem(dishFoodBuilder(3, 5.5F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> GRAINFRUIT_CUP = registerDish(
            "grainfruit_cup",
            dishItem(dishFood(3, 4.0F))
    );

    public static final DeferredItem<TeyvatDishItem> JADE_PATTERN_TEA_EGG = registerDish(
            "jade_pattern_tea_egg",
            dishItem(dishFoodBuilder(3, 3.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<PlaceableTeaItem> CHENYU_TEA_BREW = ModItems.ITEMS.register(
            "chenyu_tea_brew",
            () -> new PlaceableTeaItem(ModBlocks.CHENYU_TEA_BREW_BLOCK.get(),
                    dishItem(dishFoodBuilder(1, 2.0F)
                            .effect(new MobEffectInstance(MobEffects.HEAL, 1, 0), 1.0F)
                            .usingConvertsTo(Items.FLOWER_POT)
                            .build()).component(ModDataComponents.STARS.get(), 1))
    );

    public static final DeferredItem<TeyvatDishItem> LEISURE_TEA = registerDish(
            "leisure_tea",
            dishItem(dishFoodBuilder(5, 6.5F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 90 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> HONEY_CHAR_SIU = registerDish(
            "honey_char_siu",
            dishItem(dishFoodBuilder(4, 5.5F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> ALMOND_TROUT = registerDish(
            "almond_trout",
            dishItem(dishFoodBuilder(3, 6.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> CONFIT_DUCK_LEG = registerDish(
            "confit_duck_leg",
            dishItem(dishFoodBuilder(4, 5.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> MINT_BEAN_SOUP = registerDish(
            "mint_bean_soup",
            dishItem(dishFoodBuilder(3, 5.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> LAMBAD_FISH_ROLL = registerDish(
            "lambad_fish_roll",
            dishItem(dishFoodBuilder(3, 4.0F)
                    .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> WARMTH = registerDish(
            "warmth",
            dishItem(dishFoodBuilder(3, 6.0F)
                    .effect(new MobEffectInstance(MobEffects.REGENERATION, 60 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> SOBA_NOODLES = registerDish(
            "soba_noodles",
            dishItem(dishFoodBuilder(3, 5.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> STRATAGEM = registerDish(
            "stratagem",
            dishItem(dishFoodBuilder(7.5F, 6.9F)
                    .effect(new MobEffectInstance(MobEffects.REGENERATION, 45 * 20, 1), 1.0F)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> SATIETY_GEL = registerDish(
            "satiety_gel",
            dishItem(dishFoodBuilder(3, 4.0F)
                    .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 0), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.JUMP, 60 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> ETERNAL_FAITH = registerDish(
            "eternal_faith",
            dishItem(dishFoodBuilder(4, 3.5F)
                    .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 60 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> BIRD_EGG_YAKI = registerDish(
            "bird_egg_yaki",
            dishItem(dishFoodBuilder(3, 2.5F)
                    .usingConvertsTo(Items.BOWL)
                    .build()).craftRemainder(Items.BOWL)
    );

    public static final DeferredItem<TeyvatDishItem> BIRD_EGG_SUSHI = registerDish(
            "bird_egg_sushi",
            dishItem(dishFood(6.5F, 5.9F))
    );

    public static final DeferredItem<TeyvatDishItem> SWEET_SHRIMP_SUSHI = registerDish(
            "sweet_shrimp_sushi",
            dishItem(dishFood(5, 6.0F))
    );

    public static final DeferredItem<TeyvatDishItem> RAIN_OR_SHINE = registerDish(
            "rain_or_shine",
            dishItem(dishFoodBuilder(4, 5.0F)
                    .effect(new MobEffectInstance(MobEffects.JUMP, 45 * 20, 2), 1.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> DRY_BRAISED_FISH = registerDish(
            "dry_braised_fish",
            dishItem(dishFoodBuilder(3, 4.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> MISO_SOUP = registerDish(
            "miso_soup",
            dishItem(dishFoodBuilder(2, 5.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> RICE_BUNS = registerDish(
            "rice_buns",
            dishItem(dishFoodBuilder(4, 3.0F)
                    .usingConvertsTo(Items.BOWL)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> MINT_JELLY = registerDish(
            "mint_jelly",
            dishItem(dishFoodBuilder(2, 3.0F)
                    .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> DEFINITELY_NOT_BAR_FOOD = registerDish(
            "definitely_not_bar_food",
            dishItem(dishFoodBuilder(5, 6.5F)
                    .effect(new MobEffectInstance(MobEffects.REGENERATION, 60 * 20, 0), 1.0F)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> QIANKUN_MORA_MEAT = registerDish(
            "qiankun_mora_meat",
            dishItem(dishFoodBuilder(4, 5.0F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60 * 20, 0), 1.0F)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> GRILLED_TIGER_FISH = registerDish(
            "grilled_tiger_fish",
            dishItem(dishFood(4, 5.5F))
    );

    public static final DeferredItem<TeyvatDishItem> LARGE_BOWL_OF_TEA = ModItems.ITEMS.register(
            "large_bowl_of_tea",
            () -> new TeyvatDishItem(dishItem(dishFood(0.5F, 1.0F)).component(ModDataComponents.STARS.get(), 1)) {
                @Override
                public UseAnim getUseAnimation(ItemStack stack) {
                    return UseAnim.DRINK;
                }

                @Override
                public SoundEvent getEatingSound() {
                    return SoundEvents.GENERIC_DRINK;
                }
            }
    );

    public static final DeferredItem<TeyvatDishItem> FLAMING_STIR_FRIED_FILET = registerDish(
            "flaming_stir_fried_filet",
            dishItem(dishFoodBuilder(4, 5.0F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * 20, 0), 1.0F)
                    .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> SURVIVAL_GRILLED_FISH = registerDish(
            "survival_grilled_fish",
            dishItem(dishFoodBuilder(4, 7.0F)
                    .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 1), 1.0F)
                    .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 60 * 20, 1), 1.0F)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> XIAN_TIAO_QIANG = registerGoldDish(
            "xian_tiao_qiang",
            dishItem(dishFoodBuilder(10, 16.0F)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 90 * 20, 1), 1.0F)
                    .usingConvertsTo(Items.FLOWER_POT)
                    .build())
    );

    public static final DeferredItem<TeyvatDishItem> OLD_COURTYARD_SMOKED_SAUSAGE = registerDish(
            "old_courtyard_smoked_sausage",
            dishItem(dishFoodBuilder(3, 4.0F)
                    .usingConvertsTo(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .build())
    );

    private static DeferredItem<TeyvatDishItem> registerDish(String name, Item.Properties properties) {
        return ModItems.ITEMS.register(name, () -> new TeyvatDishItem(
                properties.component(ModDataComponents.STARS.get(), 1),
                null,
                1
        ));
    }

    private static DeferredItem<TeyvatDishItem> registerGoldDish(String name, Item.Properties properties) {
        return ModItems.ITEMS.register(name, () -> new TeyvatDishItem(
                properties.component(ModDataComponents.STARS.get(), 5),
                ChatFormatting.GOLD,
                5
        ));
    }

    private BasicDishes() {
    }

    public static void init() {
        // Loading this class queues all legacy dishes in ModItems.ITEMS.
    }
}
