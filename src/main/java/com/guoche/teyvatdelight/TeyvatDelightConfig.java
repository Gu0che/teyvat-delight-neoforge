package com.guoche.teyvatdelight;

import net.neoforged.neoforge.common.ModConfigSpec;

/** @deprecated Use the config package for new integrations. */
@Deprecated
public final class TeyvatDelightConfig {
    public static final ModConfigSpec SPEC = com.guoche.teyvatdelight.config.TeyvatDelightConfig.SPEC;
    public static final ModConfigSpec.BooleanValue COUNTRY_CHALLENGE_WIND_WINGS = com.guoche.teyvatdelight.config.TeyvatDelightConfig.COUNTRY_CHALLENGE_WIND_WINGS;
    public static final ModConfigSpec.BooleanValue PRIMOGEMS_IN_CHESTS = com.guoche.teyvatdelight.config.TeyvatDelightConfig.PRIMOGEMS_IN_CHESTS;

    private TeyvatDelightConfig() {
    }
}
