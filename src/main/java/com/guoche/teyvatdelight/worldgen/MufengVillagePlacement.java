package com.guoche.teyvatdelight.worldgen;

import com.guoche.teyvatdelight.crop.WildMufengMushroomBlock;
import com.guoche.teyvatdelight.registry.ModBlocks;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;

final class MufengVillagePlacement {
    static final TagKey<Structure> VILLAGES = TagKey.create(Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath("teyvatdelight", "mufeng_villages"));
    static final TagKey<Block> STRIPPED = WildMufengMushroomBlock.STRIPPED_WOOD;
    static final double CHANCE = 0.175;
    private static final Direction[] DIRECTIONS = {
            Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private MufengVillagePlacement() {}

    static long mix(long value) {
        value = (value ^ value >>> 30) * 0xbf58476d1ce4e5b9L;
        value = (value ^ value >>> 27) * 0x94d049bb133111ebL;
        return value ^ value >>> 31;
    }
    static boolean selected(long seed) { return (mix(seed) >>> 11) * 0x1.0p-53 < CHANCE; }
    static boolean reasonable(BoundingBox box) {
        return box.getXSpan() <= 128 && box.getZSpan() <= 128
                && box.getYSpan() >= 4 && box.getYSpan() <= 128;
    }
    static List<Long> footprint(BoundingBox box) {
        List<Long> result = new ArrayList<>();
        for (int x = (box.minX() - 1) >> 4; x <= (box.maxX() + 1) >> 4; x++)
            for (int z = (box.minZ() - 1) >> 4; z <= (box.maxZ() + 1) >> 4; z++)
                result.add(ChunkPos.asLong(x, z));
        return result;
    }
    static Map<Long, LevelChunk> loaded(ServerLevel level, List<Long> footprint) {
        Map<Long, LevelChunk> chunks = new HashMap<>();
        for (long key : footprint) {
            ChunkPos pos = new ChunkPos(key);
            LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
            if (chunk == null) return null;
            chunks.put(key, chunk);
        }
        return chunks;
    }
    static BlockState state(Map<Long, LevelChunk> chunks, BlockPos pos) {
        LevelChunk chunk = chunks.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        return chunk == null ? null : chunk.getBlockState(pos);
    }
    static boolean support(BlockState state) {
        return state.is(STRIPPED)
                || !state.is(BlockTags.LOGS) && WildMufengMushroomBlock.canAttachTo(state);
    }
    static boolean target(ServerLevel level, Map<Long, LevelChunk> chunks, BlockPos pos) {
        LevelChunk chunk = chunks.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        return chunk != null && level.isInWorldBounds(pos) && chunk.getBlockState(pos).isAir()
                && (chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).is(Biomes.PLAINS)
                    || chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).is(Biomes.MEADOW))
                && level.canSeeSky(pos);
    }

    // At most seven high support blocks per column; no interior/foundation volume scan.
    static void column(ServerLevel level, MufengVillageData.Piece piece,
            Map<Long, LevelChunk> chunks) {
        BoundingBox box = piece.box;
        int x = box.minX() + piece.cursor % box.getXSpan();
        int z = box.minZ() + piece.cursor / box.getXSpan();
        LevelChunk chunk = chunks.get(ChunkPos.asLong(x >> 4, z >> 4));
        int surface = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x & 15, z & 15);
        int top = Math.min(surface, box.maxY());
        int bottom = Math.max(Math.max(box.minY(), piece.groundY + 2), top - 6);
        BlockPos.MutableBlockPos support = new BlockPos.MutableBlockPos();
        for (int y = top; y >= bottom; y--) {
            support.set(x, y, z);
            BlockState base = chunk.getBlockState(support);
            boolean stripped = base.is(STRIPPED);
            if (!support(base)) continue;
            for (Direction direction : DIRECTIONS) {
                BlockPos pos = support.relative(direction);
                if (!target(level, chunks, pos)) continue;
                if (direction != Direction.UP) {
                    BlockState below = state(chunks, pos.below());
                    if (y < surface - 3 || below == null || !below.isAir()) continue;
                }
                int priority = direction == Direction.UP ? (stripped ? 0 : 1) : 2;
                long rank = mix(piece.seed ^ pos.asLong() ^ (direction.get3DDataValue() * 0x9e3779b97f4a7c15L));
                piece.keep(new MufengVillageData.Candidate(pos.asLong(),
                        direction.get3DDataValue(), priority, rank));
            }
        }
        piece.cursor++;
    }
    static List<MufengVillageData.Candidate> choose(ServerLevel level,
            MufengVillageData.Piece piece, Map<Long, LevelChunk> chunks) {
        List<MufengVillageData.Candidate> result = new ArrayList<>();
        for (var candidate : piece.candidates) {
            BlockPos pos = BlockPos.of(candidate.pos());
            Direction facing = Direction.from3DDataValue(candidate.facing());
            BlockState support = state(chunks, pos.relative(facing.getOpposite()));
            if (support == null || !support(support)
                    || !target(level, chunks, pos)) continue;
            BlockState crop = ModBlocks.WILD_MUFENG_MUSHROOM.get().defaultBlockState()
                    .setValue(WildMufengMushroomBlock.FACING, facing);
            if (!crop.canSurvive(level, pos)) continue;
            result.add(candidate);
            if (result.size() == piece.count()) break;
        }
        return result;
    }
    static void place(ServerLevel level, List<MufengVillageData.Candidate> candidates) {
        for (var candidate : candidates) {
            BlockState crop = ModBlocks.WILD_MUFENG_MUSHROOM.get().defaultBlockState()
                    .setValue(WildMufengMushroomBlock.FACING, Direction.from3DDataValue(candidate.facing()));
            level.setBlock(BlockPos.of(candidate.pos()), crop, Block.UPDATE_CLIENTS);
        }
    }
}
