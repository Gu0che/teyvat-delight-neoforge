package com.guoche.teyvatdelight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.katheryne.KatheryneRenderer} for new integrations. */
@Deprecated
public class KatheryneRenderer extends com.guoche.teyvatdelight.client.katheryne.KatheryneRenderer {
    public KatheryneRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
