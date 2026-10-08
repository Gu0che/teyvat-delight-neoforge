package com.guoche.teyvatdelight;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.StarconchEntity} for new integrations. */
@Deprecated
public class StarconchEntity extends com.guoche.teyvatdelight.entity.StarconchEntity {
    public StarconchEntity(EntityType<? extends StarconchEntity> entityType, Level level) {
        super(entityType, level);
    }
}
