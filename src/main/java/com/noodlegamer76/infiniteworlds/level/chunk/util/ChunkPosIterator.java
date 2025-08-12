package com.noodlegamer76.infiniteworlds.level.chunk.util;

import net.minecraft.world.level.ChunkPos;

public class ChunkPosIterator {
    private int currentX;
    private int currentZ;
    private final int gridSize;

    public ChunkPosIterator(int startX, int startZ, int gridSize) {
        this.currentX = startX;
        this.currentZ = startZ;
        this.gridSize = gridSize;
    }

    public ChunkPos next() {
        ChunkPos pos = new ChunkPos(currentX, currentZ);
        currentX++;
        if (currentX >= gridSize) {
            currentX = 0;
            currentZ++;
        }
        return pos;
    }

    public int getCurrentZ() {
        return currentZ;
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getGridSize() {
        return gridSize;
    }
}