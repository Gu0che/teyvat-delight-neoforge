package com.guoche.teyvatdelight;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneData} for new integrations. */
@Deprecated
public class KatheryneData extends com.guoche.teyvatdelight.entity.katheryne.KatheryneData {
    public record Quest(long cycle, ResourceLocation dish, boolean completed) {
    }

    public record DailyOffer(List<ItemStack> items, boolean bought) {
    }

    public static KatheryneData get(net.minecraft.server.MinecraftServer server) {
        return (KatheryneData) com.guoche.teyvatdelight.entity.katheryne.KatheryneData.get(server);
    }
}
