package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/** Serializes commission state while keeping persistence details out of the state machine. */
final class CommissionBookStorage {
  private static final Gson JSON = new Gson();

  private CommissionBookStorage() {}

  static Map<UUID, CommissionBook.State> load(CompoundTag saved) {
    Map<UUID, CommissionBook.State> players = new HashMap<>();
    for (String key : saved.getAllKeys()) {
      try {
        CommissionBook.State state = JSON.fromJson(saved.getString(key), CommissionBook.State.class);
        if (state == null
            || state.quests == null
            || state.pending == null
            || state.rotation == null) continue;
        if (state.targetRotation == null) state.targetRotation = new HashMap<>();
        if (state.acceptedTemplates == null) state.acceptedTemplates = new HashSet<>();
        if (state.completedTemplates == null) state.completedTemplates = new HashSet<>();
        if (state.closedTemplates == null) state.closedTemplates = new HashSet<>();
        state.closedTemplates.removeIf(id -> id == null || !id.matches("[a-z0-9_:/.-]{1,128}"));
        if (state.completedCounts == null) state.completedCounts = new HashMap<>();
        state.completedCounts.entrySet().removeIf(e -> e.getKey() == null
            || !e.getKey().matches("[a-z0-9_:/.-]{1,128}")
            || e.getValue() == null || e.getValue() < 0);
        if (state.events == null) state.events = new HashMap<>();
        state.events.entrySet().removeIf(e -> e.getKey() == null
            || ResourceLocation.tryParse(e.getKey()) == null || e.getValue() == null || e.getValue() < 0);
        state.acceptedTemplates.addAll(state.rotation);
        for (CommissionBook.Instance q : state.quests) {
          if (q != null && q.template != null) state.acceptedTemplates.add(q.template);
          if (q != null && q.completionAdvancements == null) q.completionAdvancements = List.of();
          if (q != null && q.completionRules == null) q.completionRules = List.of();
          if (q != null && q.introduction == null) q.introduction = "";
        }
        state.acceptedTemplates.removeIf(id -> id == null || !id.matches("[a-z0-9_:/.-]{1,128}"));
        state.quests.removeIf(q -> !CommissionBook.valid(q));
        // Acceptance/rotation history cannot prove completion in older saves.
        for (CommissionBook.Instance q : state.quests)
          if (q.done) state.completedTemplates.add(q.template);
        state.completedTemplates.removeIf(id -> id == null || !id.matches("[a-z0-9_:/.-]{1,128}"));
        state.pending.removeIf(rewards -> !CommissionBook.validRewards(rewards) || rewards.isEmpty());
        if (state.nextBonus != null && !CommissionBook.validRewards(state.nextBonus)) state.nextBonus = null;
        state.rotation.removeIf(id -> id == null || id.length() > 128);
        state.targetRotation.entrySet().removeIf(
            e -> e.getKey() == null || e.getKey().length() > 256 || e.getValue() == null);
        state.remainder = Math.max(0, state.remainder);
        state.totalCompleted = Math.max(0L, state.totalCompleted);
        state.milestoneEvery = Math.max(0, state.milestoneEvery);
        players.put(UUID.fromString(key), state);
      } catch (RuntimeException e) {
        TeyvatDelight.LOGGER.warn("Invalid commission record {}", key);
      }
    }
    return players;
  }

  static CompoundTag save(Map<UUID, CommissionBook.State> players) {
    CompoundTag tag = new CompoundTag();
    players.forEach((id, state) -> tag.putString(id.toString(), JSON.toJson(state)));
    return tag;
  }
}
