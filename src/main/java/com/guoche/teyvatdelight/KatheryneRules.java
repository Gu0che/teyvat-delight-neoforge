package com.guoche.teyvatdelight;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneRules} for new integrations. */
@Deprecated
public final class KatheryneRules {
    private KatheryneRules() {
    }

    public static int questRefreshTime() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.questRefreshTime();
    }

    public static int shopRefreshTime() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.shopRefreshTime();
    }

    public static long cycle(MinecraftServer server, int refreshTime) {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.cycle(server, refreshTime);
    }

    public static int secondsUntil(MinecraftServer server, int refreshTime) {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.secondsUntil(server, refreshTime);
    }

    public static boolean rotateQuests() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.rotateQuests();
    }

    public static List<Item> questTargets() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.questTargets();
    }

    public static List<StackAmount> questRewards() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.questRewards();
    }

    public static List<ShopOffer> shopOffers() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.shopOffers();
    }

    public static List<DailySlot> dailySlots() {
        return com.guoche.teyvatdelight.entity.katheryne.KatheryneRules.dailySlots();
    }

    public record StackAmount(Item item, int count) {
    }

    public record ShopOffer(String key, List<StackAmount> outputs, List<StackAmount> prices, int dailyLimit) {
    }

    public record DailySlot(String key, List<Item> pool, List<StackAmount> prices, int drawCount) {
    }
}
