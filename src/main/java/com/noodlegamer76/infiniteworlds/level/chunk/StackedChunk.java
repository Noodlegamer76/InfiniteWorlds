package com.noodlegamer76.infiniteworlds.level.chunk;

import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class StackedChunk extends LevelChunk {
    private StackedChunkPos pos;
    private static final int HEIGHT = 16;

    public StackedChunk(Level level, StackedChunkPos pos) {
        super(level, pos);
        this.pos = pos;
    }

    public void setPos(StackedChunkPos pos) {
        this.pos = pos;
    }

    @Override
    public LevelChunkSection getSection(int index) {
        return super.getSection(0);
    }

    @Override
    public StackedChunkPos getPos() {
        return pos;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public int getHeight(Heightmap.Types type, int x, int z) {
        return HEIGHT;
    }

    @Override
    public int getMinBuildHeight() {
        return pos.y * 16;
    }

    @Override
    public int getMaxBuildHeight() {
        return pos.y * 16 + HEIGHT;
    }

    @Override
    public boolean isSectionEmpty(int y) {
        return this.getSection(0).hasOnlyAir();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return super.getBlockState(pos);
    }

    @Override
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        StackedChunkPos chunkPos = new StackedChunkPos(pos);
        if (getLevel() instanceof ServerLevel serverLevel) {
            LevelChunk layerChunk = LayerIndexStorage.getLayerIndexManager(serverLevel).getChunkAt(chunkPos, serverLevel);
            if (layerChunk == null) {
                return Blocks.VOID_AIR.defaultBlockState();
            }
            return layerChunk.setBlockState(pos, state, isMoving);
        }
        StackedChunkRenderer.markDirty(this.pos);
        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;
        LevelChunkSection section = this.getSection(0);
        return section.setBlockState(x, y, z, state, isMoving);
    }
}
