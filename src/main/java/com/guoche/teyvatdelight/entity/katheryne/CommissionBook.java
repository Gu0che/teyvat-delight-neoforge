package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.Gson;
import com.guoche.teyvatdelight.KatheryneData.Quest;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

/** Server-only state machine. Material payment and reward claiming are separate transactions. */
public final class CommissionBook {
  private static final Gson JSON = new Gson();

  public static final class Objective {
    public String type, target;
    public int count, progress;
    public long baseline;
    public List<String> alternatives = List.of();
    public String name = "", tag = "";

    public Objective(String type, String target, int count, long baseline) {
      this.type = type;
      this.target = target;
      this.count = count;
      this.baseline = baseline;
    }
  }

  public static final class Instance {
    public String id, template, title, description, icon;
    public String introduction = "";
    public boolean previewBeforeUnlock;
    public long cycle;
    public long expiresAt = -1;
    public boolean lifetimeInitialized, followsRefresh;
    public boolean important, urgent, submitted;
    public boolean stopRepeating;
    public List<CommissionCompletionRule> completionRules = List.of();
    public long acceptedOrder;
    public List<String> completionAdvancements = List.of();
    public List<Objective> objectives;
    public List<RawStack> rewards;
    public boolean done;

    public boolean targetsReady() {
      return objectives.stream().filter(o -> !o.type.equals("view"))
          .allMatch(o -> (submitted || !o.type.equals("item")) && o.progress >= o.count);
    }

    public boolean viewUnlocked() {
      return !done && objectives.stream().anyMatch(o -> o.type.equals("view")) && targetsReady();
    }

    public boolean ready() {
      return !done && targetsReady()
          && objectives.stream().filter(o -> o.type.equals("view")).allMatch(o -> o.progress >= o.count);
    }

    public boolean protectedProgress() {
      return ready() || viewUnlocked() && objectives.stream().anyMatch(o -> !o.type.equals("view"));
    }
  }

  public static final class State {
    public long cycle = Long.MIN_VALUE, revision;
    public long totalCompleted;
    public Map<String, Long> completedCounts = new HashMap<>();
    public int remainder, milestoneEvery;
    public List<Instance> quests = new ArrayList<>();
    public List<List<RawStack>> pending = new ArrayList<>();
    public List<RawStack> nextBonus;
    public List<String> rotation = new ArrayList<>();
    public Map<String, List<String>> targetRotation = new HashMap<>();
    public Set<String> acceptedTemplates = new HashSet<>();
    public Set<String> completedTemplates = new HashSet<>();
    public Set<String> closedTemplates = new HashSet<>();
    public Map<String, Long> events = new HashMap<>();
    public long lastAcceptedOrder;
  }

  public record View(
      String id,
      String title,
      String description,
      String icon,
      boolean done,
      boolean ready,
      List<ObjectiveView> objectives,
      List<RawStack> rewards,
      int secondsRemaining,
      boolean important,
      List<PaymentView> payment,
      String introduction,
      boolean viewUnlocked) {
    public View(String id, String title, String description, String icon, boolean done,
        boolean ready, List<ObjectiveView> objectives, List<RawStack> rewards, int secondsRemaining,
        boolean important, List<PaymentView> payment, String introduction) {
      this(id, title, description, icon, done, ready, objectives, rewards, secondsRemaining,
          important, payment, introduction, !done
              && objectives.stream().anyMatch(o -> o.type().equals("view"))
              && objectives.stream().noneMatch(o -> o.type().equals("item"))
              && objectives.stream().filter(o -> !o.type().equals("view"))
                  .allMatch(o -> o.progress() >= o.count()));
    }
    public View(String id, String title, String description, String icon, boolean done,
        boolean ready, List<ObjectiveView> objectives, List<RawStack> rewards, int secondsRemaining,
        boolean important, List<PaymentView> payment) {
      this(id, title, description, icon, done, ready, objectives, rewards, secondsRemaining,
          important, payment, "");
    }
    public View(String id, String title, String description, String icon, boolean done,
        boolean ready, List<ObjectiveView> objectives, List<RawStack> rewards, int secondsRemaining,
        boolean important) {
      this(id, title, description, icon, done, ready, objectives, rewards, secondsRemaining, important, List.of());
    }
    public View(String id, String title, String description, String icon, boolean done,
        boolean ready, List<ObjectiveView> objectives, List<RawStack> rewards, int secondsRemaining) {
      this(id, title, description, icon, done, ready, objectives, rewards, secondsRemaining, false);
    }

    public String actionKey() {
      return done ? "gui.teyvatdelight.katheryne.finished"
          : ready ? "gui.teyvatdelight.katheryne.claim"
          : viewing() && !pendingSubmission() ? "gui.teyvatdelight.katheryne.view" : "gui.teyvatdelight.katheryne.submit";
    }
    public boolean viewing() { return objectives.stream().anyMatch(o -> o.type().equals("view")); }
    public List<ObjectiveView> visibleObjectives() {
      return objectives.stream().filter(o -> !o.type().equals("view")).toList();
    }
    public boolean pendingSubmission() {
      return !done && !ready && !viewUnlocked
          && objectives.stream().anyMatch(o -> o.type().equals("item"));
    }
    public boolean canAct() {
      return !done && (ready || viewing() && viewUnlocked
          || pendingSubmission() && visibleObjectives().stream().allMatch(o -> o.progress() >= o.count()));
    }
    public String cyclingItem(long seconds) {
      int count = 0;
      for (var goal : objectives)
        if (goal.type().equals("item"))
          count += goal.alternatives() == null || goal.alternatives().isEmpty() ? 1 : goal.alternatives().size();
      if (count == 0) return "";
      int selected = (int) Math.floorMod(seconds, count);
      for (var goal : objectives) {
        if (!goal.type().equals("item")) continue;
        int size = goal.alternatives() == null || goal.alternatives().isEmpty() ? 1 : goal.alternatives().size();
        if (selected < size) return size == 1 ? goal.target() : goal.alternatives().get(selected);
        selected -= size;
      }
      return "";
    }
  }

