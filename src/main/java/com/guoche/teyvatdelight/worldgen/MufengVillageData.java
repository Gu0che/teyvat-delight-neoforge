package com.guoche.teyvatdelight.worldgen;

import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;

final class MufengVillageData extends SavedData {
    final Set<String> planned = new HashSet<>();
    final Map<String, Piece> pending = new LinkedHashMap<>();

    static MufengVillageData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(MufengVillageData::new, (tag, registries) -> load(tag)),
                "teyvatdelight_mufeng_villages");
    }

    static MufengVillageData load(CompoundTag tag) {
        MufengVillageData data = new MufengVillageData();
        for (Tag entry : tag.getList("Villages", Tag.TAG_STRING)) data.planned.add(entry.getAsString());
        for (Tag entry : tag.getList("Pending", Tag.TAG_COMPOUND)) {
            CompoundTag value = (CompoundTag) entry;
            int[] b = value.getIntArray("Box");
            if (b.length != 6) continue;
            Piece piece = new Piece(value.getString("Id"),
                    new BoundingBox(b[0], b[1], b[2], b[3], b[4], b[5]),
                    value.getInt("Ground"), value.getLong("Seed"));
            if (!MufengVillagePlacement.reasonable(piece.box)) continue;
            piece.cursor = Math.max(0, Math.min(value.getInt("Cursor"), piece.columns()));
            for (Tag candidate : value.getList("Candidates", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) candidate;
                int direction = c.getInt("Facing");
                if (direction < 0 || direction > 5 || direction == 0) continue;
                piece.keep(new Candidate(c.getLong("Pos"), direction, c.getInt("Priority"), c.getLong("Rank")));
            }
            data.pending.put(piece.id, piece);
        }
        return data;
    }

    CompoundTag write(CompoundTag tag) {
        ListTag villages = new ListTag();
        planned.stream().sorted().forEach(id -> villages.add(StringTag.valueOf(id)));
        tag.put("Villages", villages);
        ListTag jobs = new ListTag();
        for (Piece piece : pending.values()) {
            CompoundTag value = new CompoundTag();
            value.putString("Id", piece.id);
            BoundingBox b = piece.box;
            value.putIntArray("Box", new int[]{b.minX(), b.minY(), b.minZ(), b.maxX(), b.maxY(), b.maxZ()});
            value.putInt("Ground", piece.groundY);
            value.putLong("Seed", piece.seed);
            value.putInt("Cursor", piece.cursor);
            ListTag candidates = new ListTag();
            for (Candidate c : piece.candidates) {
                CompoundTag entry = new CompoundTag();
                entry.putLong("Pos", c.pos); entry.putInt("Facing", c.facing);
                entry.putInt("Priority", c.priority); entry.putLong("Rank", c.rank);
                candidates.add(entry);
            }
            value.put("Candidates", candidates);
            jobs.add(value);
        }
        tag.put("Pending", jobs);
        return tag;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) { return write(tag); }

    record Candidate(long pos, int facing, int priority, long rank) {}

    static final class Piece {
        final String id;
        final BoundingBox box;
        final int groundY;
        final long seed;
        int cursor;
        final List<Candidate> candidates = new ArrayList<>();

        Piece(String id, BoundingBox box, int groundY, long seed) {
            this.id = id; this.box = box; this.groundY = groundY; this.seed = seed;
        }
        int columns() { return box.getXSpan() * box.getZSpan(); }
        int count() { return 1 + (int) (MufengVillagePlacement.mix(seed + 1) & 1); }
        void keep(Candidate value) {
            if (candidates.stream().anyMatch(c -> c.pos == value.pos)) return;
            candidates.add(value);
            candidates.sort((a, b) -> {
                int priority = Integer.compare(a.priority, b.priority);
                return priority != 0 ? priority : Long.compareUnsigned(a.rank, b.rank);
            });
            if (candidates.size() > 8) candidates.remove(8);
        }
    }
}
