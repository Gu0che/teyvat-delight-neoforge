package com.guoche.teyvatdelight;

import net.minecraft.ChatFormatting;

/** @deprecated Use {@link com.guoche.teyvatdelight.food.TeyvatDishItem} for new integrations. */
@Deprecated
public class TeyvatDishItem extends com.guoche.teyvatdelight.food.TeyvatDishItem {
    public TeyvatDishItem(Properties properties) {
        super(properties);
    }

    public TeyvatDishItem(Properties properties, ChatFormatting nameColor) {
        super(properties, nameColor);
    }

    public TeyvatDishItem(Properties properties, ChatFormatting nameColor, int defaultStars) {
        super(properties, nameColor, defaultStars);
    }
}
