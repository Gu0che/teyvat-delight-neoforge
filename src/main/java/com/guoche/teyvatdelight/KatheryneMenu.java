package com.guoche.teyvatdelight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneMenu} for new integrations. */
@Deprecated
public class KatheryneMenu extends com.guoche.teyvatdelight.entity.katheryne.KatheryneMenu {
    public KatheryneMenu(int id, Inventory inventory) {
        super(id, inventory);
    }

    public KatheryneMenu(int id, Inventory inventory, ServerPlayer player, KatheryneEntity entity) {
        super(id, inventory, player, entity);
    }

    public KatheryneMenu(int id, net.minecraft.world.entity.player.Inventory inventory,
            net.minecraft.server.level.ServerPlayer player, com.guoche.teyvatdelight.entity.katheryne.KatheryneEntity entity) {
        super(id, inventory, player, entity);
    }
}
