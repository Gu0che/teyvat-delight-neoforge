package com.guoche.teyvatdelight;

import com.guoche.teyvatdelight.config.TeyvatDelightConfig;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig;
import com.guoche.teyvatdelight.integration.curios.WindWingsCurios;
import com.guoche.teyvatdelight.network.NutritionBagNetwork;
import com.guoche.teyvatdelight.registry.ModEvents;
import com.guoche.teyvatdelight.registry.ModRegistries;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(TeyvatDelight.MODID)
public final class TeyvatDelight {
    public static final String MODID = "teyvatdelight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TeyvatDelight(IEventBus modEventBus, ModContainer modContainer) {
        KatheryneShopConfig.initialize(FMLPaths.CONFIGDIR.get());
        modContainer.registerConfig(ModConfig.Type.COMMON, TeyvatDelightConfig.SPEC);
        KatheryneNetwork.register(modEventBus);
        NutritionBagNetwork.register(modEventBus);
        ModRegistries.register(modEventBus);
        ModEvents.register();
        if (ModList.get().isLoaded("curios") && ModList.get().isLoaded("caelus")) {
            modEventBus.addListener(WindWingsCurios::register);
        }
    }
}
