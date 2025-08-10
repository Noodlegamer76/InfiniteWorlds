package com.noodlegamer76.infiniteworlds.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {

    @Accessor("progressListenerFactory")
    ChunkProgressListenerFactory getProgressListenerFactory();

    @Accessor("storageSource")
    LevelStorageSource.LevelStorageAccess getStorageSource();
}
