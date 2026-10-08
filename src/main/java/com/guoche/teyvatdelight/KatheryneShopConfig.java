package com.guoche.teyvatdelight;

import java.nio.file.Path;
import java.util.List;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig} for new integrations. */
@Deprecated
public final class KatheryneShopConfig {
    private KatheryneShopConfig() {
    }

    public static void initialize(Path configDirectory) {
        com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig.initialize(configDirectory);
    }

    public static List<RawOffer> offers() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig.offers();
    }

    public record RawStack(String item, int count) {
    }

    public record RawOffer(String id, List<RawStack> sell, List<RawStack> cost, int dailyLimit) {
    }
}