  public record ObjectiveView(String type, String target, int count, int progress, List<String> alternatives,
      String name, String tag) {
    public ObjectiveView(String type, String target, int count, int progress, List<String> alternatives) {
      this(type, target, count, progress, alternatives, "", "");
    }
    public ObjectiveView(String type, String target, int count, int progress) {
      this(type, target, count, progress, List.of());
    }
  }
  public record PaymentView(int slot, String item, int count) {}

  public record Snapshot(
      List<View> quests,
      int limit,
      int remainder,
      int every,
      int claimable,
      List<RawStack> bonus,
      String message,
      long revision,
      int offset,
      int total,
      int completed,
      int nextOffset,
      List<String> balanceItems) {
    public Snapshot(List<View> quests, int limit, int remainder, int every, int claimable,
        List<RawStack> bonus, String message, long revision, int offset, int total,
        int completed, int nextOffset) {
      this(quests, limit, remainder, every, claimable, bonus, message, revision, offset, total,
          completed, nextOffset, List.of());
    }
    public Snapshot(List<View> quests, int limit, int remainder, int every, int claimable,
        List<RawStack> bonus, String message, long revision) {
      this(quests, limit, remainder, every, claimable, bonus, message, revision, 0,
          quests.size(), (int) quests.stream().filter(View::done).count(), -1);
    }
  }

  private final Map<UUID, State> players = new HashMap<>();
  private final Set<UUID> dispatching = new HashSet<>();
  private final Set<UUID> settling = new HashSet<>();
  private long conditionRevision = -1;
  private List<CommissionConditions.Condition> statisticConditions = List.of();
  private final Map<UUID, long[]> observedStatistics = new HashMap<>();
  private final Runnable dirty;

  public CommissionBook(Runnable dirty) {
    this.dirty = dirty;
  }

