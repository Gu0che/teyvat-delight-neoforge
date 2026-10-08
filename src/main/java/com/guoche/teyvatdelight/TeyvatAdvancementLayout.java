package com.guoche.teyvatdelight;

import net.minecraft.server.ServerAdvancementManager;

/** @deprecated Use {@link com.guoche.teyvatdelight.advancement.TeyvatAdvancementLayout} for new integrations. */
@Deprecated
public final class TeyvatAdvancementLayout {
    private TeyvatAdvancementLayout() {
    }

    public static void arrange(ServerAdvancementManager manager) {
        com.guoche.teyvatdelight.advancement.TeyvatAdvancementLayout.arrange(manager);
    }
}
