package com.guoche.teyvatdelight.entity.katheryne;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class KatheryneEquipment {
    private KatheryneEquipment() {
    }

    public static ItemStack roll(ServerPlayer player, Item item) {
        ItemStack stack = new ItemStack(item);
        var settings = KatheryneShopConfig.settings().equipment();
        Registry<Enchantment> registry = player.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> eligible = new ArrayList<>(registry.holders()
                .filter(holder -> holder.value().isSupportedItem(stack))
                .filter(holder -> settings.allowCurses() || !holder.is(EnchantmentTags.CURSE))
                .map(holder -> (Holder<Enchantment>) holder).toList());
        RandomSource random = player.getRandom();
        List<Holder<Enchantment>> selected = new ArrayList<>();
        double firstChance = settings.firstChance();
        double chanceStep = settings.chanceStep();
        while (!eligible.isEmpty()) {
            double chance = firstChance - selected.size() * chanceStep;
            if (chance <= 0 || random.nextDouble() >= chance) break;

            List<Holder<Enchantment>> compatible = eligible.stream()
                    .filter(enchantment -> EnchantmentHelper.isEnchantmentCompatible(selected, enchantment))
                    .toList();
            if (compatible.isEmpty()) break;
            Holder<Enchantment> chosen = compatible.get(random.nextInt(compatible.size()));
            selected.add(chosen);
            eligible.remove(chosen);
        }
        for (Holder<Enchantment> enchantment : selected) {
            int min = enchantment.value().getMinLevel();
            int max = enchantment.value().getMaxLevel();
            stack.enchant(enchantment, min + random.nextInt(max - min + 1));
        }
        return stack;
    }
}
