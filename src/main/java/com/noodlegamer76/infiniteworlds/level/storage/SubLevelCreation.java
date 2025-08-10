package com.noodlegamer76.infiniteworlds.level.storage;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.mixin.accessor.MinecraftServerAccessor;
import com.noodlegamer76.infiniteworlds.mixin.accessor.ServerLevelAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class SubLevelCreation {
    public StackedServerLevel createSubLevel(MinecraftServer server, ServerLevel parentLevel, int yOffset) throws IOException {
        ResourceKey<Level> parentDimensionKey = parentLevel.dimension();
        String subLevelFolderName = parentDimensionKey.location().getPath() + "_stacked_" + yOffset;
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, subLevelFolderName));

        LevelStorageSource.LevelStorageAccess subLevelStorage = ((MinecraftServerAccessor) server).getStorageSource().parent().createAccess(subLevelFolderName);

        ServerLevelData levelData = (ServerLevelData) parentLevel.getLevelData();

        RegistryAccess registryAccess = parentLevel.registryAccess();
        Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        LevelStem parentStem = levelStemRegistry.get(parentDimensionKey.location());

        ChunkProgressListener chunkListener = ((MinecraftServerAccessor) server).getProgressListenerFactory().create(server.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS));

        List<CustomSpawner> spawners = ((ServerLevelAccessor) parentLevel).getCustomSpawners();

        return new StackedServerLevel(
                server,
                server,
                subLevelStorage,
                levelData,
                dimensionKey,
                parentStem,
                chunkListener,
                parentLevel.isDebug(),
                parentLevel.getSeed(),
                spawners,
                ((ServerLevelAccessor) parentLevel).getTickTime(),
                null,
                yOffset,
                parentLevel
        );
    }

}
