package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.KatheryneEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KatheryneRenderer extends MobRenderer<KatheryneEntity, KatheryneModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "textures/entity/katheryne.png");

    public KatheryneRenderer(EntityRendererProvider.Context context) {
        super(context, new KatheryneModel(context.bakeLayer(KatheryneModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(KatheryneEntity entity) {
        return TEXTURE;
    }
}
