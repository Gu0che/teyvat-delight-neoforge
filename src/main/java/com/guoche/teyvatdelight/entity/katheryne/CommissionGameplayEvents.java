package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID)
public final class CommissionGameplayEvents {
  public static final String FISHING = "teyvatdelight:successful_fishing";
  public static final String FIREWORKS = "teyvatdelight:launch_firework";
  public static final String LONGSHOT = "teyvatdelight:bow_longshot";
  public static final String FREE_FALL = "teyvatdelight:free_fall_100";
  private static final String ORIGIN = "teyvatdelight_bow_origin";
  private static final String COUNTED = "teyvatdelight_launch_counted";
  private record Fall(String token, CommissionFallTracker tracker) {}
  private static final Map<UUID, Fall> falls = new HashMap<>();

  private CommissionGameplayEvents() {}

  private static ServerPlayer owner(Projectile projectile) {
    return projectile.getOwner() instanceof ServerPlayer player && !(player instanceof FakePlayer)
        ? player : null;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void fish(ItemFishedEvent event) {
    if (!event.isCanceled() && event.getEntity() instanceof ServerPlayer player
        && !(player instanceof FakePlayer) && event.getDrops().stream().anyMatch(s -> !s.isEmpty()))
      record(player, FISHING);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void spawn(EntityJoinLevelEvent event) {
    if (event.isCanceled() || event.loadedFromDisk() || event.getLevel().isClientSide()
        || !(event.getEntity() instanceof Projectile projectile)) return;
    ServerPlayer player = owner(projectile);
    if (player == null) return;
    var data = projectile.getPersistentData();
    if (projectile instanceof FireworkRocketEntity) {
      if (!data.getBoolean(COUNTED)) {
        data.putBoolean(COUNTED, true);
        record(player, FIREWORKS);
      }
    } else if (!data.contains(ORIGIN) && projectile instanceof AbstractArrow arrow && !(projectile instanceof ThrownTrident)
        && !arrow.shotFromCrossbow()
        && (arrow.getWeaponItem() != null ? isBow(arrow.getWeaponItem())
            : isBow(player.getUseItem()) || isBow(player.getMainHandItem()) || isBow(player.getOffhandItem()))) {
      var origin = new net.minecraft.nbt.CompoundTag();
      origin.putDouble("x", projectile.getX());
      origin.putDouble("y", projectile.getY());
      origin.putDouble("z", projectile.getZ());
      data.put(ORIGIN, origin);
    }
  }

  static boolean isBow(ItemStack stack) {
    return !stack.isEmpty() && !(stack.getItem() instanceof CrossbowItem)
        && (stack.getItem() instanceof BowItem || stack.getUseAnimation() == UseAnim.BOW);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void hit(ProjectileImpactEvent event) {
    if (event.isCanceled() || !(event.getRayTraceResult() instanceof EntityHitResult hit)
        || !(hit.getEntity() instanceof LivingEntity)) return;
    Projectile projectile = event.getProjectile();
    ServerPlayer player = owner(projectile);
    var data = projectile.getPersistentData();
    if (player == null || !data.contains(ORIGIN) || data.getBoolean(ORIGIN + "_counted")) return;
    var origin = data.getCompound(ORIGIN);
    Vec3 position = new Vec3(origin.getDouble("x"), origin.getDouble("y"), origin.getDouble("z"));
    if (position.distanceToSqr(hit.getLocation()) >= 30.0 * 30.0) {
      data.putBoolean(ORIGIN + "_counted", true);
      record(player, LONGSHOT);
    }
  }

  static void record(ServerPlayer player, String event) {
    var data = KatheryneData.get(player.server);
    data.commissions.advance(player, "event", event, 1);
    if (player.containerMenu instanceof KatheryneMenu menu) menu.sendCommissionUpdate();
  }

  public static void tick(ServerPlayer player) {
    var book = KatheryneData.get(player.server).commissions;
    String token = book.activeEventToken(player, FREE_FALL);
    if (token.isEmpty()) { reset(player); return; }
    Fall fall = falls.get(player.getUUID());
    if (fall == null || !fall.token().equals(token)) {
      fall = new Fall(token, new CommissionFallTracker());
      falls.put(player.getUUID(), fall);
    }
    boolean falling = !player.onGround() && !player.isInWater() && !player.isInLava()
        && !player.isFallFlying() && !player.getAbilities().flying && !player.isPassenger()
        && !player.onClimbable() && !player.isDeadOrDying();
    if (fall.tracker().sample(player.getY(), falling,
        Math.max(16.0, Math.abs(player.getDeltaMovement().y) + 5.0))) record(player, FREE_FALL);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
  public static void land(LivingFallEvent event) {
    if (event.getEntity() instanceof ServerPlayer player
        && !(player instanceof FakePlayer)) {
      Fall fall = falls.get(player.getUUID());
      if (fall != null && fall.token().equals(
          KatheryneData.get(player.server).commissions.activeEventToken(player, FREE_FALL))
          && fall.tracker().sample(player.getY(), true,
          Math.max(16.0, Math.abs(player.getDeltaMovement().y) + 5.0))) record(player, FREE_FALL);
      reset(player);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void teleport(EntityTeleportEvent event) {
    if (!event.isCanceled() && event.getEntity() instanceof ServerPlayer player) reset(player);
  }

  public static void reset(ServerPlayer player) { falls.remove(player.getUUID()); }
  public static void clear() { falls.clear(); }
}
