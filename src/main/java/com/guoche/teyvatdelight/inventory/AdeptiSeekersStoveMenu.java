package com.guoche.teyvatdelight.inventory;

import com.guoche.teyvatdelight.block.AdeptiSeekersStoveBlockEntity;
import com.guoche.teyvatdelight.registry.ModBlocks;
import com.guoche.teyvatdelight.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

/** Keeps the original slots and recipe book, but validates our block instead of the original pot. */
public final class AdeptiSeekersStoveMenu extends CookingPotMenu {
    public AdeptiSeekersStoveMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, readBlockEntity(inventory, buffer), new SimpleContainerData(2));
    }

    public AdeptiSeekersStoveMenu(int id, Inventory inventory, AdeptiSeekersStoveBlockEntity stove, ContainerData data) {
        super(id, inventory, stove, data);
    }

    private static AdeptiSeekersStoveBlockEntity readBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        if (inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof AdeptiSeekersStoveBlockEntity stove) {
            return stove;
        }
        throw new IllegalStateException("Missing Adepti Seeker's Stove block entity");
    }

    @Override
    public MenuType<?> getType() {
        return ModMenuTypes.ADEPTI_SEEKERS_STOVE.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
                ModBlocks.ADEPTI_SEEKERS_STOVE.get());
    }
}
