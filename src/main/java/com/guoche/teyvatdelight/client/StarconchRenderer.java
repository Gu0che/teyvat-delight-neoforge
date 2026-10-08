package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.StarconchEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StarconchRenderer extends MobRenderer<StarconchEntity, StarconchModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "textures/entity/starconch.png");

    public StarconchRenderer(EntityRendererProvider.Context context) {
        super(context, new StarconchModel(context.bakeLayer(StarconchModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(StarconchEntity entity) {
        return TEXTURE;
    }
}
