package com.guoche.teyvatdelight;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageSpawner} for new integrations. */
@Deprecated
public final class KatheryneVillageSpawner {
    private KatheryneVillageSpawner() {
    }

    public static void register() {
        com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageSpawner.register();
    }
}
