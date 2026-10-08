package com.guoche.teyvatdelight.entity.katheryne;

import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

final class KatheryneDataReloadListener
    extends SimplePreparableReloadListener<KatheryneDataPack.Catalog> {
  @Override
  protected KatheryneDataPack.Catalog prepare(ResourceManager manager, ProfilerFiller profiler) {
    try {
      return KatheryneDataPack.resources(manager);
    } catch (IOException | RuntimeException e) {
      throw new IllegalArgumentException("Cannot read Katheryne data packs", e);
    }
  }

  @Override
  protected void apply(
      KatheryneDataPack.Catalog catalog, ResourceManager manager, ProfilerFiller profiler) {
    // Tags are bound after listener apply; registry validation and the atomic swap happen then.
    KatheryneDataPack.stage(catalog);
  }
}
