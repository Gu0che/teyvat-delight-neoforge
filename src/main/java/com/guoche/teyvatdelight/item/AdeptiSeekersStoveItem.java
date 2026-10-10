package com.guoche.teyvatdelight.item;

import java.util.Optional;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import vectorwing.farmersdelight.common.item.CookingPotItem;

public final class AdeptiSeekersStoveItem extends CookingPotItem {
    public AdeptiSeekersStoveItem(Block block, Properties properties) {
        super(block, properties);
    }

    private static ItemStack tooltipStack(ItemStack stack) {
        // FD's static meal helpers check its own item ID; adapt only the temporary tooltip stack.
        ItemStack pot = new ItemStack(vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get());
        pot.applyComponents(stack.getComponents());
        return pot;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(tooltipStack(stack));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return super.getBarWidth(tooltipStack(stack));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return super.getTooltipImage(tooltipStack(stack));
    }
}
