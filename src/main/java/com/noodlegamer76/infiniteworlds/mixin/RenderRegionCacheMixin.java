package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.RenderChunkContext;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderRegionCache.class)
public class RenderRegionCacheMixin {

    @Redirect(
            method = "lambda$getChunkInfo$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;")
    )
    private static LevelChunk fixGetLevelChunk(Level level, int chunkX, int chunkZ) {
        Integer y = RenderChunkContext.get();
        if (y != null) {
            StackedChunkPos pos = new StackedChunkPos(chunkX, y, chunkZ);
            StackedChunk chunk = StackedChunkStorage.get(pos);
            if (chunk != null) {
                return chunk;
            }
        }

        return level.getChunk(chunkX, chunkZ);
    }
}
