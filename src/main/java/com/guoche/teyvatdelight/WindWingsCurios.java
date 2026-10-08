package com.guoche.teyvatdelight;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/** @deprecated Use {@link com.guoche.teyvatdelight.integration.curios.WindWingsCurios} for new integrations. */
@Deprecated
public final class WindWingsCurios {
    private WindWingsCurios() {
    }

    public static void register(FMLCommonSetupEvent event) {
        com.guoche.teyvatdelight.integration.curios.WindWingsCurios.register(event);
    }

    public static ItemStack getBackWings(Player player) {
        return com.guoche.teyvatdelight.integration.curios.WindWingsCurios.getBackWings(player);
    }
}
