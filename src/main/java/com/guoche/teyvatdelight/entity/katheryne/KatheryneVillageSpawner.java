package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneEntity;
import com.guoche.teyvatdelight.KatheryneVillageData;
import com.guoche.teyvatdelight.TeyvatDelight;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class KatheryneVillageSpawner {
    private static final int CHECKS_PER_LEVEL_PER_TICK = 64;
    private static final Map<ServerLevel, KatheryneChunkQueue> PENDING = new WeakHashMap<>();

    private KatheryneVillageSpawner() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(KatheryneVillageSpawner::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(KatheryneVillageSpawner::onServerTick);
        NeoForge.EVENT_BUS.addListener(KatheryneVillageSpawner::onServerStopped);
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        if (event.isNewChunk() && event.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = event.getChunk().getPos();
            level.getServer().execute(() -> PENDING.computeIfAbsent(level, unused -> new KatheryneChunkQueue()).add(pos));
        }
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            KatheryneChunkQueue positions = PENDING.get(level);
            if (positions == null) continue;
            // Structure lookups may load chunks and re-enter onChunkLoad. Drain before calling them.
            for (ChunkPos pos : positions.takeBatch(CHECKS_PER_LEVEL_PER_TICK)) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
                if (chunk == null) {
                    positions.add(pos);
                    continue;
                }
                findLibraries(level, pos);
            }
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        PENDING.keySet().removeIf(level -> level.getServer() == event.getServer());
    }

    private static void findLibraries(ServerLevel level, ChunkPos chunk) {
        KatheryneVillageData data = KatheryneVillageData.get(level.getServer());
        for (StructureStart start : level.structureManager().startsForStructure(chunk, structure -> {
            ResourceLocation id = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
            return id != null && id.getPath().contains("village");
        })) {
            if (!start.isValid()) continue;
            for (StructurePiece piece : start.getPieces()) {
                if (!(piece instanceof PoolElementStructurePiece poolPiece)
                        || !poolPiece.getElement().toString().toLowerCase(Locale.ROOT).contains("library")) continue;
                BoundingBox box = piece.getBoundingBox();
                if (!box.intersects(chunk.getMinBlockX(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), chunk.getMaxBlockZ())) continue;
                String key = level.dimension().location() + ":" + start.getChunkPos().toLong()
                        + ":" + box.minX() + ":" + box.minY() + ":"
                        + box.minZ() + ":" + box.maxX() + ":" + box.maxY() + ":" + box.maxZ();
                if (data.hasSpawned(key)) continue;
                BlockPos location = findLecternAndStandingPlace(level, box);
                if (location != null) {
                    if (!level.getEntitiesOfClass(KatheryneEntity.class,
                            new net.minecraft.world.phys.AABB(box.minX(), box.minY(), box.minZ(),
                                    box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1)).isEmpty()) {
                        data.markSpawned(key);
                        continue;
                    }
                    KatheryneEntity entity = TeyvatDelight.KATHERYNE.get().create(level);
                    if (entity == null) continue;
                    entity.moveTo(location.getX() + 0.5D, location.getY(), location.getZ() + 0.5D,
                            level.random.nextFloat() * 360.0F, 0.0F);
                    entity.finalizeSpawn(level, level.getCurrentDifficultyAt(location), MobSpawnType.STRUCTURE, null);
                    entity.setPersistenceRequired();
                    if (level.addFreshEntity(entity)) data.markSpawned(key);
                }
            }
        }
    }

    private static BlockPos findLecternAndStandingPlace(ServerLevel level, BoundingBox box) {
        int minX = box.minX();
        int maxX = box.maxX();
        int minZ = box.minZ();
        int maxZ = box.maxZ();
        for (int y = box.minY(); y <= box.maxY(); y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (level.getChunkSource().getChunkNow(x >> 4, z >> 4) == null) continue;
                    BlockPos lectern = new BlockPos(x, y, z);
                    if (!level.getBlockState(lectern).is(Blocks.LECTERN)) continue;
                    Direction facing = level.getBlockState(lectern).getValue(LecternBlock.FACING);
                    BlockPos preferred = lectern.relative(facing.getOpposite());
                    if (canStand(level, box, preferred)) return preferred;
                    for (int radius = 1; radius <= 3; radius++) {
                        for (int dx = -radius; dx <= radius; dx++) {
                            for (int dz = -radius; dz <= radius; dz++) {
                                BlockPos candidate = lectern.offset(dx, 0, dz);
                                if (canStand(level, box, candidate)) return candidate;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean canStand(ServerLevel level, BoundingBox box, BlockPos pos) {
        if (level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null) return false;
        if (!box.isInside(pos) || !box.isInside(pos.above())) return false;
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) return false;
        if (!level.getFluidState(pos).isEmpty()) return false;
        BlockPos floor = pos.below();
        if (!level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP)
                || !level.noCollision(new net.minecraft.world.phys.AABB(
                        pos.getX() + 0.175D, pos.getY(), pos.getZ() + 0.175D,
                        pos.getX() + 0.825D, pos.getY() + 1.9D, pos.getZ() + 0.825D))) return false;
        for (int y = pos.getY() + 2; y <= box.maxY(); y++) {
            if (!level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).isAir()) return true;
        }
        return false;
    }
}
