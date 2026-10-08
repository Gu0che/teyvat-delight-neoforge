package com.guoche.teyvatdelight;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** @deprecated Use {@link com.guoche.teyvatdelight.api.TeyvatItemData} for new integrations. */
@Deprecated
public final class TeyvatItemData {
    private TeyvatItemData() {
    }

    public static final int MIN_STARS = com.guoche.teyvatdelight.api.TeyvatItemData.MIN_STARS;

    public static final int MAX_STARS = com.guoche.teyvatdelight.api.TeyvatItemData.MAX_STARS;

    public static final String QUALITY_STRANGE = com.guoche.teyvatdelight.api.TeyvatItemData.QUALITY_STRANGE;

    public static final String QUALITY_NORMAL = com.guoche.teyvatdelight.api.TeyvatItemData.QUALITY_NORMAL;

    public static final String QUALITY_DELICIOUS = com.guoche.teyvatdelight.api.TeyvatItemData.QUALITY_DELICIOUS;

    public static int clampStars(int stars) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.clampStars(stars);
    }

    public static int getStars(ItemStack stack) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.getStars(stack);
    }

    public static int getStars(ItemStack stack, int fallbackStars) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.getStars(stack, fallbackStars);
    }

    public static int getDisplayStars(ItemStack stack) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.getDisplayStars(stack);
    }

    public static void setStars(ItemStack stack, int stars) {
        com.guoche.teyvatdelight.api.TeyvatItemData.setStars(stack, stars);
    }

    public static String getFoodQuality(ItemStack stack) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.getFoodQuality(stack);
    }

    public static void setFoodQuality(ItemStack stack, String quality) {
        com.guoche.teyvatdelight.api.TeyvatItemData.setFoodQuality(stack, quality);
    }

    public static String normalizeQuality(String quality) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.normalizeQuality(quality);
    }

    public static void appendRarityTooltip(ItemStack stack, List<Component> tooltip) {
        com.guoche.teyvatdelight.api.TeyvatItemData.appendRarityTooltip(stack, tooltip);
    }

    public static void appendRarityTooltip(ItemStack stack, List<Component> tooltip, int fallbackStars) {
        com.guoche.teyvatdelight.api.TeyvatItemData.appendRarityTooltip(stack, tooltip, fallbackStars);
    }

    public static void appendFoodQualityTooltip(ItemStack stack, List<Component> tooltip) {
        com.guoche.teyvatdelight.api.TeyvatItemData.appendFoodQualityTooltip(stack, tooltip);
    }

    public static String buildStars(int stars) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.buildStars(stars);
    }

    public static ChatFormatting getStarColor(int stars) {
        return com.guoche.teyvatdelight.api.TeyvatItemData.getStarColor(stars);
    }
}
