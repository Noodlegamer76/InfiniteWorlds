package com.noodlegamer76.infiniteworlds.level.dimension;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;

public record LayerIndex(StackedChunkPos mainPos, StackedChunkPos layerPos, int sectionHeight) {
}
