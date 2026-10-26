package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig.RawStack;
import java.util.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;

/** Immutable definitions are shared; rolled targets belong exclusively to the player's save. */
public final class CommissionConfig {
  public static final int MAX_QUESTS = 64, MAX_TARGETS = 16, MAX_TEXT = 1024;

  public record Entry(String selector, double weight) {}

  public record Pool(String type, List<Entry> include, List<String> exclude) {}

  public record Target(
      String type, String target, String pool, int count, int draws, boolean repeat, String selection,
      String name, String tag) {
    public Target(String type, String target, String pool, int count, int draws, boolean repeat, String selection) {
      this(type, target, pool, count, draws, repeat, selection, "", "");
    }
    public Target(String type, String target, String pool, int count, int draws, boolean repeat) {
      this(type, target, pool, count, draws, repeat, "random");
    }
    public boolean any() { return selection.equals("any"); }
  }

  public record Reward(List<RawStack> fixed, List<Target> random) {}

  public record Template(
      String id,
      String title,
      String description,
      String icon,
      double weight,
      List<Target> targets,
      Reward reward,
      boolean repeatable,
      boolean important,
      List<String> completionAdvancements,
      boolean urgent,
      List<CommissionConditions.Condition> conditions,
      List<CommissionCompletionRule> completionRules,
      String introduction,
      boolean previewBeforeUnlock) {
    public Template(String id, String title, String description, String icon, double weight,
        List<Target> targets, Reward reward, boolean repeatable, boolean important,
        List<String> completionAdvancements, boolean urgent,
        List<CommissionConditions.Condition> conditions, List<CommissionCompletionRule> completionRules,
        String introduction) {
      this(id, title, description, icon, weight, targets, reward, repeatable, important,
          completionAdvancements, urgent, conditions, completionRules, introduction, false);
    }
    public Template(String id, String title, String description, String icon, double weight,
        List<Target> targets, Reward reward, boolean repeatable, boolean important,
        List<String> completionAdvancements, boolean urgent,
        List<CommissionConditions.Condition> conditions, List<CommissionCompletionRule> completionRules) {
      this(id, title, description, icon, weight, targets, reward, repeatable, important,
          completionAdvancements, urgent, conditions, completionRules, "");
    }
    public Template(String id, String title, String description, String icon, double weight,
        List<Target> targets, Reward reward, boolean repeatable, boolean important,
        List<String> completionAdvancements, boolean urgent,
        List<CommissionConditions.Condition> conditions) {
      this(id, title, description, icon, weight, targets, reward, repeatable, important,
          completionAdvancements, urgent, conditions, List.of());
    }
    public Template(String id, String title, String description, String icon, double weight,
        List<Target> targets, Reward reward, boolean repeatable, boolean important,
        List<String> completionAdvancements) {
      this(id, title, description, icon, weight, targets, reward, repeatable, important,
          completionAdvancements, false, List.of());
    }
    public Template(String id, String title, String description, String icon, double weight,
        List<Target> targets, Reward reward) {
      this(id, title, description, icon, weight, targets, reward, true, false, List.of());
    }
  }

  public record Settings(
      int refreshTime,
      int durationTicks,
      int dailyCount,
      int limit,
      boolean rotation,
      Reward reward,
      int milestoneEvery,
      Reward milestoneReward,
      Map<String, Pool> pools,
      List<Template> templates) {}

  public record Choice(String id, double weight) {}

  private static Settings settings = parse(defaultRoot());
  private static final Map<String, List<Choice>> resolved = new HashMap<>();
  private static long revision;
  public static boolean runtimeValid = true;

  private CommissionConfig() {}

  public static Settings settings() {
    return settings;
  }

  public static long revision() {
    return revision;
  }

  public static void install(Settings value) {
    settings = value;
    runtimeValid = true;
    invalidate();
  }

  public static void invalidate() {
    resolved.clear();
    revision++;
    KatheryneRules.invalidate();
  }

  public static Settings parse(JsonObject root) {
    return CommissionConfigParser.parse(root);
  }

  public static JsonObject defaultRoot() {
    return CommissionConfigParser.defaultRoot();
  }

  public static int number(JsonObject object, String key, int fallback, int min, int max) {
    return CommissionConfigParser.number(object, key, fallback, min, max);
  }

  public static List<Choice> pool(String key) {
    List<Choice> cached = resolved.get("pool:" + key);
    if (cached != null) return cached;
    Pool p = settings.pools().get(key);
    if (p == null) throw new IllegalArgumentException("Unknown pool " + key);
    Map<String, Double> choices = new LinkedHashMap<>();
    for (Entry e : p.include())
      for (String value : resolve(p.type(), e.selector())) choices.putIfAbsent(value, e.weight());
    for (String excluded : p.exclude())
      for (String value : resolve(p.type(), excluded)) choices.remove(value);
    List<Choice> result =
        choices.entrySet().stream().map(e -> new Choice(e.getKey(), e.getValue())).toList();
    resolved.put("pool:" + key, result);
    return result;
  }

