package com.noodlegamer76.infiniteworlds.level.chunk.util;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class FillChunk {

    public static void fillChunkWithDirt(LevelChunk chunk) {
        BlockState dirt = Blocks.DIRT.defaultBlockState();

        LevelChunkSection[] sections = chunk.getSections();
        for (LevelChunkSection section : sections) {
            if (section == null) continue;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (y == 0) {
                            section.setBlockState(x, y, z, Blocks.SANDSTONE_SLAB.defaultBlockState(), false);
                            continue;
                        }
                        if (Math.random() < 0.15) {
                            section.setBlockState(x, y, z, dirt, false);
                        }
                        else {
                            section.setBlockState(x, y, z, Blocks.AIR.defaultBlockState(), false);
                        }
                    }
                }
            }
        }

        chunk.setUnsaved(true);
    }
}
