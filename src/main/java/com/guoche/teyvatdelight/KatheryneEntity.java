package com.guoche.teyvatdelight;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** @deprecated Use {@link com.guoche.teyvatdelight.entity.katheryne.KatheryneEntity} for new integrations. */
@Deprecated
public class KatheryneEntity extends com.guoche.teyvatdelight.entity.katheryne.KatheryneEntity {
    public KatheryneEntity(EntityType<? extends KatheryneEntity> type, Level level) {
        super(type, level);
    }
}
