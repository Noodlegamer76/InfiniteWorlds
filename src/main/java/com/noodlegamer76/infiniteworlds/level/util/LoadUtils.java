package com.noodlegamer76.infiniteworlds.level.util;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndexManager;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;

public class LoadUtils {

    public static <T> void addTicketToStackedChunk(
            TicketType<T> type,
            StackedChunkPos basePos,
            T value,
            boolean forceTicks,
            ServerLevel baseLevel) {

        LayerIndexManager manager = LayerIndexStorage.getLayerIndexManager(baseLevel);
        ServerLevel layerLevel = manager.layerLevel;
        ServerChunkCache cache = layerLevel.getChunkSource();
        LayerIndex layerIndex = manager.savedData.getLayer(basePos);

        if (layerIndex == null) {
            return;
        }

        cache.addRegionTicket(type, layerIndex.layerPos(), 0, value, forceTicks);
    }

    public static <T> void removeTicketFromStackedChunk(
            TicketType<T> type,
            StackedChunkPos basePos,
            T value,
            boolean forceTicks,
            ServerLevel baseLevel) {

        LayerIndexManager manager = LayerIndexStorage.getLayerIndexManager(baseLevel);
        ServerLevel layerLevel = manager.layerLevel;
        ServerChunkCache cache = layerLevel.getChunkSource();
        LayerIndex layerIndex = manager.savedData.getLayer(basePos);

        if (layerIndex == null) {
            return;
        }

        cache.removeRegionTicket(type, layerIndex.layerPos(), 0, value, forceTicks);
    }
}
