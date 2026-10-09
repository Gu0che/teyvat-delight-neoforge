package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.*;
import com.guoche.teyvatdelight.KatheryneShopConfig.RawStack;
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
    JsonObject q = root.getAsJsonObject("commissions");
    Map<String, Pool> pools = new LinkedHashMap<>();
    JsonObject definitions = root.getAsJsonObject("pools");
    if (definitions == null || definitions.size() > 1024) fail("pools");
    for (var field : definitions.entrySet()) {
      id(field.getKey());
      JsonObject p = field.getValue().getAsJsonObject();
      String type = text(p, "type", "item", 128);
      if (!type.equals("item") && !type.equals("entity")) fail("pool type: " + field.getKey());
      List<Entry> entries = new ArrayList<>();
      JsonArray include = p.getAsJsonArray("include");
      if (include == null || include.size() > 4096) fail("pool include");
      for (JsonElement e : include) {
        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
          entries.add(new Entry(e.getAsString(), 1));
        else if (e.isJsonArray()) {
          JsonArray pair = e.getAsJsonArray();
          if (pair.size() != 2 || !pair.get(0).isJsonPrimitive()
              || !pair.get(0).getAsJsonPrimitive().isString())
            fail("weighted candidate must be [\"ID or #tag\", weight]");
          entries.add(new Entry(pair.get(0).getAsString(), weight(pair.get(1))));
        } else fail("candidate must be an ID, #tag or [\"ID or #tag\", weight]");
      }
      List<String> exclude = new ArrayList<>();
      if (p.has("exclude"))
        for (JsonElement e : p.getAsJsonArray("exclude")) {
          if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString())
            fail("pool exclusions must be strings");
          exclude.add(e.getAsString());
        }
      if (exclude.size() > 4096) fail("pool exclusions");
      pools.put(field.getKey(), new Pool(type, List.copyOf(entries), List.copyOf(exclude)));
    }
    if (q == null) fail("commissions");
    int limit = number(q, "limit", 4, 1, MAX_QUESTS), count = number(q, "dailyCount", 1, 1, limit);
    Reward rewards = reward(q.getAsJsonObject("reward"));
    JsonObject milestone = q.getAsJsonObject("milestone");
    if (milestone == null) fail("milestone");
    int every = number(milestone, "every", 4, 1, 1000000);
    Reward bonus = reward(milestone.getAsJsonObject("reward"));
    List<Template> templates = new ArrayList<>();
    Set<String> ids = new HashSet<>();
    JsonArray list = q.getAsJsonArray("templates");
    if (list == null || list.size() > 1024) fail("templates");
    for (JsonElement element : list) {
      JsonObject t = element.getAsJsonObject();
      String key = text(t, "id", "", 128);
      id(key);
      if (!ids.add(key)) fail("duplicate template " + key);
      String icon = text(t, "icon", "", 256);
      if (!icon.isEmpty() && ResourceLocation.tryParse(icon) == null) fail("icon");
      String type = text(t, "type", "objectives", 16);
      if (!Set.of("objectives", "view").contains(type)) fail("commission type " + type);
      List<Target> goals = new ArrayList<>();
      if (!type.equals("view") || t.has("targets") && !t.getAsJsonArray("targets").isEmpty())
        goals.addAll(targets(t.getAsJsonArray("targets")));
      if (type.equals("view"))
        goals.add(new Target("view", "teyvatdelight:view_information", "", 1, 1, false));
      templates.add(
          new Template(
              key,
              text(t, "title", "", 256),
              text(t, "description", "", MAX_TEXT),
              icon,
              weight(t),
              List.copyOf(goals),
              !t.has("reward") || t.get("reward").isJsonPrimitive()
                  && t.get("reward").getAsJsonPrimitive().isString()
                  && t.get("reward").getAsString().isEmpty()
                  ? null : reward(t.getAsJsonObject("reward")),
              !t.has("repeatable") || booleanValue(t.get("repeatable")),
              t.has("important") && booleanValue(t.get("important")),
              advancementIds(t),
              t.has("urgent") && booleanValue(t.get("urgent")),
              CommissionConditions.parse(t.get("conditions")),
              CommissionCompletionRule.parse(t.get("completionRules")),
              text(t, "introduction", "", 8192),
              t.has("previewBeforeUnlock") && booleanValue(t.get("previewBeforeUnlock"))));
    }
    String selection = text(q, "selection", "random", 16);
    if (!Set.of("random", "rotation").contains(selection))
      fail("selection must be random or rotation");
    Settings result =
        new Settings(
            number(q, "refreshTime", 22000, 0, 23999),
            number(q, "durationTicks", -1, -1, 8760000),
            count,
            limit,
            selection.equals("rotation"),
            rewards,
            every,
            bonus,
            Map.copyOf(pools),
            List.copyOf(templates));
    for (Template t : templates) {
      validateTargets(t.targets(), pools);
      Set<String> allAdvancements = new HashSet<>(t.completionAdvancements());
      for (var rule : t.completionRules()) allAdvancements.addAll(rule.advancements());
      if (allAdvancements.size() > 64) fail("combined completion advancements exceed 64 IDs");
    }
    for (Template t : templates)
      if (t.reward() != null) validateTargets(t.reward().random(), pools);
    validateTargets(rewards.random(), pools);
    validateTargets(bonus.random(), pools);
    for (Pool p : pools.values())
      for (Entry e : p.include()) {
        if (e.selector().startsWith("$"))
          fail("nested pool references are not supported; reference pools at consumers");
        selector(p.type(), e.selector());
      }
    for (Pool p : pools.values()) for (String e : p.exclude()) selector(p.type(), e);
    int perQuest = 0;
    for (Template t : templates) {
      Reward r = t.reward() == null ? rewards : t.reward();
      int rewardSize =
          new Gson().toJson(r.fixed()).length()
              + r.random().stream().mapToInt(v -> v.draws() * 320).sum();
      int size =
          new Gson().toJson(t.title()).length()
              + new Gson().toJson(t.description()).length()
              + new Gson().toJson(t.introduction()).length()
              + t.icon().length()
              + 320
              + t.targets().stream().mapToInt(v -> v.draws() * 352).sum()
              + rewardSize;
      perQuest = Math.max(perQuest, size);
    }
    if ((long) perQuest + new Gson().toJson(bonus.fixed()).length()
        + bonus.random().stream().mapToInt(v -> v.draws() * 320).sum() > 180000)
      fail("a commission exceeds safe page size; reduce objectives, text or rewards");
    return result;
  }

  private static List<String> advancementIds(JsonObject value) {
    if (!value.has("completionAdvancements")) return List.of();
    JsonArray ids = value.getAsJsonArray("completionAdvancements");
    if (ids == null || ids.size() > 64) fail("completionAdvancements must contain at most 64 IDs");
    LinkedHashSet<String> result = new LinkedHashSet<>();
    for (JsonElement id : ids) {
      if (!id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString()
          || id.getAsString().length() > 256
          || ResourceLocation.tryParse(id.getAsString()) == null)
        fail("completion advancement ID");
      result.add(id.getAsString());
    }
    return List.copyOf(result);
  }

  private static void validateTargets(List<Target> list, Map<String, Pool> pools) {
    for (Target t : list) {
      if (!Set.of("item", "kill", "stat", "event", "view").contains(t.type()))
        fail("target type " + t.type());
      if (!Set.of("random", "any").contains(t.selection()))
        fail("target selection must be random or any");
      if (t.any() && (!t.type().equals("item") || t.draws() != 1 || t.repeat()))
        fail("any selection requires an item target, drawCount 1 and repeat false");
      if (!t.target().isEmpty() && !t.pool().isEmpty())
        fail("target and pool are mutually exclusive");
      if (!t.pool().isEmpty()) {
        Pool p = pools.get(t.pool());
        if (p == null
            || !p.type().equals(t.type().equals("kill") ? "entity" : "item")
            || t.type().equals("stat")
            || t.type().equals("event")) fail("pool reference " + t.pool());
      } else if (ResourceLocation.tryParse(t.target().replaceFirst("^#", "")) == null)
        fail("target " + t.target());
    }
  }

  private static List<Target> targets(JsonArray array) {
    if (array == null || array.isEmpty() || array.size() > MAX_TARGETS)
      fail("targets must contain 1..16 entries");
    List<Target> list = new ArrayList<>();
    for (JsonElement e : array) {
      JsonObject t = e.getAsJsonObject();
      if (text(t, "type", "item", 16).equals("view"))
        fail("Use commission type view; its final viewing step is added automatically");
      list.add(
          new Target(
              text(t, "type", "item", 16),
              text(t, "target", "", 256),
              text(t, "pool", "", 128),
              number(t, "count", 1, 1, 1000000),
              number(t, "drawCount", 1, 1, MAX_TARGETS),
              t.has("repeat") && booleanValue(t.get("repeat")),
              text(t, "selection", "random", 16),
              text(t, "name", "", 256),
              text(t, "tag", "", 256)));
    }
    if (list.stream().mapToInt(Target::draws).sum() > MAX_TARGETS) fail("too many rolled targets");
    return List.copyOf(list);
  }

  private static Reward reward(JsonObject r) {
    if (r == null) fail("reward must be an object; use {} for no reward");
    List<RawStack> fixed = new ArrayList<>();
    if (r.has("items"))
      for (JsonElement e : r.getAsJsonArray("items")) {
        JsonObject stack = e.getAsJsonObject();
        String item = text(stack, "item", "", 256);
        if (ResourceLocation.tryParse(item) == null) fail("reward item");
        fixed.add(new RawStack(item, number(stack, "count", 1, 1, 1000000)));
      }
    if (fixed.size() > 128) fail("too many rewards");
    return new Reward(
        List.copyOf(fixed), r.has("random") && !r.getAsJsonArray("random").isEmpty()
            ? targets(r.getAsJsonArray("random")) : List.of());
  }

  private static void selector(String type, String text) {
    if (text.length() > 256) fail("selector too long");
    if (text.equals("@merchant_ingredients") && type.equals("item")) return;
    if (ResourceLocation.tryParse(text.replaceFirst("^#", "")) == null) fail("selector " + text);
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
      return com.guoche.teyvatdelight.TeyvatMerchantCatalog.INGREDIENTS.stream()
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
      List<com.guoche.teyvatdelight.KatheryneShopConfig.RawOffer> offers,
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

  public static JsonObject defaultRoot() {
    JsonObject root = KatheryneDataPack.defaultsRoot();
    root.keySet().removeIf(key -> !key.equals("commissions") && !key.equals("pools"));
    stripNotes(root);
    for (var entry : root.getAsJsonObject("pools").entrySet())
      entry.getValue().getAsJsonObject().remove("id");
    return root;
  }

  private static void stripNotes(JsonElement element) {
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      object.remove("_comment");
      object.remove("_help");
      object.remove("order");
      object.entrySet().forEach(entry -> stripNotes(entry.getValue()));
    } else if (element.isJsonArray()) {
      for (JsonElement child : element.getAsJsonArray()) stripNotes(child);
    }
  }

  public static int number(JsonObject o, String k, int fallback, int min, int max) {
    if (!o.has(k)) return fallback;
    if (!o.get(k).isJsonPrimitive() || !o.get(k).getAsJsonPrimitive().isNumber())
      fail(k + " must be a number");
    int value = new java.math.BigDecimal(o.get(k).getAsString()).intValueExact();
    if (value < min || value > max) fail(k + " must be " + min + ".." + max);
    return value;
  }

  private static String text(JsonObject o, String k, String fallback, int max) {
    if (!o.has(k)) return fallback;
    if (!o.get(k).isJsonPrimitive() || !o.get(k).getAsJsonPrimitive().isString()) fail(k);
    String text = o.get(k).getAsString().trim();
    if (text.length() > max) fail(k + " too long");
    return text;
  }

  private static double weight(JsonObject o) {
    return o.has("weight") ? weight(o.get("weight")) : 1;
  }

  private static double weight(JsonElement value) {
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
      fail("weight must be a number");
    double w = value.getAsDouble();
    if (!Double.isFinite(w) || w <= 0 || w > 1000000) fail("weight");
    return w;
  }

  private static void id(String id) {
    if (!id.matches("[a-z0-9_:/.-]{1,128}")) fail("id " + id);
  }

  private static boolean booleanValue(JsonElement value) {
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean())
      fail("Expected a JSON boolean");
    return value.getAsBoolean();
  }

  private static void fail(String detail) {
    throw new IllegalArgumentException("Katheryne configuration: " + detail);
  }
}
