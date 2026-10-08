package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.PlaceableTeaItem;
import com.guoche.teyvatdelight.PortableNutritionBagItem;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.item.KatheryneFigurineBlockItem;
import com.guoche.teyvatdelight.api.TeyvatTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public final class ItemDescriptionTooltips {


    private ItemDescriptionTooltips() {
    }

    @SubscribeEvent
    public static void addItemDescription(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(TeyvatDelight.PORTABLE_NUTRITION_BAG.get())) {
            PortableNutritionBagItem.appendRemainingTooltip(stack, event.getToolTip());
        }
        int insertIndex = 1;
        if (stack.getItem() instanceof PlaceableTeaItem) {
            event.getToolTip().add(insertIndex++, Component.translatable("tooltip.teyvatdelight.placeable_sneaking")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else if (stack.is(TeyvatTags.Items.TEYVAT_DISHES) && stack.getItem() instanceof BlockItem) {
            event.getToolTip().add(insertIndex++, Component.translatable("tooltip.teyvatdelight.placeable")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        String key = stack.getDescriptionId() + ".desc";
        if (!Language.getInstance().has(key)) {
            return;
        }
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(insertIndex, Component.translatable("tooltip.teyvatdelight.hold_shift_for_description").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        String desc = Language.getInstance().getOrDefault(key);
        for (String line : desc.split("\\n")) {
            var description = Component.literal(line).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
            if (stack.getItem() instanceof KatheryneFigurineBlockItem) {
                description.withStyle(ChatFormatting.STRIKETHROUGH);
            }
            event.getToolTip().add(insertIndex++, description);
        }
    }

}
