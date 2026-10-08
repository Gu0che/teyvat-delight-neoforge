package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.TeyvatMineralPatchConfiguration;
import com.guoche.teyvatdelight.TeyvatMineralPatchFeature;
import com.guoche.teyvatdelight.WildTeyvatCropPatchConfiguration;
import com.guoche.teyvatdelight.WildTeyvatCropPatchFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Owns feature type registrations; holders are resolved by deferred suppliers. */
public final class ModWorldgen {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TeyvatDelight.MODID);

    public static final DeferredHolder<Feature<?>, Feature<WildTeyvatCropPatchConfiguration>> WILD_CROP_PATCH_FEATURE = FEATURES.register(
            "wild_crop_patch",
            () -> new WildTeyvatCropPatchFeature(WildTeyvatCropPatchConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<TeyvatMineralPatchConfiguration>> MINERAL_PATCH_FEATURE = FEATURES.register(
            "mineral_patch",
            () -> new TeyvatMineralPatchFeature(TeyvatMineralPatchConfiguration.CODEC)
    );

    private ModWorldgen() {
    }

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}
