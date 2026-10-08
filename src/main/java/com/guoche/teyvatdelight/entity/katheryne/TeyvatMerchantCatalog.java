package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

public final class TeyvatMerchantCatalog {
    public static final int PRICE = 3;
    public static final List<Supplier<? extends Item>> INGREDIENTS = List.of(
            TeyvatDelight.PEPPER, TeyvatDelight.SALT,
            TeyvatDelight.TOFU, TeyvatDelight.GLABROUS_BEANS,
            TeyvatDelight.ALMOND, TeyvatDelight.SAUSAGE,
            TeyvatDelight.CHENYU_TEA, TeyvatDelight.MATSUTAKE,
            TeyvatDelight.SHRIMP_MEAT, TeyvatDelight.CRAB);

    private TeyvatMerchantCatalog() {
    }

    public static Item ingredient(int index) {
        return INGREDIENTS.get(index).get();
    }
}
