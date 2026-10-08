package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.KatheryneSounds;
import net.neoforged.bus.api.IEventBus;

/** Attaches each registry exactly once, after all dish modules have been loaded. */
public final class ModRegistries {
    private ModRegistries() {
    }

    public static void register(IEventBus modEventBus) {
        ModDataComponents.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModWorldgen.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        KatheryneSounds.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModVillage.register(modEventBus);
    }
}
