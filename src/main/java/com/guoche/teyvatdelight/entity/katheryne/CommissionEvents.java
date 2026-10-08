package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID)
public final class CommissionEvents {
  private record Death(LivingEntity target, ServerPlayer player, int tick) {}

  private static final List<Death> deaths = new ArrayList<>();

  private CommissionEvents() {}

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void death(LivingDeathEvent event) {
    if (event.getSource().getEntity() instanceof ServerPlayer player
        && !(player instanceof FakePlayer)
        && deaths.stream().noneMatch(d -> d.target() == event.getEntity()))
      deaths.add(new Death(event.getEntity(), player, player.server.getTickCount()));
  }

  @SubscribeEvent
  public static void player(PlayerTickEvent.Post event) {

    if (!(event.getEntity() instanceof ServerPlayer player)
        || player instanceof FakePlayer) return;
    KatheryneData data = KatheryneData.get(player.server);
    data.commissions.pollConditions(player);
    CommissionGameplayEvents.tick(player);
    if (player.tickCount % 20 != 0) return;
    data.commissionState(player);
    data.commissions.stats(player, null);
    if (player.containerMenu instanceof KatheryneMenu menu) menu.sendCommissionUpdate();
  }

  @SubscribeEvent
  public static void tick(ServerTickEvent.Post event) {
    com.guoche.teyvatdelight.advancement.CommissionAdvancements.flushVisibility();

    deaths.removeIf(
        d -> {
          if (event.getServer().getTickCount() <= d.tick()) return false;
          if (d.target().isDeadOrDying() && d.target().getHealth() <= 0) {
            KatheryneData data = KatheryneData.get(d.player().server);
            data.commissionState(d.player());
            data.commissions.kill(d.player(), d.target().getType());
          }
          return true;
        });
  }

  @SubscribeEvent
  public static void login(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer p) {
      CommissionGameplayEvents.reset(p);
      KatheryneData.get(p.server).commissionState(p);
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.update(p);
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.scheduleVisibility(p);
    }
  }

  @SubscribeEvent
  public static void advancement(AdvancementEvent.AdvancementEarnEvent event) {
    if (event.getEntity() instanceof ServerPlayer p && !(p instanceof FakePlayer)) {
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.update(p);
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.scheduleVisibility(p, event.getAdvancement().id());
      var data = KatheryneData.get(p.server);
      data.commissions.checkUrgent(p);
      if (p.containerMenu instanceof KatheryneMenu menu) menu.sendCommissionUpdate();
    }
  }

  @SubscribeEvent
  public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
    if (event.getEntity() instanceof ServerPlayer p) {
      CommissionGameplayEvents.reset(p);
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.forget(p);
      KatheryneData.get(p.server).commissions.logout(p);
    }
  }

  @SubscribeEvent
  public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    if (event.getEntity() instanceof ServerPlayer p) CommissionGameplayEvents.reset(p);
  }

  @SubscribeEvent
  public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
    if (event.getEntity() instanceof ServerPlayer p) CommissionGameplayEvents.reset(p);
  }

  @SubscribeEvent
  public static void dataPacks(AddReloadListenerEvent event) {
    event.addListener(new KatheryneDataReloadListener());
  }

  @SubscribeEvent
  public static void syncAdvancements(net.neoforged.neoforge.event.OnDatapackSyncEvent event) {
    if (event.getPlayer() != null)
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.scheduleVisibility(event.getPlayer());
    else for (var player : event.getPlayerList().getPlayers())
      com.guoche.teyvatdelight.advancement.CommissionAdvancements.scheduleVisibility(player);
  }

  @SubscribeEvent
  public static void tags(TagsUpdatedEvent event) {
    if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
      KatheryneDataPack.applyPending();
      CommissionConfig.invalidate();
      if (KatheryneShopConfig.registriesReady) validate();
    }
  }

  @SubscribeEvent
  public static void started(ServerStartedEvent event) {
    KatheryneShopConfig.registriesReady = true;
    KatheryneDataPack.applyPending();
    validate();
  }

  private static void validate() {
    try {
      CommissionConfig.validateRegistries(CommissionConfig.settings());
      CommissionConfig.validateShop(
          CommissionConfig.settings(),
          KatheryneShopConfig.offers(),
          KatheryneShopConfig.settings());
      ShopDefinitions.validate(
          CommissionConfig.settings(),
          ShopDefinitions.shops(),
          KatheryneShopConfig.settings().equipment());
      CommissionConfig.runtimeValid = true;
    } catch (RuntimeException e) {
      TeyvatDelight.LOGGER.error(
          "Invalid Katheryne registry references; commissions disabled until configuration is"
              + " fixed",
          e);
      CommissionConfig.runtimeValid = false;
    }
  }

  @SubscribeEvent
  public static void stopped(ServerStoppedEvent event) {
    com.guoche.teyvatdelight.advancement.CommissionAdvancements.clearVisibility();
    CommissionGameplayEvents.clear();
    deaths.clear();
    KatheryneDataPack.stopped();
    KatheryneShopConfig.registriesReady = false;
    CommissionConfig.invalidate();
  }

  @SubscribeEvent
  public static void commands(RegisterCommandsEvent event) {
    event
        .getDispatcher()
        .register(
            Commands.literal("teyvatdelight")
                .requires(s -> s.hasPermission(2))
                .then(
                    Commands.literal("katheryne")
                        .then(KatheryneRefreshCommand.command())
                        .then(KatheryneEventCommand.command())
                        .then(
                            Commands.literal("reload")
                                .executes(
                                    context -> {
                                      boolean success = KatheryneShopConfig.reload();
                                      if (success)
                                        context
                                            .getSource()
                                            .sendSuccess(
                                                () ->
                                                    Component.literal(
                                                        "Katheryne config reloaded / 凯瑟琳配置已重载"),
                                                true);
                                      else
                                        context
                                            .getSource()
                                            .sendFailure(
                                                Component.literal(KatheryneShopConfig.lastError));
                                      return success ? 1 : 0;
                                    }))
                        .then(
                            Commands.literal("export")
                                .executes(context -> {
                                  try {
                                    var destination = KatheryneDataPack.exportExamples();
                                    context.getSource().sendSuccess(
                                        () -> Component.literal("Examples exported / 示例已导出: " + destination),
                                        false);
                                    return 1;
                                  } catch (java.io.IOException | RuntimeException exception) {
                                    context.getSource().sendFailure(
                                        Component.literal(String.valueOf(exception.getMessage())));
                                    return 0;
                                  }
                                }))));
  }
}
