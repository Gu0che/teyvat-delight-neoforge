package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Instance;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Objective;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.ObjectiveView;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.Snapshot;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.State;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook.View;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Builds client-facing commission views without mixing presentation with state mutations. */
final class CommissionBookView {
  private static final Gson JSON = new Gson();

  private CommissionBookView() {}

  static Component progressMessage(Instance q, Objective o) {
    String text =
        q.ready()
            ? "gui.teyvatdelight.katheryne.ready_named_message"
            : q.viewUnlocked() ? "gui.teyvatdelight.katheryne.view_unlocked_named_message"
                : "gui.teyvatdelight.katheryne.progress_message";
    Component title =
        q.title.isEmpty()
            ? Component.translatable("gui.teyvatdelight.katheryne.auto_title")
            : Component.translatable(q.title);
    return Component.translatable(text, title, o.progress, o.count);
  }

  static Snapshot snapshot(ServerPlayer p, State s, String message) {
    return snapshot(p, s, message, 0);
  }

  static Snapshot snapshot(ServerPlayer p, State s, String message, int requestedOffset) {
    List<View> views = new ArrayList<>();
    int offset = Math.max(0, Math.min(requestedOffset, Math.max(0, s.quests.size() - 1)));
    int bytes = 0;
    for (int index = offset; index < s.quests.size() && views.size() < 32; index++) {
      Instance q = s.quests.get(index);
      var plan = CommissionItemPlan.create(p.getInventory(), q.objectives);
      List<ObjectiveView> objectives = new ArrayList<>();
      for (int goal = 0; goal < q.objectives.size(); goal++) {
        Objective o = q.objectives.get(goal);
        int progress = q.done ? o.count : o.progress;
        if (!q.done && !q.submitted && o.type.equals("item")) {
          progress = plan.progress[goal];
        }
        objectives.add(new ObjectiveView(o.type, o.target, o.count, progress,
            o.alternatives == null ? List.of() : o.alternatives.stream()
                .limit(Math.max(1, 256 / q.objectives.size())).toList(), o.name, o.tag));
      }
      View view = new View(
              q.id,
              q.title,
              q.description,
              q.icon,
              q.done,
              q.ready(),
              List.copyOf(objectives),
              q.rewards,
              CommissionBook.remaining(p, q),
              q.important,
              !q.done && !q.submitted && plan.complete ? plan.payment : List.of(),
              q.previewBeforeUnlock || q.ready() || q.done
                  || q.objectives.stream().noneMatch(o -> o.type.equals("view"))
                  ? q.introduction : "",
              q.viewUnlocked());
      int size = JSON.toJson(view).length();
      if (!views.isEmpty() && bytes + size > 180000) break;
      views.add(view);
      bytes += size;
    }
    return new Snapshot(
        views,
        CommissionConfig.settings().limit(),
        s.remainder,
        s.milestoneEvery <= 0 ? CommissionConfig.settings().milestoneEvery() : s.milestoneEvery,
        s.pending.size(),
        s.pending.isEmpty() ? s.nextBonus == null ? List.of() : s.nextBonus : s.pending.get(0),
        CommissionConfig.runtimeValid ? message : "gui.teyvatdelight.katheryne.invalid_rules",
        s.revision,
        offset,
        s.quests.size(),
        (int) s.quests.stream().filter(q -> q.done).count(),
        offset + views.size() < s.quests.size() ? offset + views.size() : -1,
        KatheryneShopConfig.balanceItems());
  }

  public static boolean matches(ItemStack stack, String target) {
    return !stack.isEmpty()
        && CommissionConfig.resolve("item", target)
            .contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
  }

  private static int available(ServerPlayer p, String target) {
    int count = 0;
    for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
      ItemStack stack = p.getInventory().getItem(i);
      if (matches(stack, target)) count += stack.getCount();
    }
    return count;
  }


}
