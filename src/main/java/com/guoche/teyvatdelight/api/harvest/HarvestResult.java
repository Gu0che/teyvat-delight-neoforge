package com.guoche.teyvatdelight.api.harvest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

/** Source-aware item amounts. Stack data is copied at every public boundary. */
public final class HarvestResult {
    public enum Source { BASE, FIELD, SEED_DISPENSARY, MORA }
    public record Entry(Source source, ItemStack stack) {
        public Entry { Objects.requireNonNull(source); stack = stack.copy(); }
        @Override public ItemStack stack() { return stack.copy(); }
    }
    public static final int MAX_ENTRIES = 128;
    public static final int MAX_COUNT = 4096;
    public static final int MAX_OUTPUT_STACKS = 128;
    private final List<Entry> entries = new ArrayList<>();
    private boolean readOnly;

    public List<Entry> entries() { return List.copyOf(entries); }
    public HarvestResult add(Source source, ItemStack stack) {
        checkMutable();
        if (!stack.isEmpty() && stack.getCount() > 0) {
            if (entries.size() >= MAX_ENTRIES) throw new IllegalArgumentException("Too many harvest entries");
            var copy = stack.copy();
            copy.setCount(Math.min(MAX_COUNT, copy.getCount()));
            entries.add(new Entry(source, copy));
        }
        return this;
    }
    public void remove(Predicate<Entry> match) {
        checkMutable();
        entries.removeIf(match);
    }
    public void multiply(Predicate<Entry> match, double factor) {
        checkMutable();
        if (!Double.isFinite(factor) || factor < 0 || factor > MAX_COUNT)
            throw new IllegalArgumentException("Invalid harvest multiplier");
        for (int i = entries.size() - 1; i >= 0; i--) {
            var old = entries.get(i);
            if (!match.test(old)) continue;
            var stack = old.stack();
            int count = (int) Math.min(MAX_COUNT, Math.floor(stack.getCount() * factor));
            if (count == 0) entries.remove(i);
            else {
                stack.setCount(count);
                entries.set(i, new Entry(old.source(), stack));
            }
        }
    }
    public HarvestResult copy() {
        var copy = new HarvestResult();
        for (var entry : entries) copy.add(entry.source(), entry.stack());
        return copy;
    }
    public HarvestResult snapshot() {
        var snapshot = copy();
        snapshot.readOnly = true;
        return snapshot;
    }
    public List<ItemStack> stacks() {
        var merged = new ArrayList<ItemStack>();
        for (var entry : entries) {
            var stack = entry.stack();
            for (var existing : merged) {
                if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < MAX_COUNT) {
                    int move = Math.min(stack.getCount(), MAX_COUNT - existing.getCount());
                    existing.grow(move);
                    stack.shrink(move);
                    if (stack.isEmpty()) break;
                }
            }
            if (!stack.isEmpty()) merged.add(stack);
        }
        var split = new ArrayList<ItemStack>();
        for (var stack : merged) {
            while (!stack.isEmpty()) {
                if (split.size() >= MAX_OUTPUT_STACKS) throw new IllegalArgumentException("Too many harvest output stacks");
                int amount = Math.min(stack.getCount(), stack.getMaxStackSize());
                var part = stack.copy();
                part.setCount(amount);
                split.add(part);
                stack.shrink(amount);
            }
        }
        return List.copyOf(split);
    }
    private void checkMutable() {
        if (readOnly) throw new IllegalStateException("Harvest snapshot is read-only");
    }
}
