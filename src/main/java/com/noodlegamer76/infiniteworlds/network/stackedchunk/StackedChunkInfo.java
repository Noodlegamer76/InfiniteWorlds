package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public record StackedChunkInfo(LevelChunk chunk, StackedChunkPos pos, int offset) {
}
