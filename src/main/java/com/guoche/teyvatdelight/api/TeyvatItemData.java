package com.guoche.teyvatdelight.api;

import com.guoche.teyvatdelight.item.PortableNutritionBagItem;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.food.TeyvatDishItem;
import com.guoche.teyvatdelight.item.WindWingsItem;
import com.guoche.teyvatdelight.item.SeedDispensaryItem;
import com.guoche.teyvatdelight.item.KatheryneFigurineBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Common accessors for data stored on individual ItemStacks.
 *
 * <p>In Minecraft 1.21.1 these values are data components rather than the
 * legacy free-form NBT fields. Keeping all access here gives future mods a
 * small, stable contract to copy or call.</p>
 */
public final class TeyvatItemData {
    public static final int MIN_STARS = 1;
    public static final int MAX_STARS = 5;

    public static final String QUALITY_STRANGE = "strange";
    public static final String QUALITY_NORMAL = "normal";
    public static final String QUALITY_DELICIOUS = "delicious";

    private TeyvatItemData() {
    }

    public static int clampStars(int stars) {
        return Math.max(MIN_STARS, Math.min(MAX_STARS, stars));
    }

    /** Component value (including item prototype defaults), or 0 when absent; no display fallback. */
    public static int getStars(ItemStack stack) {
        Integer stars = stack.get(TeyvatDelight.STARS.get());
        return stars == null ? 0 : clampStars(stars);
    }

    public static int getStars(ItemStack stack, int fallbackStars) {
        Integer stars = stack.get(TeyvatDelight.STARS.get());
        return stars == null ? clampStars(fallbackStars) : clampStars(stars);
    }

    /** Effective visual star rating: stack override, then the item's default, otherwise 0. */
    public static int getDisplayStars(ItemStack stack) {
        int stars = getStars(stack);
        if (stars > 0) {
            return stars;
        }

        if (stack.getItem() instanceof TeyvatDishItem dishItem) {
            return dishItem.getDefaultStars();
        }

        if (stack.getItem() instanceof WindWingsItem) {
            return WindWingsItem.DEFAULT_STARS;
        }

        if (stack.getItem() instanceof PortableNutritionBagItem) {
            return PortableNutritionBagItem.DEFAULT_STARS;
        }

        if (stack.getItem() instanceof SeedDispensaryItem) {
            return SeedDispensaryItem.DEFAULT_STARS;
        }

        if (stack.getItem() instanceof KatheryneFigurineBlockItem) {
            return KatheryneFigurineBlockItem.DEFAULT_STARS;
        }

        return 0;
    }

    /** Mutates only this stack; clamped to 1..5. Use clearStars to restore its item default. */
    public static void setStars(ItemStack stack, int stars) {
        stack.set(TeyvatDelight.STARS.get(), clampStars(stars));
    }

    /** Unknown or absent qualities read as normal; quality currently grants no gameplay bonuses. */
    public static String getFoodQuality(ItemStack stack) {
        return normalizeQuality(stack.get(TeyvatDelight.FOOD_QUALITY.get()));
    }

    public static void setFoodQuality(ItemStack stack, String quality) {
        stack.set(TeyvatDelight.FOOD_QUALITY.get(), normalizeQuality(quality));
    }

    /** Whether a star component is present, including one supplied by the item prototype. */
    public static boolean hasStars(ItemStack stack) {
        return stack.has(TeyvatDelight.STARS.get());
    }

    public static void clearStars(ItemStack stack) {
        Integer defaults = stack.getItem().components().get(TeyvatDelight.STARS.get());
        if (defaults == null) stack.remove(TeyvatDelight.STARS.get());
        else stack.set(TeyvatDelight.STARS.get(), defaults);
    }

    public static boolean hasFoodQuality(ItemStack stack) {
        return stack.has(TeyvatDelight.FOOD_QUALITY.get());
    }

    public static void clearFoodQuality(ItemStack stack) {
        String defaults = stack.getItem().components().get(TeyvatDelight.FOOD_QUALITY.get());
        if (defaults == null) stack.remove(TeyvatDelight.FOOD_QUALITY.get());
        else stack.set(TeyvatDelight.FOOD_QUALITY.get(), defaults);
    }

    public static String normalizeQuality(String quality) {
        if (QUALITY_STRANGE.equals(quality)
                || QUALITY_DELICIOUS.equals(quality)
                || QUALITY_NORMAL.equals(quality)) {
            return quality;
        }
        return QUALITY_NORMAL;
    }

    public static void appendRarityTooltip(ItemStack stack, List<Component> tooltip) {
        appendRarityTooltip(getStars(stack), tooltip);
    }

    public static void appendRarityTooltip(ItemStack stack, List<Component> tooltip, int fallbackStars) {
        appendRarityTooltip(getStars(stack, fallbackStars), tooltip);
    }

    private static void appendRarityTooltip(int stars, List<Component> tooltip) {
        if (stars > 0) {
            tooltip.add(Component.translatable(
                    "tooltip.teyvatdelight.stars",
                    buildStars(stars)
            ).withStyle(getStarColor(stars)));
        }
    }

    public static void appendFoodQualityTooltip(ItemStack stack, List<Component> tooltip) {
        String quality = getFoodQuality(stack);
        if (!QUALITY_NORMAL.equals(quality)) {
            tooltip.add(Component.translatable(
                    "tooltip.teyvatdelight.food_quality." + normalizeQuality(quality)
            ));
        }
    }

    public static String buildStars(int stars) {
        return "★".repeat(clampStars(stars));
    }

    public static ChatFormatting getStarColor(int stars) {
        return switch (clampStars(stars)) {
            case 5 -> ChatFormatting.GOLD;
            case 4 -> ChatFormatting.LIGHT_PURPLE;
            case 3 -> ChatFormatting.BLUE;
            case 2 -> ChatFormatting.GREEN;
            default -> ChatFormatting.WHITE;
        };
    }
}
