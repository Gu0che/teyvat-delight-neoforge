package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.advancement.AdvancementEvents;
import com.guoche.teyvatdelight.advancement.CountryChallengeRewards;
import com.guoche.teyvatdelight.crop.CropEvents;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageSpawner;
import com.guoche.teyvatdelight.entity.katheryne.MerchantEvents;
import com.guoche.teyvatdelight.item.WindWingsFlight;
import com.guoche.teyvatdelight.loot.LootEvents;
import com.guoche.teyvatdelight.worldgen.MufengVillageGeneration;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

public final class ModEvents {
    private ModEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, CropEvents::harvestTeyvatCropBeforeOtherRightClickHandlers);
        NeoForge.EVENT_BUS.addListener(CropEvents::syncGlazeLilies);
        NeoForge.EVENT_BUS.addListener(MerchantEvents::registerTeyvatMerchantTrades);
        NeoForge.EVENT_BUS.addListener(LootEvents::configurePrimogemChestLoot);
        NeoForge.EVENT_BUS.addListener(AdvancementEvents::arrangeAdvancementDisplay);
        NeoForge.EVENT_BUS.addListener(CountryChallengeRewards::onAdvancementEarned);
        NeoForge.EVENT_BUS.addListener(WindWingsFlight::onPlayerTick);
        KatheryneVillageSpawner.register();
        MufengVillageGeneration.register();
    }
}
