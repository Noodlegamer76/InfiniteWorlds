package com.noodlegamer76.infiniteworlds.level.dimension.loading;

import com.noodlegamer76.infiniteworlds.Config;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class LayerRenderDistanceManager {
    private final ServerLevel baseLevel;

    private final Deque<StackedChunkPos> chunkSendQueue = new ArrayDeque<>();
    private final Set<StackedChunkPos> queuedSet = new HashSet<>();

    private final Set<StackedChunkPos> sentChunksCache = new HashSet<>();

    private static final int SECTIONS_PER_TOLL = Math.max(1, Config.MAX_CHUNKS_RENDER_PER_TICK.get());

    public LayerRenderDistanceManager(ServerLevel baseLevel) {
        this.baseLevel = baseLevel;
    }

    public void updateQueuedChunksForPlayer(ServerPlayer player, int horizontalRenderDistanceChunks, int verticalRenderDistanceChunks) {
        LayerTicketManager ticketManager = LayerIndexStorage.getLayerIndexManager(baseLevel).ticketManager;
        Set<LayerIndex> loadedLayers = ticketManager.getActiveTickets();

        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        double horizontalDistanceBlocks = horizontalRenderDistanceChunks * 16.0;
        double verticalDistanceBlocks = verticalRenderDistanceChunks * 16.0;
        double horizontalDistanceSq = horizontalDistanceBlocks * horizontalDistanceBlocks;

        List<StackedChunkPos> filteredChunks = new ArrayList<>();
        for (LayerIndex layerIndex : loadedLayers) {
            StackedChunkPos basePos = layerIndex.mainPos();
            int sectionsCount = Math.max(1, layerIndex.sectionHeight() / 16);

            for (int i = 0; i < sectionsCount; i++) {
                StackedChunkPos pos = new StackedChunkPos(basePos.x, basePos.y + i, basePos.z);

                if (sentChunksCache.contains(pos)) continue;

                double dx = px - pos.x * 16.0;
                double dy = py - pos.y * 16.0;
                double dz = pz - pos.z * 16.0;

                if (Math.abs(dy) > verticalDistanceBlocks) continue;
                double hSq = dx * dx + dz * dz;
                if (hSq > horizontalDistanceSq) continue;

                if (queuedSet.add(pos)) {
                    filteredChunks.add(pos);
                }
            }
        }

        chunkSendQueue.addAll(filteredChunks);

        List<StackedChunkPos> sortedQueue = new ArrayList<>(chunkSendQueue);
        sortedQueue.sort(Comparator.comparingDouble(pos -> {
            double dx = px - pos.x * 16.0;
            double dy = py - pos.y * 16.0;
            double dz = pz - pos.z * 16.0;
            return dx * dx + dy * dy + dz * dz;
        }));

        chunkSendQueue.clear();
        chunkSendQueue.addAll(sortedQueue);
    }


    public List<StackedChunkPos> pollChunksToSend() {
        List<StackedChunkPos> toSend = new ArrayList<>(SECTIONS_PER_TOLL);
        int taken = 0;
        while (taken < SECTIONS_PER_TOLL && !chunkSendQueue.isEmpty()) {
            StackedChunkPos pos = chunkSendQueue.pollFirst();
            queuedSet.remove(pos);
            toSend.add(pos);
            taken++;
        }
        return toSend;
    }

    public void markSent(StackedChunkPos pos) {
        sentChunksCache.add(pos);
    }

    public void requeueIfNeeded(StackedChunkPos pos) {
        if (!sentChunksCache.contains(pos) && queuedSet.add(pos)) {
            chunkSendQueue.addLast(pos);
        }
    }

    public void clearCacheForChunksOutsideRange(ServerPlayer player, int horizontalRenderDistanceChunks, int verticalRenderDistanceChunks) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        double horizontalDistanceBlocks = horizontalRenderDistanceChunks * 16.0;
        double verticalDistanceBlocks = verticalRenderDistanceChunks * 16.0;
        double horizontalDistanceSq = horizontalDistanceBlocks * horizontalDistanceBlocks;

        sentChunksCache.removeIf(pos -> {
            double dx = px - pos.x * 16.0;
            double dy = py - pos.y * 16.0;
            double dz = pz - pos.z * 16.0;
            if (Math.abs(dy) > verticalDistanceBlocks) return true;
            double hSq = dx * dx + dz * dz;
            return hSq > horizontalDistanceSq;
        });

        Iterator<StackedChunkPos> it = chunkSendQueue.iterator();
        while (it.hasNext()) {
            StackedChunkPos pos = it.next();
            double dx = px - pos.x * 16.0;
            double dy = py - pos.y * 16.0;
            double dz = pz - pos.z * 16.0;
            if (Math.abs(dy) > verticalDistanceBlocks || (dx * dx + dz * dz) > horizontalDistanceSq) {
                it.remove();
                queuedSet.remove(pos);
            }
        }
    }
}
