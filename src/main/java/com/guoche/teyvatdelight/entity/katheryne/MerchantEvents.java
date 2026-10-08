package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.TeyvatMerchantCatalog;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

public final class MerchantEvents {


    private MerchantEvents() {
    }

    public static void registerTeyvatMerchantTrades(VillagerTradesEvent event) {
        if (event.getType() != TeyvatDelight.TEYVAT_MERCHANT_PROFESSION.get()) {
            return;
        }
        Int2ObjectMap<List<ItemListing>> trades = event.getTrades();
        int maxUses = 16;
        float priceMult = 0.05F;
        int xp = 250;

        for (int i = 0; i < TeyvatMerchantCatalog.INGREDIENTS.size(); i++) {
            trades.get(i / 2 + 1).add(new BasicItemListing(
                    new ItemStack(TeyvatDelight.MORA.get(), TeyvatMerchantCatalog.PRICE),
                    new ItemStack(TeyvatMerchantCatalog.ingredient(i)), maxUses, xp, priceMult));
        }
    }

}
