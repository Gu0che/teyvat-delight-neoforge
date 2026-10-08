package com.guoche.teyvatdelight.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/** Converts configured hunger icons and saturation icons without changing recipe values. */
public final class FoodPropertiesHelper {
    private FoodPropertiesHelper() {
    }

    public static Item.Properties dishItem(FoodProperties food) {
        return new Item.Properties()
                .stacksTo(16)
                .food(food);
    }

    public static FoodProperties dishFood(float hungerIcons, float saturationIcons) {
        return dishFoodBuilder(hungerIcons, saturationIcons).build();
    }

    public static FoodProperties.Builder dishFoodBuilder(float hungerIcons, float saturationIcons) {
        return foodBuilder(Math.round(hungerIcons * 2.0F), saturationIcons * 2.0F);
    }

    public static FoodProperties food(int nutrition, float saturationPoints) {
        return foodBuilder(nutrition, saturationPoints).build();
    }

    public static FoodProperties.Builder foodBuilder(int nutrition, float saturationPoints) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationPoints / (nutrition * 2.0F));
    }
}
