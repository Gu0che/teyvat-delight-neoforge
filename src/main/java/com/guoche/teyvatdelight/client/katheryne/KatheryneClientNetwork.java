package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.KatheryneMenu;
import com.guoche.teyvatdelight.KatheryneSnapshot;
import net.minecraft.client.Minecraft;

public final class KatheryneClientNetwork {
  private KatheryneClientNetwork() {}

  public static void sendAction(int menuId, int kind, int index) {
    var player = Minecraft.getInstance().player;
    if (player == null
        || !(player.containerMenu instanceof KatheryneMenu menu)
        || menu.containerId != menuId) return;
    String key =
        kind == 1
            ? menu.shopKey(index)
            : kind == 2 && index >= 0 && index < menu.snapshot().daily().size()
                ? menu.snapshot().daily().get(index).key()
                : kind == 0 && index >= 0 && index < menu.commissionView().quests().size()
                    ? menu.commissionView().quests().get(index).id()
                    : "";
    com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork.sendAction(
        menuId, kind, index, menu.snapshot().revision(), key);
  }

  public static void receive(KatheryneSnapshot snapshot) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.player != null && minecraft.player.containerMenu instanceof KatheryneMenu menu) {
      menu.receive(snapshot);
    }
  }
}
