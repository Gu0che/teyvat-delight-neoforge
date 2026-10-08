package com.guoche.teyvatdelight.item;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WindWingsCurios;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class WindWingsFlight {
    private static final float EXHAUSTION_PER_TICK = 0.04F;

    private WindWingsFlight() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isFallFlying() || !usingWings(player)) return;
        if (player.getFoodData().getFoodLevel() == 0) {
            player.stopFallFlying();
            return;
        }
        player.causeFoodExhaustion(EXHAUSTION_PER_TICK);
        if (player.getFoodData().getFoodLevel() == 0) player.stopFallFlying();
    }

    private static boolean usingWings(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(TeyvatDelight.WIND_WINGS.get())) return true;
        return !chest.is(Items.ELYTRA) && ModList.get().isLoaded("curios")
                && ModList.get().isLoaded("caelus") && !WindWingsCurios.getBackWings(player).isEmpty();
    }
}
