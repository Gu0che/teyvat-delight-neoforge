package com.guoche.teyvatdelight.crop;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class TeyvatCropDropTracker {
    private static final Set<DropKey> SKIPPED_DROPS = ConcurrentHashMap.newKeySet();
    private static final Map<DropKey, BlockState> REMEMBERED_STATES = new ConcurrentHashMap<>();

    private TeyvatCropDropTracker() {
    }

    public static void skipNextDrop(Level level, BlockPos pos) {
        SKIPPED_DROPS.add(key(level, pos));
    }

    public static boolean consumeSkipDrop(Level level, BlockPos pos) {
        return SKIPPED_DROPS.remove(key(level, pos));
    }

    public static void rememberState(Level level, BlockPos pos, BlockState state) {
        REMEMBERED_STATES.put(key(level, pos), state);
    }

    public static Optional<BlockState> consumeRememberedState(Level level, BlockPos pos) {
        return Optional.ofNullable(REMEMBERED_STATES.remove(key(level, pos)));
    }

    private static DropKey key(Level level, BlockPos pos) {
        return new DropKey(level.dimension().location(), pos.asLong());
    }

    private record DropKey(ResourceLocation dimension, long pos) {
    }
}
