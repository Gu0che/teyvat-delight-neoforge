package com.guoche.teyvatdelight.advancement;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.config.TeyvatDelightConfig;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

public final class CountryChallengeRewards {
    private static final Map<String, Supplier<? extends Item>> WIND_WINGS_BY_CHALLENGE = Map.of(
            "teyvatdelight:main/outlander_who_caught_the_wind", TeyvatDelight.WIND_WINGS::get
    );

    private CountryChallengeRewards() {
    }

    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !TeyvatDelightConfig.COUNTRY_CHALLENGE_WIND_WINGS.get()) {
            return;
        }

        Supplier<? extends Item> reward = WIND_WINGS_BY_CHALLENGE.get(event.getAdvancement().id().toString());
        if (reward != null) {
            ItemStack stack = new ItemStack(reward.get());
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
