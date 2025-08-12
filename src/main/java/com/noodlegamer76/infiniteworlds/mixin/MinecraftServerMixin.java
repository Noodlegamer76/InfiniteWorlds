package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.util.CreateLayer;
import com.noodlegamer76.infiniteworlds.level.util.LevelCreationVariables;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow @Final private Executor executor;

    @Shadow @Final protected LevelStorageSource.LevelStorageAccess storageSource;

    @Inject(method = "createLevels", at = @At("TAIL"))
    private void create(ChunkProgressListener listener, CallbackInfo ci) {
        LevelCreationVariables.setLevels(levels);
        LevelCreationVariables.setExecutor(executor);
        LevelCreationVariables.setStorageSource(storageSource);
        LevelCreationVariables.setListener(listener);

        ArrayList<ServerLevel> layers = new ArrayList<>();
        for (ServerLevel parent: levels.values()) {
            ServerLevel level = CreateLayer.createLayer(parent, 1);
            layers.add(level);
        }

        for (ServerLevel level: layers) {
            levels.put(level.dimension(), level);
        }
    }
}
