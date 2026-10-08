package com.guoche.teyvatdelight.client.config;

import com.google.gson.*;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.config.TeyvatDelightConfig;
import com.guoche.teyvatdelight.entity.katheryne.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Local content editing. There is deliberately no remote administrator network channel. */
public final class KatheryneEditorScreens {
  private KatheryneEditorScreens() {}

  public static Component text(String key, Object... args) {
    return Component.translatable("config.teyvatdelight." + key, args);
  }

  public static Screen open(Screen parent, Path directory) {
    EditorListScreen[] home = new EditorListScreen[1];
    try {
      var session = KatheryneConfigEditor.open(directory);
      home[0] = new EditorListScreen(parent, text("title"), List::of,
          () -> List.of(
              new EditorListScreen.Action(text("controls"),
                  () -> show(controls(home[0], session, directory))),
              new EditorListScreen.Action(text("shops"),
                  () -> show(shops(home[0], session, directory)))),
          null, () -> false);
      home[0].status(text(localAllowed() ? "local_scope" : "remote_disabled"));
    } catch (Exception e) {
      home[0] = new EditorListScreen(parent, text("title"), List::of, List::of, null, () -> false);
      home[0].status(error(e));
    }
    return home[0];
  }

  private static Screen controls(Screen parent, KatheryneConfigEditor.Session session, Path path) {
    JsonObject original = session.controls(), draft = original.deepCopy();
    boolean[] common = {TeyvatDelightConfig.COUNTRY_CHALLENGE_WIND_WINGS.get(),
        TeyvatDelightConfig.PRIMOGEMS_IN_CHESTS.get()};
    boolean[] initial = common.clone();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("controls"),
        () -> List.of(
            row(text("reward"), () -> show(rewards(screen[0], session,
                draft.getAsJsonObject("commissions"), "reward"))),
            row(text("milestoneReward"), () -> show(rewards(screen[0], session,
                draft.getAsJsonObject("commissions").getAsJsonObject("milestone"), "reward")))),
        () -> List.of(
            new EditorListScreen.Action(text("settings"), () -> {
              ClothFields f = new ClothFields(screen[0], text("controls"));
              JsonObject q = draft.getAsJsonObject("commissions");
              f.integer(q, "refreshTime", 22000, 0, 23999, "commissionRefreshTime");
              f.integer(q, "durationTicks", -1, -1, 8760000);
              f.integer(q, "dailyCount", 1, 1, 64);
              f.integer(q, "limit", 4, 1, 64);
              f.choice(q, "selection", "random", "random", "rotation");
              f.integer(q.getAsJsonObject("milestone"), "every", 4, 1, 1000000);
              f.integer(draft, "shopRefreshTime", -1, -1, 23999);
              f.strings(draft, "balanceItems",
                  List.of("teyvatdelight:mora", "teyvatdelight:primogem"), 2, true);
              JsonObject equipment = object(draft, "equipment");
              f.decimal(equipment, "firstChance", .7, 0, 1);
              f.decimal(equipment, "chanceStep", .1, 0, 1);
              f.bool(equipment, "allowCurses", false);
              f.toggle("countryChallengeWindWings", common[0], true, v -> common[0] = v);
              f.toggle("primogemsInChests", common[1], false, v -> common[1] = v);
              show(f.build());
            }),
            new EditorListScreen.Action(text("defaults"), () -> show(new ConfirmScreen(yes -> {
              if (yes) {
                draft.keySet().clear();
                KatheryneConfigEditor.defaults().entrySet()
                    .forEach(e -> draft.add(e.getKey(), e.getValue().deepCopy()));
                common[0] = true; common[1] = false;
              }
              show(screen[0]);
            }, text("defaults"), text("defaults_detail"))))),
        () -> save(screen[0], () -> {
          boolean changed = session.saveControls(draft, Minecraft.getInstance().hasSingleplayerServer());
          if (common[0] != initial[0] || common[1] != initial[1]) {
            TeyvatDelightConfig.COUNTRY_CHALLENGE_WIND_WINGS.set(common[0]);
            TeyvatDelightConfig.PRIMOGEMS_IN_CHESTS.set(common[1]);
            TeyvatDelightConfig.SPEC.save();
          }
          return changed;
        }, () -> KatheryneShopConfig.initialize(path), () -> show(controls(parent, session, path))),
        () -> !original.equals(draft) || !Arrays.equals(common, initial));
    screen[0].persistent();
    screen[0].status(text(localAllowed() ? "local_scope" : "remote_disabled"));
    return screen[0];
  }

  private static Screen shops(Screen parent, KatheryneConfigEditor.Session session, Path path) {
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("shops"), () -> session.shops().stream().map(id -> {
      JsonObject shop = session.shop(id);
      return new EditorListScreen.Row(label(shop, "title", id), List.of(),
          () -> show(shop(screen[0], session, path, id, shop)), null, null, null);
    }).toList(), () -> List.of(new EditorListScreen.Action(text("new_shop"), () -> {
      show(new NewShopScreen(screen[0], session.shops(), id -> {
        JsonObject shop = new JsonObject();
        shop.addProperty("title", ""); shop.addProperty("enabled", true);
        shop.addProperty("order", 1000); shop.addProperty("refreshTime", -1);
        shop.add("offers", new JsonArray());
        show(shop(screen[0], session, path, id, shop));
      }));
    })), null, () -> false);
    screen[0].status(text(localAllowed() ? "local_scope" : "remote_disabled"));
    return screen[0];
  }

  private static Screen shop(Screen parent, KatheryneConfigEditor.Session session, Path path,
      String id, JsonObject original) {
    JsonObject draft = original.deepCopy();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("shop_title", id),
        () -> rows(draft.getAsJsonArray("offers"), KatheryneEditorScreens::offerLabel,
            KatheryneEditorScreens::offerIcons,
            index -> show(offer(screen[0], session, draft.getAsJsonArray("offers"), index)),
            screen[0]),
        () -> List.of(
            new EditorListScreen.Action(text("properties"), () -> {
              ClothFields f = new ClothFields(screen[0], text("properties"));
              f.string(draft, "title", "", 256, false);
              f.bool(draft, "enabled", true);
              f.integer(draft, "order", 1000, -1000000, 1000000);
              f.integer(draft, "refreshTime", -1, -2, 23999);
              show(f.build());
            }),
            new EditorListScreen.Action(text("add_trade"), () -> {
              JsonArray list = draft.getAsJsonArray("offers");
              if (list.size() >= 1024) { screen[0].status(text("too_many")); return; }
              JsonObject added = new JsonObject();
              int serial = 1;
              Set<String> used = new HashSet<>();
              list.forEach(e -> used.add(e.getAsJsonObject().get("id").getAsString()));
              while (used.contains("trade_" + serial)) serial++;
              added.addProperty("id", "trade_" + serial);
              added.addProperty("name", ""); added.addProperty("enabled", true);
              added.addProperty("type", "fixed"); added.addProperty("dailyLimit", 10);
              JsonArray products = new JsonArray(); products.add(stack("minecraft:apple", 1));
              added.add("sell", products);
              JsonArray costs = new JsonArray(); costs.add(stack("teyvatdelight:mora", 3));
              added.add("cost", costs);
              list.add(added); screen[0].rebuild();
            })),
        () -> save(screen[0],
            () -> session.saveShop(id, draft, Minecraft.getInstance().hasSingleplayerServer()),
            KatheryneEditorScreens::reloadContent, () -> {
              show(parent);
              if (parent instanceof EditorListScreen list) list.status(text("saved"));
            }), () -> !original.equals(draft));
    screen[0].persistent();
    screen[0].status(text(localAllowed() ? "save_scope" : "remote_disabled"));
    return screen[0];
  }

  private static Screen offer(Screen parent, KatheryneConfigEditor.Session session,
      JsonArray list, int index) {
    JsonObject original = list.get(index).getAsJsonObject(), draft = original.deepCopy();
    String stableId = original.get("id").getAsString();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("trade_title", stableId),
        () -> List.of(
            row(text("properties"), () -> {
              ClothFields f = new ClothFields(screen[0], text("properties"));
              // Existing identity is not editable; display names have their own field.
              f.string(draft, "name", "", 256, false);
              f.bool(draft, "enabled", true);
              f.choice(draft, "type", "fixed", "fixed", "random");
              f.integer(draft, "dailyLimit", 10, -1, 1000000);
              f.integer(draft, "drawCount", 1, 1, 128);
              f.integer(draft, "count", 1, 1, 64, "randomCount");
              f.bool(draft, "repeat", false);
              f.bool(draft, "enchantEquipment", false);
              show(f.build());
            }),
            new EditorListScreen.Row(text("sell"), icons(array(draft, "sell")),
                () -> show(stacks(screen[0], array(draft, "sell"),
                    value -> draft.add("sell", value), true)), null, null, null),
            new EditorListScreen.Row(text("cost"), icons(array(draft, "cost")),
                () -> show(stacks(screen[0], array(draft, "cost"),
                    value -> draft.add("cost", value), false)), null, null, null),
            row(text("source"), () -> show(source(screen[0], session,
                draft.has("source") ? draft.get("source") : new JsonPrimitive("minecraft:apple"),
                value -> draft.add("source", value))))),
        List::of, () -> {
          boolean fixed = ClothFields.string(draft, "type", "fixed").equals("fixed");
          if (fixed && array(draft, "sell").isEmpty()) {
            screen[0].status(text("missing_sell")); return;
          }
          if (!fixed && !draft.has("source")) {
            screen[0].status(text("missing_source")); return;
          }
          list.set(index, draft.deepCopy());
          show(parent);
        }, () -> !original.equals(draft));
    return screen[0];
  }

  private static Screen stacks(Screen parent, JsonArray original, Consumer<JsonArray> accept,
      boolean required) {
    JsonArray draft = original.deepCopy();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text(required ? "sell" : "items"),
        () -> rows(draft, v -> Component.literal(ClothFields.string(v, "item", "")
                + " x" + ClothFields.number(v, "count", 1)), v -> icons(single(v)),
            i -> {
              JsonObject copy = draft.get(i).getAsJsonObject().deepCopy();
              ClothFields f = new ClothFields(screen[0], text("item"));
              f.string(copy, "item", "minecraft:apple", 256, true);
              f.integer(copy, "count", 1, 1, 1000000);
              Screen fields = f.build();
              // Save is a draft update, not a file write.
              setSaving(fields, () -> draft.set(i, copy));
              show(fields);
            }, screen[0]),
        () -> List.of(new EditorListScreen.Action(text("add_item"), () -> {
          if (draft.size() >= 128) { screen[0].status(text("too_many")); return; }
          draft.add(stack("minecraft:apple", 1)); screen[0].rebuild();
        })),
        () -> {
          Set<String> ids = new HashSet<>();
          for (JsonElement e : draft) {
            JsonObject item = e.getAsJsonObject();
            String id = item.get("item").getAsString();
            if (!KatheryneConfigEditor.validItem(id)
                || !ids.add(ResourceLocation.tryParse(id).toString())) {
              screen[0].status(text("invalid_list")); return;
            }
          }
          if (required && draft.isEmpty()) { screen[0].status(text("missing_sell")); return; }
          accept.accept(draft.deepCopy()); show(parent);
        }, () -> !original.equals(draft));
    return screen[0];
  }

  private static Screen source(Screen parent, KatheryneConfigEditor.Session session,
      JsonElement original, Consumer<JsonElement> accept) {
    JsonElement[] draft = {original.deepCopy()};
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("source"),
        () -> List.of(
            row(text("direct"), () -> {
              ClothFields f = new ClothFields(screen[0], text("direct"));
              f.literal("selector", draft[0].isJsonPrimitive() ? draft[0].getAsString()
                  : "minecraft:apple", value -> draft[0] = new JsonPrimitive(value), true);
              show(f.build());
            }),
            row(text("public_pool"), () -> {
              ClothFields f = new ClothFields(screen[0], text("public_pool"));
              String value = draft[0].isJsonObject() && draft[0].getAsJsonObject().has("pool")
                  ? draft[0].getAsJsonObject().get("pool").getAsString()
                  : session.pools().stream().findFirst().orElse("");
              f.literal("pool", value, id -> {
                if (!session.pools().contains(id)) {
                  screen[0].status(text("invalid_source")); return;
                }
                JsonObject ref = new JsonObject(); ref.addProperty("pool", id); draft[0] = ref;
              }, false);
              show(f.build());
            }),
            row(text("include"), () -> editCandidates(screen[0], session, draft[0],
                value -> draft[0] = value, false)),
            row(text("exclude"), () -> editCandidates(screen[0], session, draft[0],
                value -> draft[0] = value, true))),
        List::of, () -> {
          accept.accept(draft[0].deepCopy()); show(parent);
        }, () -> !original.equals(draft[0]));
    screen[0].status(() -> text("current_source", sourceSummary(draft[0])));
    return screen[0];
  }

  private static void editCandidates(Screen parent, KatheryneConfigEditor.Session session,
      JsonElement current, Consumer<JsonElement> accept, boolean exclude) {
    boolean referenced = current.isJsonObject() && current.getAsJsonObject().has("pool");
    Runnable edit = () -> {
      JsonObject inline = inlineSource(current);
      if (referenced) {
        JsonObject pool = session.pool(current.getAsJsonObject().get("pool").getAsString());
        inline.add("include", array(pool, "include").deepCopy());
        inline.add("exclude", array(pool, "exclude").deepCopy());
      }
      String key = exclude ? "exclude" : "include";
      show(candidates(parent, array(inline, key), values -> {
        inline.add(key, values); accept.accept(inline);
      }, exclude));
    };
    if (referenced) show(new ConfirmScreen(yes -> {
      if (yes) edit.run(); else show(parent);
    }, text("copy_pool"), text("copy_pool_detail")));
    else edit.run();
  }

  private static Screen candidates(Screen parent, JsonArray original,
      Consumer<JsonArray> accept, boolean exclude) {
    JsonArray draft = original.deepCopy();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text(exclude ? "exclude" : "include"),
        () -> {
          List<EditorListScreen.Row> result = new ArrayList<>();
          for (int i = 0; i < draft.size(); i++) {
            int index = i;
            JsonElement entry = draft.get(i);
            String value = entry.isJsonArray() ? entry.getAsJsonArray().get(0).getAsString()
                : entry.getAsString();
            double weight = entry.isJsonArray() ? entry.getAsJsonArray().get(1).getAsDouble() : 1;
            result.add(new EditorListScreen.Row(Component.literal(value
                + (exclude ? "" : " (" + weight + ")")), List.of(),
                () -> {
                  JsonObject edited = new JsonObject();
                  edited.addProperty("selector", value); edited.addProperty("weight", weight);
                  ClothFields f = new ClothFields(screen[0], text("candidate"));
                  f.literal("selector", value, v -> edited.addProperty("selector", v), true);
                  if (!exclude) f.decimal(edited, "weight", 1, Double.MIN_VALUE, 1000000);
                  Screen fields = f.build();
                  setSaving(fields, () -> {
                    if (exclude) draft.set(index, edited.get("selector"));
                    else {
                      JsonArray pair = new JsonArray();
                      pair.add(edited.get("selector")); pair.add(edited.get("weight"));
                      draft.set(index, pair);
                    }
                  });
                  show(fields);
                }, () -> { draft.remove(index); screen[0].rebuild(); },
                i == 0 ? null : () -> move(draft, index, index - 1, screen[0]),
                i == draft.size() - 1 ? null : () -> move(draft, index, index + 1, screen[0])));
          }
          return result;
        }, () -> List.of(new EditorListScreen.Action(text("add_candidate"), () -> {
          if (draft.size() >= 4096) { screen[0].status(text("too_many")); return; }
          draft.add("minecraft:apple"); screen[0].rebuild();
        })), () -> { accept.accept(draft.deepCopy()); show(parent); },
        () -> !original.equals(draft));
    return screen[0];
  }

  private static Screen rewards(Screen parent, KatheryneConfigEditor.Session session,
      JsonObject owner, String field) {
    JsonElement original = owner.get(field);
    JsonObject draft = session.reward(original);
    JsonElement[] reference = {original.deepCopy()};
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("reward"),
        () -> List.of(
            new EditorListScreen.Row(text("items"), icons(array(draft, "items")),
                () -> show(stacks(screen[0], array(draft, "items"),
                    v -> draft.add("items", v), false)), null, null, null),
            row(text("random_rewards"), () -> show(randomRewards(screen[0], session,
                array(draft, "random"), v -> draft.add("random", v)))),
            row(text("reward_reference"), () -> {
              ClothFields f = new ClothFields(screen[0], text("reward_reference"));
              f.literal("reward_reference", reference[0].isJsonPrimitive() ? reference[0].getAsString() : "",
                  id -> {
                    try {
                      JsonObject resolved = session.reward(new JsonPrimitive(id));
                      draft.keySet().clear();
                      resolved.entrySet().forEach(e -> draft.add(e.getKey(), e.getValue().deepCopy()));
                      reference[0] = new JsonPrimitive(id);
                    } catch (RuntimeException e) { screen[0].status(error(e)); }
                  }, false);
              show(f.build());
            })),
        List::of, () -> {
          owner.add(field, session.reward(reference[0]).equals(draft)
              ? reference[0].deepCopy() : draft.deepCopy());
          show(parent);
        }, () -> !original.equals(reference[0]) || !session.reward(original).equals(draft));
    return screen[0];
  }

  private static Screen randomRewards(Screen parent, KatheryneConfigEditor.Session session,
      JsonArray original, Consumer<JsonArray> accept) {
    JsonArray draft = original.deepCopy();
    EditorListScreen[] screen = new EditorListScreen[1];
    screen[0] = new EditorListScreen(parent, text("random_rewards"),
        () -> rows(draft, v -> text("random_reward_row", ClothFields.number(v, "drawCount", 1),
                ClothFields.number(v, "count", 1)), v -> List.of(),
            index -> {
              JsonObject target = draft.get(index).getAsJsonObject().deepCopy();
              EditorListScreen[] detail = new EditorListScreen[1];
              detail[0] = new EditorListScreen(screen[0], text("random_rewards"),
                  () -> List.of(
                      row(text("properties"), () -> {
                        ClothFields f = new ClothFields(detail[0], text("properties"));
                        f.integer(target, "drawCount", 1, 1, 16);
                        f.integer(target, "count", 1, 1, 1000000);
                        f.bool(target, "repeat", false);
                        show(f.build());
                      }),
                      row(text("source"), () -> show(source(detail[0], session, target.get("source"),
                          v -> target.add("source", v))))),
                  List::of, () -> { draft.set(index, target); show(screen[0]); },
                  () -> !draft.get(index).equals(target));
              show(detail[0]);
            }, screen[0]),
        () -> List.of(new EditorListScreen.Action(text("add_reward"), () -> {
          if (draft.size() >= 16) { screen[0].status(text("too_many")); return; }
          JsonObject value = new JsonObject();
          value.addProperty("type", "item"); value.addProperty("source", "minecraft:apple");
          value.addProperty("drawCount", 1); value.addProperty("count", 1);
          value.addProperty("repeat", false);
          draft.add(value); screen[0].rebuild();
        })), () -> { accept.accept(draft.deepCopy()); show(parent); },
        () -> !original.equals(draft));
    return screen[0];
  }

  private static List<EditorListScreen.Row> rows(JsonArray array,
      Function<JsonObject, Component> label, Function<JsonObject, List<ItemStack>> icons,
      IntConsumer edit, EditorListScreen screen) {
    List<EditorListScreen.Row> result = new ArrayList<>();
    for (int i = 0; i < array.size(); i++) {
      int index = i;
      JsonObject value = array.get(i).getAsJsonObject();
      result.add(new EditorListScreen.Row(label.apply(value), icons.apply(value),
          () -> edit.accept(index), () -> { array.remove(index); screen.rebuild(); },
          i == 0 ? null : () -> move(array, index, index - 1, screen),
          i == array.size() - 1 ? null : () -> move(array, index, index + 1, screen)));
    }
    return result;
  }

  private static void move(JsonArray array, int from, int to, EditorListScreen screen) {
    JsonElement item = array.get(from); array.set(from, array.get(to)); array.set(to, item);
    screen.rebuild();
  }

  private static void setSaving(Screen screen, Runnable runnable) {
    ((me.shedaniel.clothconfig2.gui.AbstractConfigScreen) screen).setSavingRunnable(runnable);
  }

  private static void save(EditorListScreen screen, IoSave write, Runnable reload,
      Runnable success) {
    if (!localAllowed()) { screen.status(text("remote_disabled")); return; }
    screen.busy(true);
    Minecraft mc = Minecraft.getInstance();
    Runnable task = () -> {
      try {
        boolean changed = write.run();
        if (changed) reload.run();
        mc.execute(() -> { screen.busy(false); success.run(); });
      } catch (Exception e) {
        TeyvatDelight.LOGGER.warn("Could not save local Katheryne configuration", e);
        mc.execute(() -> { screen.busy(false); screen.status(error(e)); show(screen); });
      }
    };
    var server = mc.getSingleplayerServer();
    if (server != null) server.execute(task);
    else task.run();
  }

  private static void reloadContent() {
    var server = Minecraft.getInstance().getSingleplayerServer();
    if (server == null) return;
    server.reloadResources(server.getPackRepository().getSelectedIds())
        .exceptionally(e -> {
          Minecraft.getInstance().execute(() -> show(new NoticeScreen(
              Minecraft.getInstance().screen, text("saved_reload_failed", e.getMessage()))));
          return null;
        });
  }

  private static boolean localAllowed() {
    var mc = Minecraft.getInstance();
    return mc.getConnection() == null || mc.hasSingleplayerServer();
  }

  private static Component error(Exception e) { return text("error", e.getMessage()); }
  private static void show(Screen screen) { Minecraft.getInstance().setScreen(screen); }
  private static EditorListScreen.Row row(Component title, Runnable edit) {
    return new EditorListScreen.Row(title, List.of(), edit, null, null, null);
  }
  private static JsonObject object(JsonObject owner, String key) {
    if (!owner.has(key)) owner.add(key, new JsonObject());
    return owner.getAsJsonObject(key);
  }
  private static JsonArray array(JsonObject owner, String key) {
    return owner.has(key) ? owner.getAsJsonArray(key) : new JsonArray();
  }
  private static JsonArray single(JsonObject value) {
    JsonArray a = new JsonArray(); a.add(value); return a;
  }
  private static JsonObject stack(String item, int count) {
    JsonObject value = new JsonObject();
    value.addProperty("item", item); value.addProperty("count", count); return value;
  }
  private static JsonObject inlineSource(JsonElement current) {
    if (current.isJsonObject() && current.getAsJsonObject().has("include"))
      return current.getAsJsonObject().deepCopy();
    JsonObject value = new JsonObject(); JsonArray include = new JsonArray();
    if (current.isJsonPrimitive()) include.add(current.getAsString());
    value.add("include", include); value.add("exclude", new JsonArray()); return value;
  }
  private static String sourceSummary(JsonElement current) {
    if (current.isJsonPrimitive()) return current.getAsString();
    JsonObject source = current.getAsJsonObject();
    if (source.has("pool")) return source.get("pool").getAsString();
    return text("source_counts", array(source, "include").size(),
        array(source, "exclude").size()).getString();
  }
  private static Component label(JsonObject value, String field, String fallback) {
    String text = ClothFields.string(value, field, "");
    return text.isEmpty() ? Component.literal(fallback) : Component.translatable(text);
  }
  private static List<ItemStack> offerIcons(JsonObject value) {
    return icons(array(value, "sell"));
  }
  private static Component offerLabel(JsonObject value) {
    String name = ClothFields.string(value, "name", "");
    if (!name.isEmpty()) return Component.translatable(name);
    if (ClothFields.string(value, "type", "fixed").equals("random"))
      return text("random_trade", ClothFields.string(value, "id", ""));
    var items = offerIcons(value);
    if (items.isEmpty()) return Component.literal(ClothFields.string(value, "id", ""));
    return Component.literal(String.join(" + ",
        items.stream().map(s -> s.getHoverName().getString()).toList()));
  }
  private static List<ItemStack> icons(JsonArray values) {
    List<ItemStack> result = new ArrayList<>();
    for (JsonElement value : values) {
      JsonObject stack = value.getAsJsonObject();
      String item = ClothFields.string(stack, "item", "");
      if (KatheryneConfigEditor.validItem(item))
        result.add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(item)),
            Math.min(64, ClothFields.number(stack, "count", 1))));
    }
    return result;
  }
  @FunctionalInterface private interface IoSave { boolean run() throws Exception; }

  private static final class NoticeScreen extends EditorListScreen {
    NoticeScreen(Screen parent, Component message) {
      super(parent, text("title"), List::of, List::of, null, () -> false);
      status(message);
    }
  }
}
