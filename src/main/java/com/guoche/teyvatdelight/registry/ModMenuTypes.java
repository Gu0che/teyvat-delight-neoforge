package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.KatheryneMenu;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Owns menu type registrations; holders are resolved by deferred suppliers. */
public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TeyvatDelight.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<KatheryneMenu>> KATHERYNE_MENU = MENUS.register(
            "katheryne", () -> new MenuType<>(KatheryneMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<com.guoche.teyvatdelight.inventory.AdeptiSeekersStoveMenu>> ADEPTI_SEEKERS_STOVE = MENUS.register(
            "adepti_seekers_stove", () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(
                    com.guoche.teyvatdelight.inventory.AdeptiSeekersStoveMenu::new));

    private ModMenuTypes() {
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
