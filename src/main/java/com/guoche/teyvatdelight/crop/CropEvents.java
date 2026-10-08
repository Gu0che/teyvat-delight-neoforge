package com.guoche.teyvatdelight.crop;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class CropEvents {
    private static final int GLAZE_LILY_SYNC_INTERVAL = 20;
    private static final int GLAZE_LILY_SYNC_CHUNK_RADIUS = 6;

    private CropEvents() {
    }

    public static void harvestTeyvatCropBeforeOtherRightClickHandlers(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof TeyvatCropBlock crop) || !crop.isMaxAge(state)) {
            return;
        }

        Player player = event.getEntity();
        crop.harvestAndReplant(level, event.getPos(), state, player);
        event.setUseBlock(TriState.FALSE);
        event.setUseItem(TriState.FALSE);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        event.setCanceled(true);
    }

    public static void syncGlazeLilies(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (level.getGameTime() % GLAZE_LILY_SYNC_INTERVAL != 0) {
            return;
        }

        boolean nightBlooming = GlazeLilyBlooming.shouldBloom(level);
        Set<Long> checkedChunks = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            ChunkPos center = player.chunkPosition();
            for (int chunkX = center.x - GLAZE_LILY_SYNC_CHUNK_RADIUS; chunkX <= center.x + GLAZE_LILY_SYNC_CHUNK_RADIUS; chunkX++) {
                for (int chunkZ = center.z - GLAZE_LILY_SYNC_CHUNK_RADIUS; chunkZ <= center.z + GLAZE_LILY_SYNC_CHUNK_RADIUS; chunkZ++) {
                    long chunkKey = ChunkPos.asLong(chunkX, chunkZ);
                    if (checkedChunks.add(chunkKey)) {
                        syncGlazeLiliesInChunk(level, chunkX, chunkZ, nightBlooming);
                    }
                }
            }
        }
    }

    private static void syncGlazeLiliesInChunk(ServerLevel level, int chunkX, int chunkZ, boolean nightBlooming) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        chunk.findBlocks(
                GlazeLilyBlooming::isGlazeLily,
                (pos, state) -> GlazeLilyBlooming.updateIfNeeded(level, pos, state, nightBlooming)
        );
    }

}
