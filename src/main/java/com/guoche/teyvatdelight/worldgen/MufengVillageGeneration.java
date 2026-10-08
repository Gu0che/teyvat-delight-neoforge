package com.guoche.teyvatdelight.worldgen;

import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class MufengVillageGeneration {
    private static final Map<ServerLevel, Work> WORLDS = new WeakHashMap<>();
    private static final int COLUMNS_PER_TICK = 256;
    private static final int JOBS_PER_TICK = 16;
    private static final long TIME_BUDGET_NANOS = 1_000_000L;

    private MufengVillageGeneration() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(MufengVillageGeneration::loaded);
        NeoForge.EVENT_BUS.addListener(MufengVillageGeneration::tick);
        NeoForge.EVENT_BUS.addListener(MufengVillageGeneration::stopped);
    }

    private static void loaded(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)) return;
        boolean fresh = event.isNewChunk();
        level.getServer().execute(() -> {
            Work work = WORLDS.computeIfAbsent(level, Work::new);
            if (fresh) {
                var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
                // Local starts only: structure-manager reference resolution can load other chunks.
                for (var entry : chunk.getAllStarts().entrySet()) {
                    if (registry.wrapAsHolder(entry.getKey()).is(MufengVillagePlacement.VILLAGES))
                        work.plan(registry.getKey(entry.getKey()).toString(), entry.getValue());
                }
            }
            work.loadedChunks.add(chunk.getPos().toLong());
        });
    }

    private static void tick(ServerTickEvent.Post event) {
        for (var entry : WORLDS.entrySet()) {
            if (entry.getKey().getServer() == event.getServer()) entry.getValue().run();
        }
    }
    private static void stopped(ServerStoppedEvent event) {
        WORLDS.keySet().removeIf(level -> level.getServer() == event.getServer());
    }

    static final class Work {
        final ServerLevel level;
        final MufengVillageData data;
        final Set<String> ready = new LinkedHashSet<>();
        final Set<Long> loadedChunks = new LinkedHashSet<>();
        final Map<Long, Set<String>> waiting = new HashMap<>();
        final Map<String, List<Long>> footprints = new HashMap<>();
        final Map<String, Set<Long>> missingByJob = new HashMap<>();

        Work(ServerLevel level) {
            this(level, MufengVillageData.get(level));
        }

        Work(ServerLevel level, MufengVillageData data) {
            this.level = level;
            this.data = data;
            for (var piece : data.pending.values()) {
                footprints.put(piece.id, MufengVillagePlacement.footprint(piece.box));
                ready.add(piece.id);
            }
        }

        void plan(String structureId, StructureStart start) {
            if (!start.isValid()) return;
            String village = structureId + ":" + start.getChunkPos().toLong();
            if (!data.planned.add(village)) return;
            List<net.minecraft.world.level.levelgen.structure.StructurePiece> pieces = start.getPieces();
            for (int i = 0; i < pieces.size(); i++) {
                if (!(pieces.get(i) instanceof PoolElementStructurePiece piece)
                        || !MufengVillagePlacement.reasonable(piece.getBoundingBox())) continue;
                var box = piece.getBoundingBox();
                long seed = MufengVillagePlacement.mix(level.getSeed() ^ start.getChunkPos().toLong()
                        ^ ((long) structureId.hashCode() << 32) ^ level.dimension().location().hashCode()
                        ^ net.minecraft.core.BlockPos.asLong(box.minX(), box.minY(), box.minZ()) ^ (i * 0x9e3779b97f4a7c15L));
                if (!MufengVillagePlacement.selected(seed)) continue;
                String id = village + ":" + i;
                var job = new MufengVillageData.Piece(id, box,
                        piece.getPosition().getY() + piece.getGroundLevelDelta(), seed);
                data.pending.put(id, job);
                footprints.put(id, MufengVillagePlacement.footprint(box));
                ready.add(id);
            }
            data.setDirty();
        }

        private void park(String id) {
            clearWait(id);
            Set<Long> missing = new HashSet<>();
            for (long key : footprints.get(id)) {
                ChunkPos pos = new ChunkPos(key);
                if (level.getChunkSource().getChunkNow(pos.x, pos.z) == null) {
                    missing.add(key);
                    waiting.computeIfAbsent(key, unused -> new HashSet<>()).add(id);
                }
            }
            if (missing.isEmpty()) ready.add(id);
            else missingByJob.put(id, missing);
        }
        private void clearWait(String id) {
            Set<Long> missing = missingByJob.remove(id);
            if (missing == null) return;
            for (long key : missing) {
                Set<String> ids = waiting.get(key);
                if (ids != null) {
                    ids.remove(id);
                    if (ids.isEmpty()) waiting.remove(key);
                }
            }
        }
        private void finish(String id) {
            data.pending.remove(id); footprints.remove(id); clearWait(id); data.setDirty();
        }

        void run() {
            if (ready.isEmpty() && loadedChunks.isEmpty()) return;
            for (long key : loadedChunks) {
                Set<String> ids = waiting.remove(key);
                if (ids == null) continue;
                for (String id : ids) {
                    Set<Long> missing = missingByJob.get(id);
                    if (missing != null && missing.remove(key) && missing.isEmpty()) {
                        missingByJob.remove(id); ready.add(id);
                    }
                }
            }
            loadedChunks.clear();
            long deadline = System.nanoTime() + TIME_BUDGET_NANOS;
            int columns = 0, jobs = 0;
            while (!ready.isEmpty() && columns < COLUMNS_PER_TICK
                    && jobs++ < JOBS_PER_TICK && System.nanoTime() < deadline) {
                String id = ready.iterator().next(); ready.remove(id);
                var piece = data.pending.get(id);
                if (piece == null) continue;
                var chunks = MufengVillagePlacement.loaded(level, footprints.get(id));
                if (chunks == null) { park(id); continue; }
                int before = piece.cursor;
                while (piece.cursor < piece.columns() && columns < COLUMNS_PER_TICK
                        && System.nanoTime() < deadline) {
                    MufengVillagePlacement.column(level, piece, chunks);
                    columns++;
                }
                if (before != piece.cursor) data.setDirty();
                if (piece.cursor == piece.columns()) {
                    var chosen = MufengVillagePlacement.choose(level, piece, chunks);
                    finish(id);
                    MufengVillagePlacement.place(level, chosen);
                    TeyvatDelight.LOGGER.debug("Mufeng village piece {}: {} columns, {} candidates, {} placed",
                            id, piece.columns(), piece.candidates.size(), chosen.size());
                } else ready.add(id);
            }
        }
    }

}
