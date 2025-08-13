package com.noodlegamer76.infiniteworlds.level.dimension.storage;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class LayerIndexSavedData extends SavedData {
    private static final String DATA_NAME = "infiniteworlds_layer_index";

    private final Map<StackedChunkPos, LayerIndex> layerMap = new HashMap<>();

    public LayerIndexSavedData() {
        super();
    }

    public LayerIndexSavedData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        loadFromTag(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        saveToTag(compoundTag);
        return compoundTag;
    }

    public static final Factory<LayerIndexSavedData> FACTORY = new Factory<>(
            LayerIndexSavedData::new,
            LayerIndexSavedData::new
    );

    public static LayerIndexSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private void loadFromTag(CompoundTag tag) {
        layerMap.clear();
        if (tag.contains("layers")) {
            CompoundTag layersTag = tag.getCompound("layers");
            for (String key : layersTag.getAllKeys()) {
                CompoundTag layerTag = layersTag.getCompound(key);
                StackedChunkPos mainPos = new StackedChunkPos(layerTag.getInt("x1"), layerTag.getInt("y1"), layerTag.getInt("z1"));
                StackedChunkPos layerPos = new StackedChunkPos(layerTag.getInt("x2"), layerTag.getInt("y2"), layerTag.getInt("z2"));
                int height = layerTag.getInt("sectionHeight");
                LayerIndex layerIndex = new LayerIndex(mainPos, layerPos, height);
                layerMap.put(StackedChunkPos.fromString(key), layerIndex);
            }
        }
    }

    private void saveToTag(CompoundTag tag) {
        CompoundTag layersTag = new CompoundTag();
        for (Map.Entry<StackedChunkPos, LayerIndex> entry : layerMap.entrySet()) {
            LayerIndex layerIndex = entry.getValue();
            CompoundTag layerTag = new CompoundTag();
            layerTag.putInt("x1", layerIndex.mainPos().x);
            layerTag.putInt("y1", layerIndex.mainPos().y);
            layerTag.putInt("z1", layerIndex.mainPos().z);
            layerTag.putInt("x2", layerIndex.layerPos().x);
            layerTag.putInt("y2", layerIndex.layerPos().y);
            layerTag.putInt("z2", layerIndex.layerPos().z);
            layerTag.putInt("sectionHeight", layerIndex.sectionHeight());

            layersTag.put(entry.getKey().toString(), layerTag);
        }
        tag.put("layers", layersTag);
    }

    public void putLayer(StackedChunkPos key, LayerIndex index) {
        layerMap.put(key, index);
        setDirty();
    }

    //Clamps the key to a multiple of the world height with an offset to match minimum build height
    @Nullable
    public LayerIndex getAdjustedLayer(StackedChunkPos key, ServerLevel levelForHeight) {
        int sectionsPerLevel = levelForHeight.getSectionsCount();
        int minSectionY = levelForHeight.getMinSection();

        int sectionY = key.y;

        int relativeY = sectionY - minSectionY;
        int baseLayerSectionY = minSectionY + (Math.floorDiv(relativeY, sectionsPerLevel)) * sectionsPerLevel;

        StackedChunkPos adjusted = new StackedChunkPos(key.x, baseLayerSectionY, key.z);
        return layerMap.get(adjusted);
    }
}