  public void load(CompoundTag saved) {
    for (String key : saved.getAllKeys())
      try {
        State state = JSON.fromJson(saved.getString(key), State.class);
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
        for (Instance q : state.quests) {
          if (q != null && q.template != null) state.acceptedTemplates.add(q.template);
          if (q != null && q.completionAdvancements == null) q.completionAdvancements = List.of();
          if (q != null && q.completionRules == null) q.completionRules = List.of();
          if (q != null && q.introduction == null) q.introduction = "";
        }
        state.acceptedTemplates.removeIf(id -> id == null || !id.matches("[a-z0-9_:/.-]{1,128}"));
        state.quests.removeIf(q -> !valid(q));
        // Acceptance/rotation history cannot prove completion in older saves.
        for (Instance q : state.quests)
          if (q.done) state.completedTemplates.add(q.template);
        state.completedTemplates.removeIf(id -> id == null || !id.matches("[a-z0-9_:/.-]{1,128}"));
        state.pending.removeIf(rewards -> !validRewards(rewards) || rewards.isEmpty());
        if (state.nextBonus != null && !validRewards(state.nextBonus)) state.nextBonus = null;
        state.rotation.removeIf(id -> id == null || id.length() > 128);
        state
            .targetRotation
            .entrySet()
            .removeIf(e -> e.getKey() == null || e.getKey().length() > 256 || e.getValue() == null);
        state.remainder = Math.max(0, state.remainder);
        state.totalCompleted = Math.max(0L, state.totalCompleted);
        state.milestoneEvery = Math.max(0, state.milestoneEvery);
        players.put(UUID.fromString(key), state);
      } catch (RuntimeException e) {
        TeyvatDelight.LOGGER.warn("Invalid commission record {}", key);
      }
  }

  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    players.forEach((id, state) -> tag.putString(id.toString(), JSON.toJson(state)));
    return tag;
  }

  public State state(ServerPlayer player, Quest legacy) {
    return state(player, legacy, List.of());
  }

  public State state(ServerPlayer player, Quest legacy, List<String> history) {
    State state = players.get(player.getUUID());
    if (!CommissionConfig.runtimeValid) return state == null ? new State() : state;
    if (state == null) {
      state = new State();
      players.put(player.getUUID(), state);
      if (legacy != null) {
        Instance old = new Instance();
        old.id = UUID.randomUUID().toString();
        old.template = "bottomless_stomach";
        old.title = "commission.teyvatdelight.stomach.title";
        old.description = "commission.teyvatdelight.stomach.description";
        old.icon = "";
        old.cycle = legacy.cycle();
        old.objectives =
            new ArrayList<>(List.of(new Objective("item", legacy.dish().toString(), 1, 0)));
        old.rewards = CommissionConfig.rewards(player, CommissionConfig.settings().reward());
        old.done = legacy.completed();
        state.quests.add(old);
        state.cycle = legacy.cycle();
        List<String> seen = new ArrayList<>(history);
        if (!seen.contains(legacy.dish().toString())) seen.add(legacy.dish().toString());
        state.targetRotation.put("bottomless_stomach:item:dishes", seen);
      }
      change(state);
    }
    int time = CommissionConfig.settings().refreshTime();
    if (state.milestoneEvery <= 0) {
      state.milestoneEvery = CommissionConfig.settings().milestoneEvery();
      change(state);
    }
    if (state.nextBonus == null) {
      state.nextBonus =
          CommissionConfig.rewards(player, CommissionConfig.settings().milestoneReward());
      change(state);
    }
    for (Instance q : state.quests)
      if (!q.lifetimeInitialized) {
        initializeLifetime(player, q);
        change(state);
      }
    for (Instance q : state.quests) {
      if (state.acceptedTemplates.add(q.template)) change(state);
      if (q.done && state.completedTemplates.add(q.template)) change(state);
      state.lastAcceptedOrder = Math.max(state.lastAcceptedOrder, q.acceptedOrder);
    }
    Set<String> onceCompleted = new HashSet<>();
    Set<String> closedTemplates = state.closedTemplates;
    for (var template : CommissionConfig.settings().templates())
      if (!template.repeatable() && state.completedTemplates.contains(template.id()))
        onceCompleted.add(template.id());
    if (state.quests.removeIf(q -> !q.done
        && (onceCompleted.contains(q.template)
            || !q.protectedProgress() && (closedTemplates.contains(q.template) || expired(player, q))))) change(state);
    for (int i = state.quests.size() - 1; i >= 0; i--) {
      Instance q = state.quests.get(i);
      if (q.acceptedOrder <= 0) {
        q.acceptedOrder = ++state.lastAcceptedOrder;
        change(state);
      } else state.lastAcceptedOrder = Math.max(state.lastAcceptedOrder, q.acceptedOrder);
    }
    long cycle = KatheryneRules.cycle(player.server, time == 0 ? 22000 : time);
    dispatchUrgent(player, state, cycle);
    if (state.cycle == Long.MIN_VALUE || time != 0 && cycle > state.cycle) {
      add(player, state, CommissionConfig.settings().dailyCount(), cycle);
      state.cycle = cycle;
      change(state);
    }
    if (time == 0
        && !settling.contains(player.getUUID())
        && !CommissionConfig.settings().templates().isEmpty()
        && state.quests.stream().noneMatch(q -> !q.important && !q.done)) {
      if (add(player, state, 1, state.cycle) > 0) change(state);
    }
    order(state);
    return state;
  }

  public int refresh(ServerPlayer player, State state) {
    int time = CommissionConfig.settings().refreshTime();
    long cycle = KatheryneRules.cycle(player.server, time == 0 ? 22000 : time);
    int generated = dispatchUrgent(player, state, cycle)
        + add(player, state, CommissionConfig.settings().dailyCount(), cycle);
    state.cycle = cycle;
    change(state);
    return generated;
  }

  private int add(ServerPlayer player, State state, int amount, long cycle) {
    int generated = 0, ordinary = 0;
    // Important tasks have their own quota: one active instance per template, not dailyCount.
    for (int tries = 0; tries < amount + CommissionConfig.settings().templates().size(); tries++) {
      final boolean ordinaryAllowed = ordinary < amount && hasRoom(state);
      var choices = CommissionConfig.settings().templates().stream()
          .filter(t -> !t.urgent() && eligible(player, state, t))
          .filter(t -> t.important() || ordinaryAllowed).toList();
      if (choices.isEmpty()) break;
      var selected = select(player, state, choices);
      if (!selected.important()) ordinary++;
      accept(player, state, selected, cycle);
      generated++;
    }
    cleanCompleted(state, generated);
    order(state);
    return generated;
  }

  private static boolean eligible(ServerPlayer p, State s, CommissionConfig.Template t) {
    return !s.closedTemplates.contains(t.id())
        && (t.repeatable() || !s.completedTemplates.contains(t.id()))
        && (t.repeatable() && !t.important() && !t.urgent()
            || s.quests.stream().noneMatch(q -> !q.done && q.template.equals(t.id())))
        && t.conditions().stream().allMatch(c -> c.matches(p, s));
  }

  private static CommissionConfig.Template select(ServerPlayer p, State s,
      List<CommissionConfig.Template> eligible) {
    List<CommissionConfig.Template> choices = new ArrayList<>(eligible);
    if (CommissionConfig.settings().rotation()) {
      choices.removeIf(t -> s.rotation.contains(t.id()));
      if (choices.isEmpty()) { s.rotation.clear(); choices.addAll(eligible); }
    }
    double pick = p.getRandom().nextDouble()
        * choices.stream().mapToDouble(CommissionConfig.Template::weight).sum();
    for (var t : choices) { pick -= t.weight(); if (pick < 0) return t; }
    return choices.get(choices.size() - 1);
  }

  private static long ordinaryCount(State s) {
    return s.quests.stream().filter(q -> !q.important && !q.done && !q.protectedProgress()).count();
  }

  private static Instance replaceable(State s) {
    return s.quests.stream().filter(q -> !q.important && !q.urgent && !q.done && !q.protectedProgress())
        .min(Comparator.comparingLong(q -> q.acceptedOrder)).orElse(null);
  }

  private static boolean hasRoom(State s) {
    return ordinaryCount(s) < CommissionConfig.settings().limit() || replaceable(s) != null;
  }

  private static void makeRoom(State s) {
    while (ordinaryCount(s) >= CommissionConfig.settings().limit()) {
      Instance victim = replaceable(s);
      if (victim == null) return;
      s.quests.remove(victim);
    }
  }

  private int dispatchUrgent(ServerPlayer p, State s, long cycle) {
    if (!CommissionConfig.runtimeValid || !dispatching.add(p.getUUID())) return 0;
    int generated = 0;
    try {
      // Never evict another urgent task: otherwise eligible templates would endlessly replace each other.
      for (int i = 0; i < CommissionConfig.settings().templates().size(); i++) {
        var choices = CommissionConfig.settings().templates().stream()
            .filter(t -> t.urgent() && eligible(p, s, t))
            .filter(t -> t.important() || hasRoom(s)).toList();
        if (choices.isEmpty()) break;
        var selected = choices.stream().filter(CommissionConfig.Template::important).findFirst()
            .orElseGet(() -> select(p, s, choices));
        accept(p, s, selected, cycle);
        generated++;
      }
      cleanCompleted(s, generated);
      if (generated > 0) { order(s); change(s); }
      return generated;
    } finally { dispatching.remove(p.getUUID()); }
  }

  public void checkUrgent(ServerPlayer p) {
    State s = players.get(p.getUUID());
    if (s != null) {
      int time = CommissionConfig.settings().refreshTime();
      int added = dispatchUrgent(p, s, KatheryneRules.cycle(p.server, time == 0 ? 22000 : time));
      if (added > 0 && p.containerMenu instanceof KatheryneMenu menu) menu.sendCommissionUpdate();
    }
  }

  /** Poll only statistics actually used as prerequisites; never scan entities or the world. */
  public void pollConditions(ServerPlayer p) {
    if (!CommissionConfig.runtimeValid) return;
    if (conditionRevision != CommissionConfig.revision()) {
      Map<String, CommissionConditions.Condition> dependencies = new LinkedHashMap<>();
      for (var template : CommissionConfig.settings().templates())
        if (template.urgent())
          for (var c : CommissionConditions.statistics(template.conditions()))
            dependencies.putIfAbsent(c.category() + "/" + c.id(), c);
      statisticConditions = List.copyOf(dependencies.values());
      observedStatistics.clear();
      conditionRevision = CommissionConfig.revision();
    }
    if (statisticConditions.isEmpty() || !players.containsKey(p.getUUID())) return;
    long[] values = observedStatistics.computeIfAbsent(p.getUUID(), id -> {
      long[] fresh = new long[statisticConditions.size()];
      Arrays.fill(fresh, -1);
      return fresh;
    });
    boolean changed = false;
    for (int i = 0; i < values.length; i++) {
      var condition = statisticConditions.get(i);
      long value = CommissionConditions.statistic(p, condition.category(), condition.id());
      if (values[i] != value) { values[i] = value; changed = true; }
    }
    if (changed) checkUrgent(p);
  }

  public void logout(ServerPlayer player) {
    observedStatistics.remove(player.getUUID());
  }

  private void accept(ServerPlayer player, State state, CommissionConfig.Template selected, long cycle) {
    Instance q = new Instance();
    q.id = UUID.randomUUID().toString();
    q.template = selected.id();
    q.cycle = cycle;
    q.important = selected.important();
    q.urgent = selected.urgent();
    q.acceptedOrder = ++state.lastAcceptedOrder;
    q.completionAdvancements = selected.completionAdvancements();
    q.completionRules = selected.completionRules();
    initializeLifetime(player, q);
    q.title = selected.title();
    q.description = selected.description();
    q.introduction = selected.introduction();
    q.previewBeforeUnlock = selected.previewBeforeUnlock();
    q.icon = selected.icon();
    q.objectives = new ArrayList<>();
    for (var target : selected.targets()) {
      if (target.any()) {
        var candidates = CommissionConfig.alternatives(target);
        if (candidates.isEmpty() || candidates.size() > 4096)
          throw new IllegalArgumentException("Any-item target needs 1..4096 candidates");
        var objective = new Objective("item", candidates.get(0), target.count(), 0);
        objective.alternatives = List.copyOf(candidates);
        objective.name = target.name();
        objective.tag = target.tag().isEmpty() && target.target().startsWith("#")
            ? target.target().substring(1) : target.tag();
        q.objectives.add(objective);
      } else for (String id : drawTarget(player, state, selected.id(), target))
        q.objectives.add(
            new Objective(
                target.type(),
                id,
                target.count(),
                target.type().equals("stat") ? stat(player, id) : 0));
    }
    q.rewards =
        CommissionConfig.rewards(
            player,
            selected.reward() == null ? CommissionConfig.settings().reward() : selected.reward());
    // Roll a complete valid instance before displacing any older unpaid work.
    if (!q.important) makeRoom(state);
    state.quests.add(0, q);
    state.acceptedTemplates.add(selected.id());
    state.rotation.add(selected.id());
    if (state.rotation.size() > 1024) state.rotation.remove(0);
    change(state);
  }

  private static void cleanCompleted(State state, int remaining) {
    // Claimed records are appended in settlement order: retire oldest first to avoid starvation.
    for (int i = 0; i < state.quests.size() && remaining > 0;)
      if (state.quests.get(i).done) {
        state.quests.remove(i);
        remaining--;
      } else i++;
  }

  private static void order(State state) {
    state.quests.sort(Comparator
        .comparingInt((Instance q) -> q.done ? 2 : q.important ? 0 : 1)
        .thenComparingLong(q -> q.important && !q.done ? q.acceptedOrder : 0));
  }

  private static void initializeLifetime(ServerPlayer player, Instance q) {
    var settings = CommissionConfig.settings();
    q.lifetimeInitialized = true;
    if (q.important) {
      q.followsRefresh = false;
      q.expiresAt = -1;
      return;
    }
    q.followsRefresh = settings.durationTicks() == -1;
    if (q.followsRefresh)
      q.expiresAt = settings.refreshTime() == 0 ? -1 : q.cycle * 24000L + settings.refreshTime();
    else
      q.expiresAt =
          settings.durationTicks() == 0
              ? -1
              : player.server.overworld().getGameTime() + settings.durationTicks();
  }

  private static long clock(ServerPlayer player, Instance q) {
    return q.followsRefresh
        ? player.server.overworld().getDayTime()
        : player.server.overworld().getGameTime();
  }

  private static boolean expired(ServerPlayer player, Instance q) {
    return !q.important && q.expiresAt >= 0 && clock(player, q) >= q.expiresAt;
  }

  private static int remaining(ServerPlayer player, Instance q) {
    if (q.important || q.protectedProgress() || q.expiresAt < 0) return -1;
    return (int)
        Math.min(Integer.MAX_VALUE, (Math.max(0, q.expiresAt - clock(player, q)) + 19) / 20);
  }

  private static boolean valid(Instance q) {
    if (q == null
        || !text(q.id, 128)
        || !text(q.template, 128)
        || !text(q.title, 256)
        || !text(q.description, 1024)
        || !text(q.introduction, 8192)
        || !text(q.icon, 256)
        || q.objectives == null
        || q.objectives.isEmpty()
        || q.objectives.size() > 17
        || q.objectives.stream().filter(o -> o != null && "view".equals(o.type)).count() > 1
        || q.objectives.stream().filter(o -> o != null && !"view".equals(o.type)).count() > 16
        || !validRewards(q.rewards)
        || q.completionAdvancements == null
        || q.completionAdvancements.size() > 64
        || q.completionAdvancements.stream().anyMatch(id -> id == null || id.length() > 256
            || ResourceLocation.tryParse(id) == null)) return false;
    return q.objectives.stream()
        .allMatch(
            o ->
                o != null
                    && o.type != null
                    && Set.of("item", "kill", "stat", "event", "view").contains(o.type)
                    && text(o.target, 256)
                    && ResourceLocation.tryParse(o.target.replaceFirst("^#", "")) != null
                    && o.count > 0
                    && o.count <= 1000000
                    && o.progress >= 0
                    && o.progress <= o.count
                    && (o.name == null || text(o.name, 256))
                    && (o.tag == null || text(o.tag, 256))
                    && (o.alternatives == null || o.alternatives.size() <= 4096
                        && o.alternatives.stream().allMatch(id -> text(id, 256)
                            && ResourceLocation.tryParse(id) != null)));
  }

  private static boolean text(String value, int max) {
    return value != null && value.length() <= max;
  }

  private static boolean validRewards(List<RawStack> values) {
    return values != null
        && values.size() <= 144
        && values.stream()
            .allMatch(
                r ->
                    r != null
                        && text(r.item(), 256)
                        && ResourceLocation.tryParse(r.item()) != null
                        && r.count() > 0
                        && r.count() <= 1000000);
  }

  private static List<String> drawTarget(
      ServerPlayer player, State state, String template, CommissionConfig.Target target) {
    if (!CommissionConfig.settings().rotation()
        || target.type().equals("stat")
        || target.type().equals("event")
        || target.type().equals("view")) return CommissionConfig.draw(player, target);
    String key =
        template
            + ":"
            + target.type()
            + ":"
            + (!target.pool().isEmpty() ? target.pool() : target.target());
    List<String> seen = state.targetRotation.computeIfAbsent(key, k -> new ArrayList<>());
    List<CommissionConfig.Choice> all =
        target.pool().isEmpty()
            ? CommissionConfig.resolve(
                    target.type().equals("kill") ? "entity" : "item", target.target())
                .stream()
                .map(id -> new CommissionConfig.Choice(id, 1))
                .toList()
            : CommissionConfig.pool(target.pool());
    List<CommissionConfig.Choice> available =
        all.stream().filter(c -> !seen.contains(c.id())).toList();
    if (available.isEmpty() || !target.repeat() && available.size() < target.draws()) {
      seen.clear();
      available = all;
    }
    List<String> chosen =
        CommissionConfig.drawChoices(player, available, target.draws(), target.repeat());
    chosen.forEach(
        id -> {
          if (!seen.contains(id)) seen.add(id);
        });
    return chosen;
  }

  public boolean hasWork(ServerPlayer p, Quest old) {
    return state(p, old).quests.stream().anyMatch(q -> !q.done);
  }

  /** Read saved lifetime settlements without assigning or refreshing commissions. */
  public long completedCount(ServerPlayer player) {
    State state = players.get(player.getUUID());
    return state == null ? 0L : state.totalCompleted;
  }

  public long completedCount(ServerPlayer player, String templateId) {
    State state = players.get(player.getUUID());
    return state == null ? 0L
        : state.completedCounts.getOrDefault(CommissionConditions.templateId(templateId), 0L);
  }

  /** Source-compatible stage adapter; network callers must use explicit submit/claim actions. */
  @Deprecated
  public String settle(ServerPlayer p, State s, String id) {
    Instance q = s.quests.stream().filter(v -> v.id.equals(id)).findFirst().orElse(null);
    return q != null && q.ready() ? claimCommission(p, s, id) : submit(p, s, id);
  }

  public String submit(ServerPlayer p, State s, String id) {
    if (!CommissionConfig.runtimeValid) return "gui.teyvatdelight.katheryne.invalid_rules";
    Instance q = s.quests.stream().filter(v -> v.id.equals(id)).findFirst().orElse(null);
    if (q == null || q.done || q.submitted || q.ready()
        || q.objectives.stream().noneMatch(o -> o.type.equals("item")))
      return "gui.teyvatdelight.katheryne.stale";
    if (s.closedTemplates.contains(q.template)) return "gui.teyvatdelight.katheryne.stale";
    if (s.completedTemplates.contains(q.template)
        && CommissionConfig.settings().templates().stream()
            .anyMatch(t -> t.id().equals(q.template) && !t.repeatable()))
      return "gui.teyvatdelight.katheryne.stale";
    if (!q.ready() && expired(p, q)) return "gui.teyvatdelight.katheryne.stale";
    var payment = CommissionItemPlan.create(p.getInventory(), q.objectives);
    if (!payment.complete) return "gui.teyvatdelight.katheryne.missing_materials";
    for (Objective objective : q.objectives)
      if (!objective.type.equals("item") && !objective.type.equals("view") && objective.progress < objective.count)
        return "gui.teyvatdelight.katheryne.not_ready";
    // Validate all items before consuming any slot. Non-item targets must also be achieved.
    for (RawStack reward : q.rewards)
      if (CommissionConfig.resolve("item", reward.item()).isEmpty())
        return "gui.teyvatdelight.katheryne.invalid_rules";
    LinkedHashSet<String> advancements = new LinkedHashSet<>(q.completionAdvancements);
    for (var rule : q.completionRules)
      for (int i = 0; i < payment.slots.length; i++)
        if (payment.slots[i] > 0 && rule.matches(p.getInventory().getItem(i))) {
          advancements.addAll(rule.advancements());
          q.stopRepeating |= rule.stopRepeating();
          break;
        }
    q.completionAdvancements = List.copyOf(advancements);
    for (int i = 0; i < payment.slots.length; i++)
      if (payment.slots[i] > 0) p.getInventory().getItem(i).shrink(payment.slots[i]);
    q.submitted = true;
    for (Objective o : q.objectives) if (o.type.equals("item")) o.progress = o.count;
    change(s);
    notify(p, q);
    checkUrgent(p);
    return "";
  }

  /** Only the authenticated open menu invokes this when entering an information detail page. */
  public String viewInformation(ServerPlayer p, State s, String id) {
    if (!CommissionConfig.runtimeValid) return "gui.teyvatdelight.katheryne.invalid_rules";
    Instance q = s.quests.stream().filter(v -> v.id.equals(id)).findFirst().orElse(null);
    if (q == null || q.done || !q.protectedProgress()
            && (expired(p, q) || s.closedTemplates.contains(q.template))
        || q.objectives.stream().noneMatch(o -> o.type.equals("view")))
      return "gui.teyvatdelight.katheryne.stale";
    if (!q.viewUnlocked()) return "gui.teyvatdelight.katheryne.view_locked";
    if (!q.ready()) {
      for (Objective objective : q.objectives)
        if (objective.type.equals("view")) objective.progress = objective.count;
      change(s);
      checkUrgent(p);
    }
    return "";
  }

  public String claimCommission(ServerPlayer p, State s, String id) {
    if (!CommissionConfig.runtimeValid) return "gui.teyvatdelight.katheryne.invalid_rules";
    if (settling.contains(p.getUUID())) return "gui.teyvatdelight.katheryne.stale";
    Instance q = s.quests.stream().filter(v -> v.id.equals(id)).findFirst().orElse(null);
    if (q == null || q.done) return "gui.teyvatdelight.katheryne.stale";
    if (!q.ready()) return "gui.teyvatdelight.katheryne.not_ready";
    if (s.completedTemplates.contains(q.template)
        && CommissionConfig.settings().templates().stream()
            .anyMatch(t -> t.id().equals(q.template) && !t.repeatable()))
      return "gui.teyvatdelight.katheryne.stale";
    for (RawStack reward : q.rewards)
      if (CommissionConfig.resolve("item", reward.item()).isEmpty())
        return "gui.teyvatdelight.katheryne.invalid_rules";
    // Resolve the next milestone before claiming so invalid rewards cannot partially settle.
    List<RawStack> bonus = s.remainder + 1 >= s.milestoneEvery ? s.nextBonus : null;
    if (bonus != null
        && bonus.stream().anyMatch(r -> CommissionConfig.resolve("item", r.item()).isEmpty()))
      return "gui.teyvatdelight.katheryne.invalid_rules";
    List<RawStack> nextBonus =
        bonus == null
            ? null
            : CommissionConfig.rewards(p, CommissionConfig.settings().milestoneReward());
    settling.add(p.getUUID());
    try {
      q.done = true;
      for (Objective o : q.objectives) o.progress = o.count;
      s.quests.remove(q);
      s.quests.add(q);
      order(s);
      s.remainder++;
      if (bonus != null) {
        s.remainder -= s.milestoneEvery;
        if (!bonus.isEmpty()) s.pending.add(bonus);
        s.nextBonus = nextBonus;
        s.milestoneEvery = CommissionConfig.settings().milestoneEvery();
      }
      give(p, q.rewards);
      s.completedTemplates.add(q.template);
      if (q.stopRepeating) {
        s.closedTemplates.add(q.template);
        // Keep paid or achieved copies claimable; remove only unfinished unpaid copies.
        s.quests.removeIf(other -> !other.done && !other.protectedProgress() && other.template.equals(q.template));
      }
      if (s.totalCompleted < Long.MAX_VALUE) s.totalCompleted++;
      s.completedCounts.merge(q.template, 1L, (old, one) -> old == Long.MAX_VALUE ? old : old + one);
      change(s);
      for (String advancement : q.completionAdvancements)
        try {
          com.guoche.teyvatdelight.api.KatheryneApi.awardAdvancement(p, advancement);
        } catch (RuntimeException exception) {
          TeyvatDelight.LOGGER.error("Commission completion advancement failed: {}", advancement, exception);
        }
      com.guoche.teyvatdelight.api.KatheryneApi.completed(p, q.id, q.template);
      checkUrgent(p);
      if (CommissionConfig.settings().refreshTime() == 0) add(p, s, 1, s.cycle);
      return "";
    } finally { settling.remove(p.getUUID()); }
  }

  public String claim(ServerPlayer p, State s) {
    if (!CommissionConfig.runtimeValid) return "gui.teyvatdelight.katheryne.invalid_rules";
    if (s.pending.isEmpty()) return "gui.teyvatdelight.katheryne.not_ready";
    if (s.pending.get(0).stream()
        .anyMatch(r -> CommissionConfig.resolve("item", r.item()).isEmpty()))
      return "gui.teyvatdelight.katheryne.invalid_rules";
    List<RawStack> reward = s.pending.remove(0);
    change(s);
    give(p, reward);
    return "";
  }

  public void kill(ServerPlayer player, EntityType<?> type) {
    advance(player, "kill", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString(), 1);
  }

  public void advance(ServerPlayer p, String type, String target, int amount) {
    if (amount <= 0 || !CommissionConfig.runtimeValid || type.equals("view")) return;
    if (type.equals("event") && (target.length() > 256 || ResourceLocation.tryParse(target) == null))
      throw new IllegalArgumentException("Invalid commission event ID " + target);
    State s = state(p, null);
    for (Instance q : List.copyOf(s.quests)) {
      if (q.done || q.ready()) continue;
      boolean updated = false;
      Objective changed = null;
      for (Objective o : q.objectives)
        if (o.type.equals(type) && o.target.equals(target) && o.progress < o.count) {
          o.progress = (int) Math.min(o.count, (long) o.progress + amount);
          updated = true;
          changed = o;
        }
      if (updated) {
        change(s);
        notify(p, q, changed);
      }
    }
    if (type.equals("event")) {
      long current = s.events.getOrDefault(target, 0L);
      s.events.put(target, current > Long.MAX_VALUE - amount ? Long.MAX_VALUE : current + amount);
      change(s);
    }
    checkUrgent(p);
  }

  public void stats(ServerPlayer p, Quest legacy) {
    if (!CommissionConfig.runtimeValid) return;
    State s = state(p, legacy);
    for (Instance q : List.copyOf(s.quests)) {
      if (q.done || q.ready()) continue;
      boolean updated = false;
      for (Objective o : q.objectives)
        if (o.type.equals("stat") && o.progress < o.count) {
          long current = stat(p, o.target), delta = Math.max(0, current - o.baseline);
          if (current != o.baseline) {
            o.baseline = current;
            change(s);
          }
          if (delta > 0) {
            o.progress = (int) Math.min(o.count, (long) o.progress + delta);
            updated = true;
          }
        }
      if (updated) {
        change(s);
        if (q.protectedProgress()) notify(p, q);
      }
    }
    checkUrgent(p);
  }

  /** Read without allocation or dispatch; event progress before acceptance remains available. */
  public long eventCount(ServerPlayer player, String id) {
    State state = players.get(player.getUUID());
    return state == null ? 0 : state.events.getOrDefault(id, 0L);
  }

  /** Read active goals without assigning quests or scanning anything outside this player's list. */
  public String activeEventToken(ServerPlayer player, String id) {
    State state = players.get(player.getUUID());
    if (state == null || !CommissionConfig.runtimeValid) return "";
    for (Instance q : state.quests)
      if (!q.done && !q.ready() && !expired(player, q))
        for (Objective o : q.objectives)
          if (o.type.equals("event") && o.target.equals(id) && o.progress < o.count) return q.id;
    return "";
  }

  private static long stat(ServerPlayer p, String id) {
    ResourceLocation canonical = BuiltInRegistries.CUSTOM_STAT.get(ResourceLocation.tryParse(id));
    return canonical == null ? 0 : p.getStats().getValue(Stats.CUSTOM.get(canonical));
  }

  private void notify(ServerPlayer p, Instance q) {
    notify(p, q, q.objectives.get(0));
  }

  private void notify(ServerPlayer p, Instance q, Objective o) {
    p.displayClientMessage(progressMessage(q, o), true);
    if (p.containerMenu instanceof KatheryneMenu menu)
      menu.showNotice(
          q.ready()
              ? "gui.teyvatdelight.katheryne.ready_message"
              : q.viewUnlocked() ? "gui.teyvatdelight.katheryne.view_unlocked_message"
                  : "gui.teyvatdelight.katheryne.progress_updated");
  }

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

  public Snapshot snapshot(ServerPlayer p, State s, String message) {
    return snapshot(p, s, message, 0);
  }

  public Snapshot snapshot(ServerPlayer p, State s, String message, int requestedOffset) {
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
              remaining(p, q),
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

  private void change(State s) {
    s.revision++;
    dirty.run();
  }

  private static void give(ServerPlayer p, List<RawStack> rewards) {
    for (RawStack r : rewards)
      KatheryneData.give(
          p, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(r.item())), r.count());
  }
}
