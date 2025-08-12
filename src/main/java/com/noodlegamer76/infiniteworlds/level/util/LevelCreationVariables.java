package com.noodlegamer76.infiniteworlds.level.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.Map;
import java.util.concurrent.Executor;

public class LevelCreationVariables {
    private static Map<ResourceKey<Level>, ServerLevel> levels;
    private static Executor executor;
    private static LevelStorageSource.LevelStorageAccess storageSource;
    private static ChunkProgressListener listener;

    public static void setExecutor(Executor executor) {
        LevelCreationVariables.executor = executor;
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static void setLevels(Map<ResourceKey<Level>, ServerLevel> levels) {
        LevelCreationVariables.levels = levels;
    }

    public static Map<ResourceKey<Level>, ServerLevel> getLevels() {
        return levels;
    }

    public static void setStorageSource(LevelStorageSource.LevelStorageAccess storageSource) {
        LevelCreationVariables.storageSource = storageSource;
    }

    public static LevelStorageSource.LevelStorageAccess getStorageSource() {
        return storageSource;
    }

    public static void setListener(ChunkProgressListener listener) {
        LevelCreationVariables.listener = listener;
    }

    public static ChunkProgressListener getListener() {
        return listener;
    }
}
