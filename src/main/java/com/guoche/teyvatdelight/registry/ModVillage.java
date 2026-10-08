package com.guoche.teyvatdelight.registry;

import com.google.common.collect.ImmutableSet;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Owns villager and POI registrations; holders are resolved by deferred suppliers. */
public final class ModVillage {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, TeyvatDelight.MODID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, TeyvatDelight.MODID);

    public static final DeferredHolder<PoiType, PoiType> TEYVAT_MERCHANT_POI = POI_TYPES.register(
            "teyvat_merchant",
            () -> new PoiType(
                    ImmutableSet.copyOf(ModBlocks.MORA_BLOCK.get().getStateDefinition().getPossibleStates()),
                    1,
                    1
            )
    );

    public static final DeferredHolder<VillagerProfession, VillagerProfession> TEYVAT_MERCHANT_PROFESSION = VILLAGER_PROFESSIONS.register(
            "teyvat_merchant",
            () -> new VillagerProfession(
                    "teyvat_merchant",
                    holder -> holder.is(TEYVAT_MERCHANT_POI),
                    holder -> holder.is(TEYVAT_MERCHANT_POI),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_FARMER
            )
    );

    private ModVillage() {
    }

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
    }
}
