package com.guoche.teyvatdelight.entity.katheryne;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Bounded capacity matching prevents overlapping goals from consuming the same item twice. */
final class CommissionItemPlan {
  final int[] slots, progress;
  final List<CommissionBook.PaymentView> payment;
  final boolean complete;

  private CommissionItemPlan(int[] slots, int[] progress,
      List<CommissionBook.PaymentView> payment, boolean complete) {
    this.slots = slots;
    this.progress = progress;
    this.payment = payment;
    this.complete = complete;
  }

  static CommissionItemPlan create(Inventory inventory, List<CommissionBook.Objective> goals) {
    int slotCount = inventory.getContainerSize(), goalCount = goals.size();
    int sink = slotCount + goalCount + 1, size = sink + 1;
    int[][] capacity = new int[size][size], residual = new int[size][size];
    for (int slot = 0; slot < slotCount; slot++) {
      ItemStack stack = inventory.getItem(slot);
      capacity[0][slot + 1] = stack.getCount();
      for (int goal = 0; goal < goalCount; goal++)
        if (goals.get(goal).type.equals("item") && matches(stack, goals.get(goal)))
          capacity[slot + 1][slotCount + goal + 1] = stack.getCount();
    }
    for (int goal = 0; goal < goalCount; goal++)
      if (goals.get(goal).type.equals("item"))
        capacity[slotCount + goal + 1][sink] = goals.get(goal).count;
    for (int i = 0; i < size; i++) residual[i] = capacity[i].clone();
    int[] parent = new int[size];
    while (true) {
      Arrays.fill(parent, -1);
      parent[0] = 0;
      var queue = new ArrayDeque<Integer>();
      queue.add(0);
      while (!queue.isEmpty() && parent[sink] < 0) {
        int from = queue.remove();
        for (int to = 1; to < size; to++)
          if (parent[to] < 0 && residual[from][to] > 0) {
            parent[to] = from;
            queue.add(to);
          }
      }
      if (parent[sink] < 0) break;
      int amount = Integer.MAX_VALUE;
      for (int at = sink; at != 0; at = parent[at])
        amount = Math.min(amount, residual[parent[at]][at]);
      for (int at = sink; at != 0; at = parent[at]) {
        residual[parent[at]][at] -= amount;
        residual[at][parent[at]] += amount;
      }
    }
    int[] slots = new int[slotCount], progress = new int[goalCount];
    List<CommissionBook.PaymentView> payment = new ArrayList<>();
    boolean complete = true;
    for (int goal = 0; goal < goalCount; goal++)
      if (goals.get(goal).type.equals("item")) {
        progress[goal] = capacity[slotCount + goal + 1][sink] - residual[slotCount + goal + 1][sink];
        complete &= progress[goal] == goals.get(goal).count;
      }
    for (int slot = 0; slot < slotCount; slot++) {
      slots[slot] = capacity[0][slot + 1] - residual[0][slot + 1];
      if (slots[slot] > 0)
        payment.add(new CommissionBook.PaymentView(slot,
            BuiltInRegistries.ITEM.getKey(inventory.getItem(slot).getItem()).toString(), slots[slot]));
    }
    return new CommissionItemPlan(slots, progress, List.copyOf(payment), complete);
  }

  private static boolean matches(ItemStack stack, CommissionBook.Objective goal) {
    if (stack.isEmpty()) return false;
    return goal.alternatives == null || goal.alternatives.isEmpty()
        ? CommissionBook.matches(stack, goal.target)
        : goal.alternatives.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
  }
}
