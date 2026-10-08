package com.guoche.teyvatdelight;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.TeyvatMerchantCatalog} for new integrations. */
@Deprecated
public final class TeyvatMerchantCatalog {
    private TeyvatMerchantCatalog() {
    }

    public static final int PRICE = com.guoche.teyvatdelight.entity.katheryne.TeyvatMerchantCatalog.PRICE;

    public static final List<Supplier<? extends Item>> INGREDIENTS = com.guoche.teyvatdelight.entity.katheryne.TeyvatMerchantCatalog.INGREDIENTS;

    public static Item ingredient(int index) {
        return com.guoche.teyvatdelight.entity.katheryne.TeyvatMerchantCatalog.ingredient(index);
    }
}
