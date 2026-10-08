package com.guoche.teyvatdelight.entity;

import com.guoche.teyvatdelight.EmptyFeatherMothEntity;
import com.guoche.teyvatdelight.KatheryneEntity;
import com.guoche.teyvatdelight.StarconchEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID)
public class TeyvatDelightEntityEvents {
    @SubscribeEvent
    public static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(TeyvatDelight.STARCONCH.get(), StarconchEntity.createAttributes().build());
        event.put(TeyvatDelight.EMPTY_FEATHER_MOTH.get(), EmptyFeatherMothEntity.createAttributes().build());
        event.put(TeyvatDelight.KATHERYNE.get(), KatheryneEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                TeyvatDelight.STARCONCH.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StarconchEntity::checkStarconchSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
                TeyvatDelight.EMPTY_FEATHER_MOTH.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                EmptyFeatherMothEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
    }
}
