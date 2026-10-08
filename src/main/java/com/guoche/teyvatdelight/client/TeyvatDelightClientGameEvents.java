package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.NutritionBagNetwork;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatItemData;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public class TeyvatDelightClientGameEvents {
    private static Screen nutritionBagClickScreen;
    private static final ResourceLocation HOLY_WATER_ADVANCEMENT = ResourceLocation.fromNamespaceAndPath(
            TeyvatDelight.MODID, "main/was_it_worth_it");
    private static Field advancementParentField;
    private static AdvancementWidget disconnectedHolyWaterWidget;
    private static boolean advancementParentFieldUnavailable;

    @SubscribeEvent
    public static void absorbNutritionBagFood(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != 1) return;
        nutritionBagClickScreen = null;
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
        ItemStack carried = screen.getMenu().getCarried();
        Slot slot = screen.getSlotUnderMouse();
        if (!carried.is(TeyvatDelight.PORTABLE_NUTRITION_BAG.get()) || slot == null || !slot.hasItem()
                || Minecraft.getInstance().player == null
                || !slot.mayPickup(Minecraft.getInstance().player) || !slot.mayPlace(slot.getItem())) return;
        ItemStack food = slot.getItem();
        if (food.getItem().getFoodProperties(food, Minecraft.getInstance().player) == null) return;
        event.setCanceled(true);
        nutritionBagClickScreen = screen;
        NutritionBagNetwork.sendAbsorb(screen.getMenu().containerId, screen.getMenu().slots.indexOf(slot));
    }

    @SubscribeEvent
    public static void finishNutritionBagClick(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getButton() == 1 && event.getScreen() == nutritionBagClickScreen) {
            event.setCanceled(true);
            nutritionBagClickScreen = null;
        }
    }

    @SubscribeEvent
    public static void hideHolyWaterConnection(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof AdvancementsScreen screen) || advancementParentFieldUnavailable) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        var node = minecraft.getConnection().getAdvancements().getTree().get(HOLY_WATER_ADVANCEMENT);
        if (node == null) return;
        AdvancementWidget widget = screen.getAdvancementWidget(node);
        if (widget == null || widget == disconnectedHolyWaterWidget) return;

        try {
            if (advancementParentField == null) {
                for (Field field : AdvancementWidget.class.getDeclaredFields()) {
                    if (field.getType() == AdvancementWidget.class) {
                        field.setAccessible(true);
                        advancementParentField = field;
                        break;
                    }
                }
            }
            if (advancementParentField == null) throw new NoSuchFieldException("AdvancementWidget parent");
            advancementParentField.set(widget, null);
            disconnectedHolyWaterWidget = widget;
        } catch (Exception exception) {
            advancementParentFieldUnavailable = true;
            TeyvatDelight.LOGGER.warn("Could not hide the Holy Water advancement connection", exception);
        }
    }

    private static final ResourceLocation[] STAR_BACKGROUNDS = new ResourceLocation[]{
            null,
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "textures/slot/star_background_1.png"),
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "textures/slot/star_background_2.png"),
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "textures/slot/star_background_3.png"),
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "textures/slot/star_background_4.png"),
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "textures/slot/star_background_5.png")
    };

    @SubscribeEvent
    public static void renderStarBackgrounds(ContainerScreenEvent.Render.Background event) {
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();

        for (Slot slot : screen.getMenu().slots) {
            if (!slot.isActive() || !slot.hasItem()) {
                continue;
            }

            ItemStack stack = slot.getItem();
            ResourceLocation background = getStarBackground(TeyvatItemData.getDisplayStars(stack));
            if (background != null) {
                event.getGuiGraphics().blit(
                        background,
                        left + slot.x,
                        top + slot.y,
                        0.0F,
                        0.0F,
                        16,
                        16,
                        16,
                        16
                );
            }
        }
    }

    private static ResourceLocation getStarBackground(int stars) {
        return stars >= TeyvatItemData.MIN_STARS && stars <= TeyvatItemData.MAX_STARS
                ? STAR_BACKGROUNDS[stars]
                : null;
    }
}
