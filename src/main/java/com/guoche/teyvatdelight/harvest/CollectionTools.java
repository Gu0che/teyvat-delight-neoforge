package com.guoche.teyvatdelight.harvest;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;

public final class CollectionTools {
    private CollectionTools() {}

    public static boolean isPickaxe(ItemStack tool) {
        return tool.is(ItemTags.PICKAXES) || tool.canPerformAction(ItemAbilities.PICKAXE_DIG);
    }

    public static boolean isShears(ItemStack tool) {
        return tool.canPerformAction(ItemAbilities.SHEARS_DIG);
    }

    public static boolean hasSilkTouch(Level level, ItemStack tool) {
        return tool.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH)) > 0;
    }
}
