package com.guoche.teyvatdelight;

import net.minecraft.server.MinecraftServer;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageData} for new integrations. */
@Deprecated
public class KatheryneVillageData extends com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageData {
    public static KatheryneVillageData get(net.minecraft.server.MinecraftServer server) {
        return (KatheryneVillageData) com.guoche.teyvatdelight.entity.katheryne.KatheryneVillageData.get(server);
    }
}
