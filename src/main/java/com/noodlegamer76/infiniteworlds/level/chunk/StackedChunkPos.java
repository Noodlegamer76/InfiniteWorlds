package com.noodlegamer76.infiniteworlds.level.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public class StackedChunkPos extends ChunkPos {
    public final int y;

    public StackedChunkPos(int x, int y, int z) {
        super(x, z);
        this.y = y;
    }

    public StackedChunkPos(BlockPos pos) {
        super(pos);
        this.y = SectionPos.blockToSectionCoord(pos.getY());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StackedChunkPos otherPos) {
            return super.equals(other) && this.y == otherPos.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    public static StackedChunkPos fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid StackedChunkPos string: " + str);
        }
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            return new StackedChunkPos(x, y, z);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numbers in StackedChunkPos string: " + str, e);
        }
    }
}
