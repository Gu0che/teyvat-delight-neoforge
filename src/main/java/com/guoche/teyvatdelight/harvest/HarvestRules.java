package com.guoche.teyvatdelight.harvest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.harvest.HarvestContext;
import com.guoche.teyvatdelight.api.harvest.HarvestResult;
import com.guoche.teyvatdelight.api.harvest.HarvestResult.Source;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Atomically reloaded, ordered modifiers. Empty default rules preserve all existing yields. */
@EventBusSubscriber(modid = TeyvatDelight.MODID)
public final class HarvestRules {
    private record Rule(ResourceLocation id, int order, JsonObject match, List<Change> changes) {}
    private record Change(Source source, String operation, String item, String output,
            int count, double factor, float chance) {}
    private static volatile List<Rule> rules = List.of();
    private HarvestRules() {}

    @SubscribeEvent
    public static void reload(AddReloadListenerEvent event) { event.addListener(new Reload()); }
    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) { rules = List.of(); }

    static void apply(HarvestContext context, HarvestResult result) {
        for (var rule : rules) {
            if (!matches(context, rule.match())) continue;
            for (var change : rule.changes()) {
                Predicate<HarvestResult.Entry> test = e -> e.source() == change.source()
                        && (change.item().isEmpty() || itemMatches(e.stack(), change.item()));
                switch (change.operation()) {
                    case "multiply" -> result.multiply(test, change.factor());
                    case "remove" -> result.remove(test);
                    case "add" -> {
                        if (change.chance() >= 1 || context.level().random.nextFloat() < change.chance())
                            result.add(change.source(), new ItemStack(BuiltInRegistries.ITEM.get(id(change.output())), change.count()));
                    }
                    case "replace" -> {
                        var selected = result.entries().stream().filter(test).toList();
                        result.remove(test);
                        for (var old : selected)
                            result.add(change.source(), new ItemStack(BuiltInRegistries.ITEM.get(id(change.output())), old.stack().getCount()));
                    }
                    default -> throw new IllegalStateException(change.operation());
                }
            }
        }
    }

    private static boolean matches(HarvestContext context, JsonObject match) {
        if (!flag(match, "mature", context.mature()) || !flag(match, "wild", context.wild())
                || !flag(match, "preferredField", context.preferredField())
                || !flag(match, "player", context.player() != null)) return false;
        if (match.has("methods") && !any(match.getAsJsonArray("methods"),
                e -> e.getAsString().equals(context.method().name().toLowerCase(Locale.ROOT)))) return false;
        if (match.has("specialties") && !any(match.getAsJsonArray("specialties"), e -> {
            var value = e.getAsString();
            return value.startsWith("#")
                    ? itemMatches(new ItemStack(BuiltInRegistries.ITEM.get(context.specialty())), value)
                    : id(value).equals(context.specialty());
        })) return false;
        if (match.has("blocks") && !any(match.getAsJsonArray("blocks"), e -> blockMatches(context.state(), e.getAsString()))) return false;
        return !match.has("fields") || any(match.getAsJsonArray("fields"), e -> blockMatches(context.soil(), e.getAsString()));
    }

    private static boolean flag(JsonObject match, String name, boolean value) {
        return !match.has(name) || match.get(name).getAsBoolean() == value;
    }
    private static boolean any(JsonArray array, Predicate<JsonElement> test) {
        for (var value : array) if (test.test(value)) return true;
        return false;
    }
    private static boolean itemMatches(ItemStack stack, String selector) {
        return selector.startsWith("#")
                ? stack.is(TagKey.create(Registries.ITEM, id(selector.substring(1))))
                : stack.is(BuiltInRegistries.ITEM.get(id(selector)));
    }
    private static boolean blockMatches(BlockState state, String selector) {
        return selector.startsWith("#")
                ? state.is(TagKey.create(Registries.BLOCK, id(selector.substring(1))))
                : state.is(BuiltInRegistries.BLOCK.get(id(selector)));
    }
    private static ResourceLocation id(String value) {
        var id = ResourceLocation.tryParse(value);
        if (id == null) throw new IllegalArgumentException("Invalid resource ID: " + value);
        return id;
    }
    private static String text(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }
    private static void item(String value) {
        if (!BuiltInRegistries.ITEM.containsKey(id(value))) throw new IllegalArgumentException("Unknown item: " + value);
    }
    private static void selectors(JsonObject match, String key, boolean block) {
        if (!match.has(key)) return;
        var list = match.getAsJsonArray(key);
        if (list.isEmpty() || list.size() > 128) throw new IllegalArgumentException("Invalid selector list: " + key);
        for (var entry : list) {
            String value = entry.getAsString();
            if (value.startsWith("#")) id(value.substring(1));
            else if (block) {
                if (!BuiltInRegistries.BLOCK.containsKey(id(value))) throw new IllegalArgumentException("Unknown block: " + value);
            } else id(value);
        }
    }
    private static int integer(JsonObject json, String key, int fallback) {
        if (!json.has(key)) return fallback;
        var value = json.get(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
            throw new IllegalArgumentException("Expected integer: " + key);
        try { return value.getAsBigDecimal().intValueExact(); }
        catch (ArithmeticException e) { throw new IllegalArgumentException("Expected integer: " + key, e); }
    }
    private static Rule parse(ResourceLocation location, JsonObject json) {
        if (json.has("enabled") && (!json.get("enabled").isJsonPrimitive()
                || !json.get("enabled").getAsJsonPrimitive().isBoolean()))
            throw new IllegalArgumentException("Expected boolean: enabled");
        if (json.has("enabled") && !json.get("enabled").getAsBoolean()) return null;
        for (var key : json.keySet())
            if (!List.of("enabled", "order", "match", "changes").contains(key))
                throw new IllegalArgumentException("Unknown harvest field: " + key);
        var match = json.has("match") ? json.getAsJsonObject("match") : new JsonObject();
        for (var key : match.keySet()) {
            if (!List.of("mature", "wild", "preferredField", "player", "methods", "specialties", "blocks", "fields").contains(key))
                throw new IllegalArgumentException("Unknown harvest match: " + key);
            if (List.of("mature", "wild", "preferredField", "player").contains(key)
                    && (!match.get(key).isJsonPrimitive() || !match.get(key).getAsJsonPrimitive().isBoolean()))
                throw new IllegalArgumentException("Expected boolean: " + key);
        }
        selectors(match, "specialties", false);
        selectors(match, "blocks", true);
        selectors(match, "fields", true);
        if (match.has("methods")) {
            if (match.getAsJsonArray("methods").isEmpty()) throw new IllegalArgumentException("Empty methods");
            for (var method : match.getAsJsonArray("methods"))
                HarvestContext.Method.valueOf(method.getAsString().toUpperCase(Locale.ROOT));
        }
        var changes = new ArrayList<Change>();
        for (var element : json.getAsJsonArray("changes")) {
            var change = element.getAsJsonObject();
            for (var key : change.keySet())
                if (!List.of("source", "operation", "item", "output", "count", "factor", "chance").contains(key))
                    throw new IllegalArgumentException("Unknown harvest change: " + key);
            var source = Source.valueOf(text(change, "source", "base").toUpperCase(Locale.ROOT));
            String operation = text(change, "operation", "");
            if (!List.of("multiply", "remove", "add", "replace").contains(operation))
                throw new IllegalArgumentException("Unknown harvest operation: " + operation);
            String filter = text(change, "item", "");
            if (!filter.isEmpty()) {
                if (filter.startsWith("#")) id(filter.substring(1));
                else item(filter);
            }
            String output = text(change, "output", "");
            if (operation.equals("add") || operation.equals("replace")) item(output);
            int count = integer(change, "count", 1);
            double factor = change.has("factor") ? change.get("factor").getAsDouble() : 1;
            float chance = change.has("chance") ? change.get("chance").getAsFloat() : 1;
            if (count < 1 || count > HarvestResult.MAX_COUNT || !Double.isFinite(factor)
                    || factor < 0 || factor > HarvestResult.MAX_COUNT || !Float.isFinite(chance) || chance < 0 || chance > 1)
                throw new IllegalArgumentException("Invalid harvest amount, factor or chance");
            changes.add(new Change(source, operation, filter, output, count, factor, chance));
        }
        if (changes.isEmpty() || changes.size() > 128) throw new IllegalArgumentException("Invalid changes size");
        return new Rule(location, integer(json, "order", 0), match.deepCopy(), List.copyOf(changes));
    }

    private static final class Reload extends SimplePreparableReloadListener<List<Rule>> {
        @Override
        protected List<Rule> prepare(ResourceManager manager, ProfilerFiller profiler) {
            var prepared = new ArrayList<Rule>();
            var resources = manager.listResources("harvest_modifiers", path -> path.getPath().endsWith(".json"));
            for (var entry : resources.entrySet()) {
                try (var reader = entry.getValue().openAsReader()) {
                    var rule = parse(entry.getKey(), JsonParser.parseReader(reader).getAsJsonObject());
                    if (rule != null) prepared.add(rule);
                } catch (IOException | RuntimeException e) {
                    throw new IllegalArgumentException("Invalid harvest modifier " + entry.getKey(), e);
                }
            }
            prepared.sort(Comparator.comparingInt(Rule::order).thenComparing(r -> r.id().toString()));
            return List.copyOf(prepared);
        }
        @Override
        protected void apply(List<Rule> prepared, ResourceManager manager, ProfilerFiller profiler) {
            rules = prepared;
            TeyvatDelight.LOGGER.info("Loaded {} harvest modifiers", rules.size());
        }
    }
}
