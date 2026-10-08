package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class KatheryneSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TeyvatDelight.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> INTERACT = register("entity.katheryne.interact");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMMISSION_COMPLETE =
            register("entity.katheryne.commission_complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_ANOMALY = register("entity.katheryne.idle_anomaly");

    private KatheryneSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

    public static boolean interruptPreviousVoice(ResourceLocation nextSound, Consumer<ResourceLocation> stopSound) {
        ResourceLocation welcome = INTERACT.getId();
        ResourceLocation thanks = COMMISSION_COMPLETE.getId();
        if (!nextSound.equals(welcome) && !nextSound.equals(thanks)) return false;
        stopSound.accept(welcome);
        stopSound.accept(thanks);
        return true;
    }
}
