package com.guoche.teyvatdelight.advancement;

import com.guoche.teyvatdelight.TeyvatAdvancementLayout;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class AdvancementEvents {


    private AdvancementEvents() {
    }

    public static void arrangeAdvancementDisplay(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profiler) {
                TeyvatAdvancementLayout.arrange(event.getServerResources().getAdvancements());
            }
        });
    }

}
