package com.guoche.teyvatdelight;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.EmptyFeatherMothEntity} for new integrations. */
@Deprecated
public class EmptyFeatherMothEntity extends com.guoche.teyvatdelight.entity.EmptyFeatherMothEntity {
    public EmptyFeatherMothEntity(EntityType<? extends EmptyFeatherMothEntity> entityType, Level level) {
        super(entityType, level);
    }
}
