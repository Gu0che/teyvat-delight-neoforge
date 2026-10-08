package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.client.katheryne.KatheryneModel;
import com.guoche.teyvatdelight.client.katheryne.KatheryneFigurineRenderer;
import com.guoche.teyvatdelight.client.katheryne.KatheryneFigurineItemRenderer;
import com.guoche.teyvatdelight.registry.ModBlockEntityTypes;
import com.guoche.teyvatdelight.registry.ModItems;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.guoche.teyvatdelight.client.katheryne.KatheryneRenderer;
import com.guoche.teyvatdelight.client.katheryne.KatheryneScreen;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public class TeyvatDelightClient {
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFFFF;
                    }

                    return level != null && pos != null
                            ? BiomeColors.getAverageGrassColor(level, pos)
                            : GrassColor.getDefaultColor();
                },
                TeyvatDelight.XUAN_CI_JADE_FIELD.get(),
                TeyvatDelight.NI_CI_ZHI_FIELD.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> {
                    if (tintIndex != 1) {
                        return 0xFFFFFFFF;
                    }

                    return level != null && pos != null
                            ? BiomeColors.getAverageWaterColor(level, pos)
                            : DEFAULT_WATER_COLOR;
                },
                TeyvatDelight.CHU_CI_ZHU_FIELD.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> tintIndex == 1 ? DEFAULT_WATER_COLOR : 0xFFFFFFFF,
                TeyvatDelight.CHU_CI_ZHU_FIELD_ITEM.get()
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.KATHERYNE_FIGURINE.get(), KatheryneFigurineRenderer::new);
        event.registerEntityRenderer(TeyvatDelight.STARCONCH.get(), StarconchRenderer::new);
        event.registerEntityRenderer(TeyvatDelight.EMPTY_FEATHER_MOTH.get(), EmptyFeatherMothRenderer::new);
        event.registerEntityRenderer(TeyvatDelight.KATHERYNE.get(), KatheryneRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(StarconchModel.LAYER_LOCATION, StarconchModel::createBodyLayer);
        event.registerLayerDefinition(EmptyFeatherMothModel.LAYER_LOCATION, EmptyFeatherMothModel::createBodyLayer);
        event.registerLayerDefinition(KatheryneModel.LAYER_LOCATION, KatheryneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(KatheryneFigurineItemRenderer.extensions(), ModItems.KATHERYNE_FIGURINE_ITEM.get());
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TeyvatDelight.KATHERYNE_MENU.get(), KatheryneScreen::new);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                WindWingsCapeLayer.install(renderer);
                renderer.addLayer(new WindWingsLayer(renderer, event.getEntityModels()));
            }
        }
    }
}
