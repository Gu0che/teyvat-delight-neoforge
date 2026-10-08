package com.guoche.teyvatdelight;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds} for new integrations. */
@Deprecated
public final class KatheryneSounds {
    private KatheryneSounds() {
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> INTERACT = com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds.INTERACT;

    public static final DeferredHolder<SoundEvent, SoundEvent> COMMISSION_COMPLETE = com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds.COMMISSION_COMPLETE;

    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_ANOMALY = com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds.IDLE_ANOMALY;

    public static void register(IEventBus modEventBus) {
        com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds.register(modEventBus);
    }
}
