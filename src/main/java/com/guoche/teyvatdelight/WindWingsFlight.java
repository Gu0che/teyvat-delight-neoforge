package com.guoche.teyvatdelight;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** @deprecated Use {@link com.guoche.teyvatdelight.item.WindWingsFlight} for new integrations. */
@Deprecated
public final class WindWingsFlight {
    private WindWingsFlight() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        com.guoche.teyvatdelight.item.WindWingsFlight.onPlayerTick(event);
    }
}
