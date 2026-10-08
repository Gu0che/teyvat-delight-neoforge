package com.guoche.teyvatdelight.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Commission stories award criteria independently; earning one never grants the whole challenge. */
public final class CommissionAdvancements {
  private CommissionAdvancements() {}

  private static final java.util.Set<ServerPlayer> pendingVisibility = new java.util.HashSet<>();

  public static void scheduleVisibility(ServerPlayer player) {
    pendingVisibility.add(player);
  }

  public static void scheduleVisibility(ServerPlayer player, ResourceLocation earned) {
    var challenge = player.server.getAdvancements().get(ResourceLocation.parse("teyvatdelight:main/all_commission_stories"));
    if (challenge != null && (earned.toString().equals("teyvatdelight:main/all_commission_stories")
        || earned.toString().equals("teyvatdelight:main/welcome_adventurers_guild")
        || challenge.value().criteria().containsKey(earned.toString())))
      scheduleVisibility(player);
  }

  public static void forget(ServerPlayer player) {
    pendingVisibility.remove(player);
  }

  public static void clearVisibility() {
    pendingVisibility.clear();
  }

  public static void flushVisibility() {
    var queued = java.util.List.copyOf(pendingVisibility);
    pendingVisibility.clear();
    for (var player : queued) {
      var packet = visibilityPacket(player);
      if (packet == null || player.connection == null) continue;
      // Send after vanilla visibility changes, without mutating shared advancement definitions.
      player.getAdvancements().flushDirty(player);
      player.connection.send(packet);
    }
  }

  public static net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket visibilityPacket(
      ServerPlayer player) {
    var manager = player.server.getAdvancements();
    var challenge = manager.get(ResourceLocation.parse("teyvatdelight:main/all_commission_stories"));
    if (challenge == null) return null;
    java.util.List<net.minecraft.advancements.AdvancementHolder> originals = new java.util.ArrayList<>();
    originals.add(challenge);
    var welcome = manager.get(ResourceLocation.parse("teyvatdelight:main/welcome_adventurers_guild"));
    if (welcome != null) originals.add(welcome);
    for (var criterion : challenge.value().criteria().keySet()) {
      var id = ResourceLocation.tryParse(criterion);
      var story = id == null ? null : manager.get(id);
      if (story != null) originals.add(story);
    }
    if (originals.stream().noneMatch(a -> player.getAdvancements().getOrStartProgress(a).isDone()))
      return null;
    var progress = new java.util.LinkedHashMap<ResourceLocation, net.minecraft.advancements.AdvancementProgress>();
    var added = new java.util.ArrayList<net.minecraft.advancements.AdvancementHolder>();
    for (var holder : originals) {
      var source = holder.value();
      var status = player.getAdvancements().getOrStartProgress(holder);
      progress.put(holder.id(), status);
      var display = source.display().map(d -> {
        var copy = new net.minecraft.advancements.DisplayInfo(d.getIcon().copy(), d.getTitle(),
            d.getDescription(), d.getBackground(), d.getType(), d.shouldShowToast() && !status.isDone(),
            d.shouldAnnounceChat(), false);
        copy.setLocation(d.getX(), d.getY());
        return copy;
      });
      var copy = new net.minecraft.advancements.Advancement(source.parent(), display, source.rewards(),
          source.criteria(), source.requirements(), source.sendsTelemetryEvent(), source.name());
      added.add(new net.minecraft.advancements.AdvancementHolder(holder.id(), copy));
    }
    return new net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket(
        false, added, progress.keySet(), progress);
  }

  public static void update(ServerPlayer player) {
    var challenge = player.server.getAdvancements().get(ResourceLocation.parse("teyvatdelight:main/all_commission_stories"));
    if (challenge == null || player.getAdvancements().getOrStartProgress(challenge).isDone()) return;
    for (String criterion : java.util.stream.StreamSupport.stream(
        player.getAdvancements().getOrStartProgress(challenge).getRemainingCriteria().spliterator(), false).toList()) {
      var id = ResourceLocation.tryParse(criterion);
      var story = id == null ? null : player.server.getAdvancements().get(id);
      if (story != null && player.getAdvancements().getOrStartProgress(story).isDone())
        player.getAdvancements().award(challenge, criterion);
    }
  }
}
