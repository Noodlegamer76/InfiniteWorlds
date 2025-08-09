package com.noodlegamer76.infiniteworlds.level.chunk;

import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import net.minecraft.core.BlockPos;
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
        return getSection(0).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }


    @Override
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        if (getLevel().isClientSide) {
            StackedChunkRenderer.markDirty(this.pos);
        }
        return getSection(0).setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
    }
}
