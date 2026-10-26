package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

public final class TeyvatMerchantCatalog {
    public static final int PRICE = 3;
    public static final List<Supplier<? extends Item>> INGREDIENTS = List.of(
            com.guoche.teyvatdelight.registry.ModItems.PEPPER, com.guoche.teyvatdelight.registry.ModItems.SALT,
            com.guoche.teyvatdelight.registry.ModItems.TOFU, com.guoche.teyvatdelight.registry.ModItems.GLABROUS_BEANS,
            com.guoche.teyvatdelight.registry.ModItems.ALMOND, com.guoche.teyvatdelight.registry.ModItems.SAUSAGE,
            com.guoche.teyvatdelight.registry.ModItems.CHENYU_TEA, com.guoche.teyvatdelight.registry.ModItems.MATSUTAKE,
            com.guoche.teyvatdelight.registry.ModItems.SHRIMP_MEAT, com.guoche.teyvatdelight.registry.ModItems.CRAB);

    private TeyvatMerchantCatalog() {
    }

    public static Item ingredient(int index) {
        return INGREDIENTS.get(index).get();
    }
}
