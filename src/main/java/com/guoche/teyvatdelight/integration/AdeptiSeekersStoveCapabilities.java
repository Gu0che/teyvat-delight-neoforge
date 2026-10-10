package com.guoche.teyvatdelight.integration;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.registry.ModBlockEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import vectorwing.farmersdelight.common.block.entity.inventory.CookingPotItemHandler;

@EventBusSubscriber(modid = TeyvatDelight.MODID)
public final class AdeptiSeekersStoveCapabilities {
    private AdeptiSeekersStoveCapabilities() {}

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.ADEPTI_SEEKERS_STOVE.get(),
                (stove, side) -> side == null ? stove.getInventory() : new CookingPotItemHandler(stove.getInventory(), side));
    }
}
