package com.guoche.teyvatdelight.loot;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.config.TeyvatDelightConfig;
import java.util.Set;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public final class LootEvents {
    private static final Set<String> PRIMOGEM_CHEST_TABLES = Set.of(
            "chests/mora_primogem_low", "chests/mora_primogem_mid", "chests/mora_primogem_high");

    private LootEvents() {
    }

    public static void configurePrimogemChestLoot(LootTableLoadEvent event) {
        if (!TeyvatDelightConfig.PRIMOGEMS_IN_CHESTS.get()
                && TeyvatDelight.MODID.equals(event.getName().getNamespace())
                && PRIMOGEM_CHEST_TABLES.contains(event.getName().getPath())) {
            event.getTable().removePool("teyvatdelight:primogem");
        }
    }

}
