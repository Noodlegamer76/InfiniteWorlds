package com.noodlegamer76.infiniteworlds.level.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class StackedServerLevel extends ServerLevel {
    private final DimensionType parentDimensionType;
    private final int yOffset;
    private final ServerLevel parentLevel;

    public StackedServerLevel(MinecraftServer server,
                              Executor dispatcher,
                              LevelStorageSource.LevelStorageAccess levelStorageAccess,
                              ServerLevelData serverLevelData,
                              ResourceKey<Level> dimension,
                              LevelStem levelStem,
                              ChunkProgressListener progressListener,
                              boolean isDebug,
                              long biomeZoomSeed,
                              List<CustomSpawner> customSpawners,
                              boolean tickTime,
                              @Nullable RandomSequences randomSequences,
                              int yOffset,
                              ServerLevel parentLevel) {
        super(server, dispatcher, levelStorageAccess, serverLevelData, dimension, levelStem, progressListener, isDebug, biomeZoomSeed, customSpawners, tickTime, randomSequences);
        this.parentDimensionType = parentLevel.dimensionType();
        this.yOffset = yOffset;
        this.parentLevel = parentLevel;
    }

    public int getYOffset() {
        return yOffset;
    }

    public DimensionType getParentDimensionType() {
        return parentDimensionType;
    }

    public ServerLevel getParentLevel() {
        return parentLevel;
    }

    public BlockState getBlockState(BlockPos pos) {
        ServerLevel currentLevel = ServerLevelStorage.getSubLevel(parentLevel, pos);
        if (currentLevel == null) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        if (currentLevel == this) {
            return super.getBlockState(pos);
        }
        else return currentLevel.getBlockState(pos);
    }
}
