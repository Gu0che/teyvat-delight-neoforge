package com.guoche.teyvatdelight;

import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/** @deprecated Event implementations are grouped by responsibility. */
@Deprecated
public final class TeyvatDelightEvents {
    private TeyvatDelightEvents() {
    }

    public static void harvestTeyvatCropBeforeOtherRightClickHandlers(PlayerInteractEvent.RightClickBlock event) {
        com.guoche.teyvatdelight.crop.CropEvents.harvestTeyvatCropBeforeOtherRightClickHandlers(event);
    }

    public static void syncGlazeLilies(LevelTickEvent.Post event) {
        com.guoche.teyvatdelight.crop.CropEvents.syncGlazeLilies(event);
    }

    public static void registerTeyvatMerchantTrades(VillagerTradesEvent event) {
        com.guoche.teyvatdelight.entity.katheryne.MerchantEvents.registerTeyvatMerchantTrades(event);
    }

    public static void arrangeAdvancementDisplay(AddReloadListenerEvent event) {
        com.guoche.teyvatdelight.advancement.AdvancementEvents.arrangeAdvancementDisplay(event);
    }

    public static void configurePrimogemChestLoot(LootTableLoadEvent event) {
        com.guoche.teyvatdelight.loot.LootEvents.configurePrimogemChestLoot(event);
    }

    public static void addItemDescription(ItemTooltipEvent event) {
        com.guoche.teyvatdelight.client.ItemDescriptionTooltips.addItemDescription(event);
    }
}
