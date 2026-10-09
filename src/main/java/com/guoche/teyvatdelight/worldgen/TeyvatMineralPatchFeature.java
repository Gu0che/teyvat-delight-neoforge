package com.guoche.teyvatdelight.worldgen;

import com.guoche.teyvatdelight.TeyvatMineralPatchConfiguration;
import com.guoche.teyvatdelight.mineral.NaturalMineralPlacement;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TeyvatMineralPatchFeature extends Feature<TeyvatMineralPatchConfiguration> {
    public TeyvatMineralPatchFeature(Codec<TeyvatMineralPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TeyvatMineralPatchConfiguration> context) {
        TeyvatMineralPatchConfiguration config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockState state = config.state();

        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 8, 32);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int y = origin.getY() + random.nextInt(config.ySpread() * 2 + 1) - config.ySpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            BlockPos pos = new BlockPos(x, y, z);

            if (level.ensureCanWrite(pos) && level.isEmptyBlock(pos)) {
                if (state.hasProperty(BlockStateProperties.FACING)) {
                    // 有朝向的方块（如晶化骨髓）：遍历 6 个方向，找到能存活的那一侧
                    for (Direction facing : Direction.values()) {
                        BlockState candidate = state.setValue(BlockStateProperties.FACING, facing);
                        if (NaturalMineralPlacement.canGenerateAt(candidate, level, pos)) {
                            level.setBlock(pos, candidate, 2);
                            placed++;
                            break;
                        }
                    }
                } else if (NaturalMineralPlacement.canGenerateAt(state, level, pos)) {
                    level.setBlock(pos, state, 2);
                    placed++;
                }
            }
        }

        return placed > 0;
    }
}