  public static List<String> resolve(String type, String selector) {
    return resolved
        .computeIfAbsent(
            type + ":" + selector,
            k -> resolveUncached(type, selector).stream().map(s -> new Choice(s, 1)).toList())
        .stream()
        .map(Choice::id)
        .toList();
  }

  private static List<String> resolveUncached(String type, String selector) {
    if (selector.equals("@merchant_ingredients"))
      return com.guoche.teyvatdelight.entity.katheryne.TeyvatMerchantCatalog.INGREDIENTS.stream()
          .map(s -> BuiltInRegistries.ITEM.getKey(s.get()).toString())
          .toList();
    ResourceLocation id = ResourceLocation.tryParse(selector.replaceFirst("^#", ""));
    if (id == null) fail(selector);
    if (type.equals("entity")) {
      if (selector.startsWith("#"))
        return BuiltInRegistries.ENTITY_TYPE
            .getTag(TagKey.create(Registries.ENTITY_TYPE, id))
            .map(
                tag ->
                    tag.stream()
                        .map(Holder::value)
                        .map(v -> BuiltInRegistries.ENTITY_TYPE.getKey(v).toString())
                        .toList())
            .orElse(List.of());
      return BuiltInRegistries.ENTITY_TYPE.containsKey(id) ? List.of(id.toString()) : List.of();
    }
    if (selector.startsWith("#"))
      return BuiltInRegistries.ITEM
          .getTag(TagKey.create(Registries.ITEM, id))
          .map(
              tag ->
                  tag.stream()
                      .map(Holder::value)
                      .filter(v -> v != Items.AIR)
                      .map(v -> BuiltInRegistries.ITEM.getKey(v).toString())
                      .toList())
          .orElse(List.of());
    return BuiltInRegistries.ITEM.containsKey(id) && BuiltInRegistries.ITEM.get(id) != Items.AIR
        ? List.of(id.toString())
        : List.of();
  }

  public static List<String> draw(ServerPlayer player, Target target) {
    if (target.any()) throw new IllegalArgumentException("Any-item goals are not random draws");
    if (target.type().equals("stat") || target.type().equals("event") || target.type().equals("view"))
      return List.of(target.target());
    List<Choice> choices =
        new ArrayList<>(
            target.pool().isEmpty()
                ? resolve(target.type().equals("kill") ? "entity" : "item", target.target())
                    .stream()
                    .map(s -> new Choice(s, 1))
                    .toList()
                : pool(target.pool()));
    return drawChoices(player, choices, target.draws(), target.repeat());
  }

  public static List<String> alternatives(Target target) {
    return target.pool().isEmpty()
        ? resolve("item", target.target())
        : pool(target.pool()).stream().map(Choice::id).toList();
  }

  public static List<String> drawChoices(
      ServerPlayer player, List<Choice> source, int draws, boolean repeat) {
    List<Choice> choices = new ArrayList<>(source);
    if (choices.isEmpty() || (!repeat && choices.size() < draws))
      throw new IllegalArgumentException("Insufficient target pool");
    List<String> result = new ArrayList<>();
    for (int i = 0; i < draws; i++) {
      double sum = choices.stream().mapToDouble(Choice::weight).sum(),
          pick = player.getRandom().nextDouble() * sum;
      int selected = choices.size() - 1;
      for (int j = 0; j < choices.size(); j++) {
        pick -= choices.get(j).weight();
        if (pick < 0) {
          selected = j;
          break;
        }
      }
      result.add(choices.get(selected).id());
      if (!repeat) choices.remove(selected);
    }
    return result;
  }

  public static List<RawStack> rewards(ServerPlayer player, Reward reward) {
    List<RawStack> values = new ArrayList<>(reward.fixed());
    for (Target t : reward.random()) {
      if (t.any()) throw new IllegalArgumentException("Random rewards cannot use any selection");
      if (!t.type().equals("item"))
        throw new IllegalArgumentException("Rewards require item pools");
      for (String item : draw(player, t)) values.add(new RawStack(item, t.count()));
    }
    for (RawStack v : values)
      if (resolve("item", v.item()).isEmpty())
        throw new IllegalArgumentException("Unknown reward " + v.item());
    return List.copyOf(values);
  }

