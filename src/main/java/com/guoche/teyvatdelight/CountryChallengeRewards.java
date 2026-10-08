package com.guoche.teyvatdelight;

import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

/** @deprecated Use {@link com.guoche.teyvatdelight.advancement.CountryChallengeRewards} for new integrations. */
@Deprecated
public final class CountryChallengeRewards {
    private CountryChallengeRewards() {
    }

    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        com.guoche.teyvatdelight.advancement.CountryChallengeRewards.onAdvancementEarned(event);
    }
}
