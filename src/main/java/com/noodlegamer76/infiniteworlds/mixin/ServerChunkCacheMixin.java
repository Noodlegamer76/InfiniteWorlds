package com.noodlegamer76.infiniteworlds.mixin;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Inject(
            method = "getFullChunk",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getStackedChunk(long chunkPos, Consumer<LevelChunk> fullChunkGetter, CallbackInfo ci) {

    }
}
