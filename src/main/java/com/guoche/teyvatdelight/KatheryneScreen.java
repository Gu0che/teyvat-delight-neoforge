package com.guoche.teyvatdelight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.katheryne.KatheryneScreen} for new integrations. */
@Deprecated
public class KatheryneScreen extends com.guoche.teyvatdelight.client.katheryne.KatheryneScreen {
    public KatheryneScreen(KatheryneMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
