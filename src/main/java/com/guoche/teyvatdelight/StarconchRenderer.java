package com.guoche.teyvatdelight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.StarconchRenderer} for new integrations. */
@Deprecated
public class StarconchRenderer extends com.guoche.teyvatdelight.client.StarconchRenderer {
    public StarconchRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
