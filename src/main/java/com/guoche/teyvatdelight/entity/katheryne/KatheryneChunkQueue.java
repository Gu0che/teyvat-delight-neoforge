package com.guoche.teyvatdelight.entity.katheryne;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.level.ChunkPos;

// Only accessed on the server thread; world callbacks must run after takeBatch returns.
final class KatheryneChunkQueue {
    private final Set<ChunkPos> pending = new LinkedHashSet<>();

    void add(ChunkPos pos) {
        pending.add(pos);
    }

    List<ChunkPos> takeBatch(int limit) {
        if (limit <= 0 || pending.isEmpty()) return List.of();
        List<ChunkPos> batch = new ArrayList<>(Math.min(limit, pending.size()));
        var iterator = pending.iterator();
        while (iterator.hasNext() && batch.size() < limit) {
            batch.add(iterator.next());
            iterator.remove();
        }
        return batch;
    }
}
