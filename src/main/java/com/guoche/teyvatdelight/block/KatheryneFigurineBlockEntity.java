package com.guoche.teyvatdelight.block;

import com.guoche.teyvatdelight.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Static decoration: no ticker, inventory, NPC state, or network payload. */
public class KatheryneFigurineBlockEntity extends BlockEntity {
    public KatheryneFigurineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KATHERYNE_FIGURINE.get(), pos, state);
    }
}
