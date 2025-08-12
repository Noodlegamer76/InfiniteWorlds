package com.noodlegamer76.infiniteworlds.level.dimension;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;

public class LayerIndex {
    public final StackedChunkPos mainPos;
    public final StackedChunkPos layerPos;
    public final int sectionHeight;

    public LayerIndex(StackedChunkPos mainPos, StackedChunkPos layerPos, int sectionHeight) {
        this.mainPos = mainPos;
        this.layerPos = layerPos;
        this.sectionHeight = sectionHeight;
    }
}
