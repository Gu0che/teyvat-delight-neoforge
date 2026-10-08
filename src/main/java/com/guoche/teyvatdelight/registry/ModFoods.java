package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.registry.food.BasicDishes;
import com.guoche.teyvatdelight.registry.food.MondstadtDishes;

/** Loads every dish module before the shared item registry is attached to the mod bus. */
public final class ModFoods {
    private ModFoods() {
    }

    public static void init() {
        BasicDishes.init();
        MondstadtDishes.init();
    }
}
