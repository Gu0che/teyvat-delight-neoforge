package com.guoche.teyvatdelight.client.config;

import com.guoche.teyvatdelight.TeyvatDelight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public final class ConfigScreenRegistration {
  private ConfigScreenRegistration() {}
  @SubscribeEvent
  public static void setup(FMLClientSetupEvent event) {
    if (!ModList.get().isLoaded("cloth_config")) return;
    ModList.get().getModContainerById(TeyvatDelight.MODID).orElseThrow()
        .registerExtensionPoint(IConfigScreenFactory.class,
            (container, parent) -> KatheryneEditorScreens.open(parent, FMLPaths.CONFIGDIR.get()));
  }
}
