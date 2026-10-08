package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID)
public final class KatheryneLocalPack {
  private KatheryneLocalPack() {}

  @SubscribeEvent
  public static void packs(AddPackFindersEvent event) {
    if (event.getPackType() != PackType.SERVER_DATA || event.isTrusted()) return;
    event.addRepositorySource(consumer -> {
      var path = FMLPaths.CONFIGDIR.get().resolve(KatheryneDataPack.PACK_DIRECTORY);
      var pack = Pack.readMetaAndCreate(
          new PackLocationInfo("teyvatdelight/katheryne-local",
              Component.literal("凯瑟琳本地定义 / Katheryne local definitions"),
              PackSource.DEFAULT, Optional.empty()),
          new PathPackResources.PathResourcesSupplier(path),
          PackType.SERVER_DATA, new PackSelectionConfig(true, Pack.Position.TOP, false));
      if (pack != null) consumer.accept(pack);
    });
  }
}
