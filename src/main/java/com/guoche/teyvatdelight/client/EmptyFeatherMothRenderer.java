package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.EmptyFeatherMothEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EmptyFeatherMothRenderer extends MobRenderer<EmptyFeatherMothEntity, EmptyFeatherMothModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "textures/entity/empty_feather_moth.png");

    public EmptyFeatherMothRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyFeatherMothModel(context.bakeLayer(EmptyFeatherMothModel.LAYER_LOCATION)), 0.15F);
    }

    @Override
    public ResourceLocation getTextureLocation(EmptyFeatherMothEntity entity) {
        return TEXTURE;
    }
}
