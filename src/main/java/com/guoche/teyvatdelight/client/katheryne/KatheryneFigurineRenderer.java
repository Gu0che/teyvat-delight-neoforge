package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.block.KatheryneFigurineBlock;
import com.guoche.teyvatdelight.block.KatheryneFigurineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public final class KatheryneFigurineRenderer implements BlockEntityRenderer<KatheryneFigurineBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "textures/entity/katheryne.png");
    private final KatheryneFigurineModel model;

    public KatheryneFigurineRenderer(BlockEntityRendererProvider.Context context) {
        model = new KatheryneFigurineModel(mode -> context.bakeLayer(KatheryneModel.LAYER_LOCATION));
    }

    @Override
    public void render(KatheryneFigurineBlockEntity figurine, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        model.render(figurine.getBlockState().getValue(KatheryneFigurineBlock.FACING),
                figurine.getBlockState().getValue(KatheryneFigurineBlock.POSE),
                pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay);
    }
}
