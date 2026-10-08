package com.guoche.teyvatdelight.item;

import com.guoche.teyvatdelight.api.TeyvatItemData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public final class KatheryneFigurineBlockItem extends BlockItem {
    public static final int DEFAULT_STARS = 5;

    public KatheryneFigurineBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(
                TeyvatItemData.getStarColor(TeyvatItemData.getStars(stack, DEFAULT_STARS)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        TeyvatItemData.appendRarityTooltip(stack, tooltip, DEFAULT_STARS);
    }

}

