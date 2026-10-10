package com.guoche.teyvatdelight.block;

import com.guoche.teyvatdelight.inventory.AdeptiSeekersStoveMenu;
import com.guoche.teyvatdelight.registry.ModBlockEntityTypes;
import com.guoche.teyvatdelight.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

/** All cooking, inventory, experience, data components and save data remain owned by Farmer's Delight. */
public final class AdeptiSeekersStoveBlockEntity extends CookingPotBlockEntity {
    public AdeptiSeekersStoveBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        // NeoForge calls this getter even during the base constructor's block-state validation.
        return ModBlockEntityTypes.ADEPTI_SEEKERS_STOVE.get();
    }

    @Override
    public ItemStack getAsItem() {
        ItemStack stack = new ItemStack(ModItems.ADEPTI_SEEKERS_STOVE.get());
        stack.applyComponents(collectComponents());
        return stack;
    }

    @Override
    public Component getName() {
        return hasCustomName() ? getCustomName() : Component.translatable("block.teyvatdelight.adepti_seekers_stove");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AdeptiSeekersStoveMenu(id, inventory, this, cookingPotData);
    }
}
