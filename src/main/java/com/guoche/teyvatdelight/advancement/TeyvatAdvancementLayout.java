package com.guoche.teyvatdelight.advancement;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;

public final class TeyvatAdvancementLayout {
    private static final String TEYVAT_DELIGHT = "teyvat_delight";
    private static final String ROOT = "root";
    private static final String HEARTBEAT_MEMORY = "heartbeat_memory";
    private static final List<Slot> MAIN_ADVANCEMENTS = List.of(
            new Slot("all_commission_stories", 1.0F, -5.0F),
            new Slot("welcome_adventurers_guild", 2.0F, -5.0F),
            new Slot("measure_world", 3.0F, -5.0F),
            new Slot("searching_for_sword", 3.0F, -4.0F),
            new Slot("elemental_taste", 1.0F, -3.0F),
            new Slot("outlander_who_caught_the_wind", 2.0F, -3.0F),
            new Slot("food_archon", 3.0F, -3.0F),
            new Slot("got_teyvat_plant", 1.0F, -1.0F),
            new Slot("teyvat_botanist", 2.0F, -1.0F),
            new Slot("got_teyvat_biological_material", 1.0F, 0.0F),
            new Slot("teyvat_biologist", 2.0F, 0.0F),
            new Slot("was_it_worth_it", 3.0F, 0.0F),
            new Slot("got_teyvat_mineral", 1.0F, 1.0F),
            new Slot("teyvat_mineralogist", 2.0F, 1.0F),
            new Slot("got_mora", 1.0F, 2.0F),
            new Slot("got_primogem", 2.0F, 2.0F),
            new Slot("primogem_knife", 3.0F, 2.0F),
            new Slot("intertwined_fate", 1.0F, 4.0F),
            new Slot(HEARTBEAT_MEMORY, 2.0F, 4.0F)
    );
    private static final List<Slot> SPECIAL_DISH_GOALS = List.of(
            new Slot("watch_out_for_the_cold", 0.0F, 0.0F),
            new Slot("special_thick_cloud_pancakes", 1.0F, 0.0F),
            new Slot("special_fish_flavored_toast", 2.0F, 0.0F),
            new Slot("special_magical_meat_sauce_pasta", 3.0F, 0.0F),
            new Slot("special_spicy_vegetable_stew", 4.0F, 0.0F),
            new Slot("special_supreme_wisdom_life", 5.0F, 0.0F),
            new Slot("special_dinners_judgment", 6.0F, 0.0F),
            new Slot("special_hearty_revelry", 7.0F, 0.0F),
            new Slot("special_blessed_symphony", 8.0F, 0.0F),
            new Slot("special_puppy_paw_hash_brown", 9.0F, 0.0F),
            new Slot("special_once_upon_a_time_in_mondstadt", 10.0F, 0.0F),
            new Slot("special_nutritious_meal_593", 11.0F, 0.0F),
            new Slot("special_invigorating_pizza", 12.0F, 0.0F),
            new Slot("special_true_barbatos_ratatouille", 13.0F, 0.0F),
            new Slot("special_forest_dream", 14.0F, 0.0F),
            new Slot("special_surveyors_egg_burger", 15.0F, 0.0F),
            new Slot("special_night_talk", 16.0F, 0.0F),
            new Slot("special_first_try_apple_stew", 17.0F, 0.0F),
            new Slot("special_boredom_bubble_gum", 18.0F, 0.0F),
            new Slot("special_surf_pie", 19.0F, 0.0F),
            new Slot("benny_adventure_team_set_out", 20.0F, 0.0F),
            new Slot("bunny_baron_go", 21.0F, 0.0F),
            new Slot("no_alcohol_allowed", 22.0F, 0.0F),
            new Slot("slay_haishan_too", 0.0F, 1.0F),
            new Slot("tianquan_craft", 1.0F, 1.0F),
            new Slot("time_for_tea", 2.0F, 1.0F),
            new Slot("where_is_the_offal", 3.0F, 1.0F),
            new Slot("one_grill_of_kazuha", 0.0F, 2.0F),
            new Slot("tengu_not_dog", 1.0F, 2.0F),
            new Slot("energy_replenished", 2.0F, 2.0F),
            new Slot("thomas_kindness", 3.0F, 2.0F),
            new Slot("can_this_really_be_eaten", 0.0F, 3.0F),
            new Slot("gentlemans_salute", 0.0F, 4.0F),
            new Slot("distant_horizon", 0.0F, 5.0F)
    );

    private TeyvatAdvancementLayout() {
    }

    public static void arrange(ServerAdvancementManager manager) {
        Optional<DisplayInfo> tabRoot = display(manager, TEYVAT_DELIGHT);
        Optional<DisplayInfo> root = display(manager, ROOT);
        if (tabRoot.isEmpty() || root.isEmpty()) {
            return;
        }

        root.get().setLocation(tabRoot.get().getX() + 1.0F, tabRoot.get().getY());
        float rootX = root.get().getX();
        float rootY = root.get().getY();
        for (Slot slot : MAIN_ADVANCEMENTS) {
            place(manager, slot.id(), rootX + slot.x(), rootY + slot.y());
        }

        Optional<DisplayInfo> anchor = display(manager, HEARTBEAT_MEMORY);
        if (anchor.isEmpty()) {
            return;
        }
        float baseX = anchor.get().getX() + 1.0F;
        float baseY = anchor.get().getY();
        for (Slot slot : SPECIAL_DISH_GOALS) {
            place(manager, slot.id(), baseX + slot.x(), baseY + slot.y());
        }
    }

    private static void place(ServerAdvancementManager manager, String id, float x, float y) {
        display(manager, id).ifPresent(display -> display.setLocation(x, y));
    }

    private static Optional<DisplayInfo> display(ServerAdvancementManager manager, String id) {
        AdvancementHolder holder = manager.get(ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "main/" + id));
        if (holder == null) {
            return Optional.empty();
        }
        return holder.value().display();
    }

    private record Slot(String id, float x, float y) {
    }
}
