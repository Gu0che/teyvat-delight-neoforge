package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WindWingsCurios;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

public class WindWingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "textures/entity/wind_wings.png");
    private final ElytraModel<AbstractClientPlayer> model;

    public WindWingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                          EntityModelSet models) {
        super(parent);
        this.model = new ElytraModel<>(models.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack wings = visibleWings(player);
        if (wings.isEmpty()) {
            return;
        }

        pose.pushPose();
        pose.translate(0.0F, 0.0F, 0.125F);
        getParentModel().copyPropertiesTo(model);
        model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertices = ItemRenderer.getArmorFoilBuffer(
                buffers, RenderType.armorCutoutNoCull(TEXTURE), wings.hasFoil());
        model.renderToBuffer(pose, vertices, light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }

    public static ItemStack visibleWings(AbstractClientPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(TeyvatDelight.WIND_WINGS.get())) {
            return chest;
        }
        if (!chest.is(Items.ELYTRA)
                && ModList.get().isLoaded("curios") && ModList.get().isLoaded("caelus")) {
            return WindWingsCurios.getBackWings(player);
        }
        return ItemStack.EMPTY;
    }
}
