package com.guoche.teyvatdelight.food;

import com.guoche.teyvatdelight.api.TeyvatItemData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class TeyvatDishItem extends Item {
    private final ChatFormatting nameColor;
    private final int defaultStars;

    public TeyvatDishItem(Properties properties) {
        this(properties, null, 1);
    }

    public TeyvatDishItem(Properties properties, ChatFormatting nameColor) {
        this(properties, nameColor, 1);
    }

    public TeyvatDishItem(Properties properties, ChatFormatting nameColor, int defaultStars) {
        super(properties);
        this.nameColor = nameColor;
        this.defaultStars = TeyvatItemData.clampStars(defaultStars);
    }

    @Override
    public Component getName(ItemStack stack) {
        int stars = TeyvatItemData.getStars(stack, defaultStars);
        if (stars > 0) {
            return Component.translatable(this.getDescriptionId(stack)).withStyle(TeyvatItemData.getStarColor(stars));
        }

        if (nameColor != null) {
            return Component.translatable(this.getDescriptionId(stack)).withStyle(nameColor);
        }

        return super.getName(stack);
    }

    public int getDefaultStars() {
        return defaultStars;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        TeyvatItemData.appendRarityTooltip(stack, tooltip, defaultStars);
        TeyvatItemData.appendFoodQualityTooltip(stack, tooltip);
        if (Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
            TextUtils.addFoodEffectTooltip(stack, tooltip::add, 1.0F, context.tickRate());
        }
    }
}
