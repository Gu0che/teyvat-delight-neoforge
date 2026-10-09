package com.guoche.teyvatdelight.mineral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class NaturalMineralPlacement {
    private NaturalMineralPlacement() {}

    public static boolean hasFullSupport(BlockGetter level, BlockPos pos, Direction face) {
        BlockState support = level.getBlockState(pos);
        return Block.isShapeFullBlock(support.getCollisionShape(level, pos))
                && support.isFaceSturdy(level, pos, face);
    }

    // Placement permits decorative bases; world generation retains the original geology.
    public static boolean canGenerateAt(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getBlock() instanceof NaturalShipoBlock block) return block.canGenerateAt(state, level, pos);
        if (state.getBlock() instanceof NaturalNoctilucousJadeBlock block) return block.canGenerateAt(state, level, pos);
        if (state.getBlock() instanceof NaturalJinghuagusuiBlock block) return block.canGenerateAt(state, level, pos);
        return state.canSurvive(level, pos);
    }
}
