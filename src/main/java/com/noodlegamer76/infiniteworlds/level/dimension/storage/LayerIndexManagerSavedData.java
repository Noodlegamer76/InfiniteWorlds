package com.noodlegamer76.infiniteworlds.level.dimension.storage;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.util.ChunkPosIterator;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;

public class LayerIndexManagerSavedData extends SavedData {
    private static final String DATA_NAME = "infiniteworlds_chunk_manager_data";

    private ChunkPosIterator iterator;

    public LayerIndexManagerSavedData() {
        super();
        iterator = new ChunkPosIterator(0, 0, 1500000);
        markDirty();
    }

    public LayerIndexManagerSavedData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        loadFromTag(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        saveToTag(compoundTag);
        return compoundTag;
    }

    public static final Factory<LayerIndexManagerSavedData> FACTORY = new Factory<>(
            LayerIndexManagerSavedData::new,
            LayerIndexManagerSavedData::new
    );

    public static LayerIndexManagerSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private void loadFromTag(CompoundTag tag) {
        if (tag.contains("iterator")) {
            CompoundTag iteratorTag = tag.getCompound("iterator");
            int currentX = iteratorTag.getInt("currentX");
            int currentZ = iteratorTag.getInt("currentZ");
            int gridSize = iteratorTag.getInt("gridSize");
            iterator = new ChunkPosIterator(currentX, currentZ, gridSize);
        } else {
            iterator = new ChunkPosIterator(0, 0, 1500000);
        }
    }

    private void saveToTag(CompoundTag tag) {
        CompoundTag iteratorTag = new CompoundTag();
        iteratorTag.putInt("currentX", iterator.getCurrentX());
        iteratorTag.putInt("currentZ", iterator.getCurrentZ());
        iteratorTag.putInt("gridSize", iterator.getGridSize());
        tag.put("iterator", iteratorTag);
    }

    public void markDirty() {
        setDirty();
    }

    @Nullable
    public ChunkPosIterator getIterator() {
        return iterator;
    }
}
