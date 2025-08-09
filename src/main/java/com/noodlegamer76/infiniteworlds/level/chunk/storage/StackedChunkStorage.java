package com.noodlegamer76.infiniteworlds.level.chunk.storage;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class StackedChunkStorage {
    private static final Map<StackedChunkPos, StackedChunk> chunks = new HashMap<>();

    public static Map<StackedChunkPos, StackedChunk> getChunks() {
        return chunks;
    }

    public static void clear() {
        chunks.clear();
    }

    public static void remove(StackedChunkPos pos) {
        chunks.remove(pos);
    }

    public static StackedChunk getOrCreate(Level level, StackedChunkPos pos) {
        return chunks.computeIfAbsent(pos, p -> new StackedChunk(level, p));
    }

    public static StackedChunk get(StackedChunkPos pos) {
        return chunks.get(pos);
    }

    public static boolean contains(StackedChunkPos pos) {
        return chunks.containsKey(pos);
    }

    public static void put(StackedChunk chunk) {
        chunks.put(chunk.getPos(), chunk);
    }
}
