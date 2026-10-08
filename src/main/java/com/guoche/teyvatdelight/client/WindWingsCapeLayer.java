package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class WindWingsCapeLayer extends CapeLayer {
    public WindWingsCapeLayer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (WindWingsLayer.visibleWings(player).isEmpty()) {
            super.render(pose, buffers, light, player, limbSwing, limbSwingAmount, partialTick,
                    ageInTicks, netHeadYaw, headPitch);
        }
    }

    @SuppressWarnings("unchecked")
    public static void install(PlayerRenderer renderer) {
        try {
            Field layersField = null;
            for (Field field : LivingEntityRenderer.class.getDeclaredFields()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    layersField = field;
                    break;
                }
            }
            if (layersField == null) {
                throw new NoSuchFieldException("LivingEntityRenderer layer list");
            }
            layersField.setAccessible(true);
            List<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> layers =
                    (List<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>) layersField.get(renderer);
            for (int index = 0; index < layers.size(); index++) {
                if (layers.get(index).getClass() == CapeLayer.class) {
                    layers.set(index, new WindWingsCapeLayer(renderer));
                    return;
                }
            }
            TeyvatDelight.LOGGER.warn("Vanilla player cape layer was not found; Wind Wings may overlap capes");
        } catch (ReflectiveOperationException | RuntimeException exception) {
            TeyvatDelight.LOGGER.warn("Could not replace player cape layer for Wind Wings", exception);
        }
    }
}
