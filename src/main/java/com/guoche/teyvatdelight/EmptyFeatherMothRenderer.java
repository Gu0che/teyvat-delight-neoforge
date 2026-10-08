package com.guoche.teyvatdelight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.EmptyFeatherMothRenderer} for new integrations. */
@Deprecated
public class EmptyFeatherMothRenderer extends com.guoche.teyvatdelight.client.EmptyFeatherMothRenderer {
    public EmptyFeatherMothRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
