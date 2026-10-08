package com.guoche.teyvatdelight;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.WindWingsLayer} for new integrations. */
@Deprecated
public class WindWingsLayer extends com.guoche.teyvatdelight.client.WindWingsLayer {
    public WindWingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                              EntityModelSet models) {
        super(parent, models);
    }
}
