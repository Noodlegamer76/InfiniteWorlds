package com.noodlegamer76.infiniteworlds.level.chunk.util;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndexManager;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;

public class ChunkUtils {

    //gets a levelchunk in client from main or a levelchunk from the server in the layer index
    @Nullable
    public static LevelChunk getLayerChunkOrClientChunk(StackedChunkPos pos, @Nullable Level level) {
        if (level instanceof ServerLevel serverLevel) {
            LayerIndexManager manager = LayerIndexStorage.getLayerIndexManager(serverLevel);
            LayerIndex index = manager.savedData.getAdjustedLayer(pos, serverLevel);
            if (manager.layerLevel != null && index != null) {
                LevelChunk chunk = manager.layerLevel.getChunkSource().getChunkNow(index.layerPos().x, index.layerPos().z);
                if (chunk != null) {
                    return chunk;
                }
            }
        }

        return StackedChunkStorage.get(pos);
    }
}
