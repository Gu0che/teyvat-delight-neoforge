package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneSounds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public final class KatheryneVoiceEvents {
    private KatheryneVoiceEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void replaceInteractionVoice(PlaySoundEvent event) {
        var sound = event.getSound();
        if (sound == null) return;
        KatheryneSounds.interruptPreviousVoice(sound.getLocation(), id -> event.getEngine().stop(id, null));
    }
}
