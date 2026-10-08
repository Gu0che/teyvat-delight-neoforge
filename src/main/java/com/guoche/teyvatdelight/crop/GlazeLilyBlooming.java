package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class GlazeLilyBlooming {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    private GlazeLilyBlooming() {
    }

    public static boolean shouldBloom(Level level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= 13000L && dayTime < 23000L;
    }

    public static boolean isGlazeLily(BlockState state) {
        return state.is(TeyvatDelight.GLAZE_LILY_CROP.get()) || state.is(TeyvatDelight.WILD_GLAZE_LILY.get());
    }

    public static boolean updateIfNeeded(ServerLevel level, BlockPos pos, BlockState state, boolean nightBlooming) {
        if (!state.hasProperty(BLOOMING)) {
            return false;
        }

        boolean shouldBloom = nightBlooming;
        if (state.is(TeyvatDelight.GLAZE_LILY_CROP.get())) {
            shouldBloom = shouldBloom && state.getValue(CropBlock.AGE) >= TeyvatDelight.GLAZE_LILY_CROP.get().getMaxAge();
        }

        if (state.getValue(BLOOMING) == shouldBloom) {
            return false;
        }

        level.setBlock(pos, state.setValue(BLOOMING, shouldBloom), 3);
        return true;
    }
}
