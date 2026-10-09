package com.guoche.teyvatdelight.integration.artifacts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/** Optional bridge. Resolve the artifact mod only when an enabled offer requests it. */
public final class ArtifactShopIntegration {
  private static Class<?> artifactItem;
  private static Method setStars, ensureStars;

  private ArtifactShopIntegration() {}

  public static synchronized void validate() {
    if (!ModList.get().isLoaded("teyvat_artifacts"))
      throw new IllegalArgumentException("artifactStars requires teyvat_artifacts; add a mod_loaded condition");
    if (ensureStars != null) return;
    try {
      artifactItem = Class.forName("com.guoche.teyvat_artifacts.ArtifactItem");
      Class<?> data = Class.forName("com.guoche.teyvat_artifacts.ArtifactItemData");
      setStars = data.getMethod("setStars", ItemStack.class, int.class);
      ensureStars = data.getMethod("ensureStars", ItemStack.class, artifactItem, RandomSource.class);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Unsupported Teyvat Artifacts item initialization API", e);
    }
  }

  public static ItemStack initialize(ItemStack stack, int stars, RandomSource random) {
    if (stars == 0) return stack;
    validate();
    if (!artifactItem.isInstance(stack.getItem()))
      throw new IllegalArgumentException("artifactStars can only initialize artifact items: " + stack);
    try {
      setStars.invoke(null, stack, stars);
      ensureStars.invoke(null, stack, stack.getItem(), random);
      return stack;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("Cannot initialize artifact shop product", e);
    }
  }

  public static boolean isArtifact(net.minecraft.world.item.Item item) {
    validate();
    return artifactItem.isInstance(item);
  }
}
