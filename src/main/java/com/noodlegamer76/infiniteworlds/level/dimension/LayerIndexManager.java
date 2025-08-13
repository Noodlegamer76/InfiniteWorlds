package com.noodlegamer76.infiniteworlds.level.dimension;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.util.ChunkPosIterator;
import com.noodlegamer76.infiniteworlds.level.dimension.loading.LayerTicketManager;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexManagerSavedData;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexSavedData;
import com.noodlegamer76.infiniteworlds.level.util.LayerUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;

public class LayerIndexManager {
    public final ServerLevel mainLevel;
    public final ServerLevel layerLevel;
    public final LayerIndexSavedData savedData;
    public final ChunkPosIterator iterator;
    public final LayerTicketManager ticketManager;


    public LayerIndexManager(ServerLevel mainLevel) {
        this.mainLevel = mainLevel;
        ResourceKey<Level> layerLevelKey = LayerUtils.getLevelKey(mainLevel, 1);
        this.layerLevel = mainLevel.getServer().getLevel(layerLevelKey);
        this.savedData = LayerIndexSavedData.get(mainLevel);
        this.iterator = LayerIndexManagerSavedData.get(mainLevel).getIterator();
        this.ticketManager = new LayerTicketManager(mainLevel);
    }

    @Nullable
    public LevelChunk getLoadedChunkAt(StackedChunkPos pos, ServerLevel baseLevel) {
        LayerIndex index = savedData.getLayer(pos);
        if (index == null) {
            index = createNewLayerIndex(pos, baseLevel.dimensionType().height());
        }

        StackedChunkPos layerPos = index.layerPos();

        return layerLevel.getChunkSource().getChunkNow(layerPos.x, layerPos.z);
    }


    public LayerIndex createNewLayerIndex(StackedChunkPos stackedPos, int sectionHeight) {
        int sectionsPerLevel = mainLevel.getSectionsCount();
        int minSectionY = mainLevel.getMinSection();

        int sectionY = stackedPos.y;

        int relativeY = sectionY - minSectionY;
        int baseLayerSectionY = minSectionY + (Math.floorDiv(relativeY, sectionsPerLevel)) * sectionsPerLevel;

        StackedChunkPos baseLayerPos = new StackedChunkPos(stackedPos.x, baseLayerSectionY, stackedPos.z);

        ChunkPos nextPos = iterator.next();
        StackedChunkPos layerPos = new StackedChunkPos(nextPos.x, 0, nextPos.z);
        LayerIndex index = new LayerIndex(baseLayerPos, layerPos, sectionHeight);
        savedData.putLayer(baseLayerPos, index);
        LayerIndexManagerSavedData.get(mainLevel).markDirty();
        return index;
    }

}