  public static void validateRegistries(Settings candidate) {
    Settings previous = settings;
    Map<String, List<Choice>> old = new HashMap<>(resolved);
    try {
      settings = candidate;
      resolved.clear();
      for (String key : candidate.pools().keySet()) pool(key);
      for (Template t : candidate.templates()) {
        CommissionConditions.validate(t.conditions());
        for (var rule : t.completionRules())
          if (resolve("item", rule.item()).isEmpty()) fail("unknown submitted item " + rule.item());
        if (!t.icon().isEmpty() && resolve("item", t.icon()).isEmpty())
          fail("unknown icon " + t.icon());
        for (Target target : t.targets()) {
          if (target.any() && alternatives(target).size() > 4096)
            fail("any-item source exceeds 4096 candidates");
          if (!target.pool().isEmpty()
              && (pool(target.pool()).isEmpty()
                  || !target.repeat() && pool(target.pool()).size() < target.draws()))
            fail("insufficient pool " + target.pool());
          if (target.type().equals("stat")) {
            var location = ResourceLocation.tryParse(target.target());
            if (!BuiltInRegistries.CUSTOM_STAT.containsKey(location))
              fail("unknown custom stat " + location);
          } else if (!target.type().equals("event") && !target.type().equals("view")
              && target.pool().isEmpty()
              && resolve(target.type().equals("kill") ? "entity" : "item", target.target())
                  .isEmpty()) fail("unknown target " + target.target());
          if ((target.type().equals("item") || target.type().equals("kill"))
              && target.pool().isEmpty()
              && !target.repeat()
              && resolve(target.type().equals("kill") ? "entity" : "item", target.target()).size()
                  < target.draws()) fail("insufficient selector " + target.target());
        }
        validateReward(t.reward() == null ? candidate.reward() : t.reward());
      }
      validateReward(candidate.milestoneReward());
    } finally {
      settings = previous;
      resolved.clear();
      resolved.putAll(old);
    }
  }

  private static void validateReward(Reward r) {
    for (RawStack s : r.fixed())
      if (resolve("item", s.item()).isEmpty()) fail("unknown reward " + s.item());
    for (Target t : r.random()) {
      if (t.any()) fail("rewards cannot use any selection");
      if (!t.type().equals("item")) fail("reward type");
      if (!t.pool().isEmpty()
          && (pool(t.pool()).isEmpty() || !t.repeat() && pool(t.pool()).size() < t.draws()))
        fail("insufficient reward pool");
      if (t.pool().isEmpty()
          && (resolve("item", t.target()).isEmpty()
              || !t.repeat() && resolve("item", t.target()).size() < t.draws()))
        fail("insufficient random reward selector");
    }
  }

  /** Shared by daily rolls and validation, including mixed named pools. */
  public static List<Choice> itemChoices(List<String> selectors) {
    Map<String, Choice> choices = new LinkedHashMap<>();
    for (String selector : selectors) {
      if (selector.startsWith("!")) continue;
      List<Choice> selected =
          selector.startsWith("$")
              ? pool(selector.substring(1))
              : resolve("item", selector).stream().map(id -> new Choice(id, 1)).toList();
      for (Choice choice : selected) choices.putIfAbsent(choice.id(), choice);
    }
    for (String selector : selectors)
      if (selector.startsWith("!"))
        for (String id : resolve("item", selector.substring(1))) choices.remove(id);
    return List.copyOf(choices.values());
  }

  static List<Choice> itemChoices(Settings candidate, List<String> selectors) {
    Settings previous = settings;
    Map<String, List<Choice>> old = new HashMap<>(resolved);
    try {
      settings = candidate;
      resolved.clear();
      return itemChoices(selectors);
    } finally {
      settings = previous;
      resolved.clear();
      resolved.putAll(old);
    }
  }

  public static void validateShop(
      Settings candidate,
      List<com.guoche.teyvatdelight.entity.katheryne.KatheryneShopConfig.RawOffer> offers,
      KatheryneShopConfig.Settings shop) {
    Settings previous = settings;
    Map<String, List<Choice>> old = new HashMap<>(resolved);
    try {
      settings = candidate;
      resolved.clear();
      for (var offer : offers) {
        validateStacks(offer.sell(), offer.id());
        validateStacks(offer.cost(), offer.id());
      }
      for (var slot : shop.dailySlots()) {
        validateStacks(slot.cost(), slot.id());
        if (slot.fixed()) validateStacks(slot.sell(), slot.id());
        else {
          for (String selector : slot.pool())
            if (selector.startsWith("$")) {
              Pool pool = candidate.pools().get(selector.substring(1));
              if (pool == null || !pool.type().equals("item"))
                fail("dailySlots[" + slot.id() + "] requires an item pool");
            }
          List<Choice> choices = itemChoices(slot.pool());
          if (choices.isEmpty() || !slot.repeat() && choices.size() < slot.drawCount())
            fail("dailySlots[" + slot.id() + "] has insufficient unique items");
        }
      }
    } finally {
      settings = previous;
      resolved.clear();
      resolved.putAll(old);
    }
  }

  private static void validateStacks(List<RawStack> stacks, String context) {
    for (RawStack stack : stacks)
      if (resolve("item", stack.item()).isEmpty()) fail(context + ": unknown item " + stack.item());
  }


  private static void fail(String detail) {
    throw new IllegalArgumentException("Katheryne configuration: " + detail);
  }

}
