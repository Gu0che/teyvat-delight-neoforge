package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.block.KatheryneFigurineBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TeyvatDelight.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KatheryneFigurineBlockEntity>> KATHERYNE_FIGURINE =
            BLOCK_ENTITY_TYPES.register("katheryne_figurine", () -> BlockEntityType.Builder.of(
                    KatheryneFigurineBlockEntity::new, ModBlocks.KATHERYNE_FIGURINE.get()).build(null));

    private ModBlockEntityTypes() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
