package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import com.noodlegamer76.infiniteworlds.level.chunk.util.ChunkUtils;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndexManager;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import com.noodlegamer76.infiniteworlds.mixin.accessor.BlockCollisionsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(BlockCollisions.class)
public abstract class BlockCollisionsMixin {

    @Shadow @Final private Cursor3D cursor;

    @Shadow @Nullable protected abstract BlockGetter getChunk(int x, int z);

    @Shadow @Final private BlockPos.MutableBlockPos pos;

    @Redirect(
            method = "computeNext",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockCollisions;getChunk(II)Lnet/minecraft/world/level/BlockGetter;")
    )
    public BlockGetter getStackedChunkForCollision(BlockCollisions instance, int x, int z) {
        int sectionX = SectionPos.blockToSectionCoord(x);
        int sectionY = SectionPos.blockToSectionCoord(cursor.nextY());
        int sectionZ = SectionPos.blockToSectionCoord(z);
        StackedChunkPos chunkPos = new StackedChunkPos(sectionX, sectionY, sectionZ);

        Level level = ((BlockCollisionsAccessor) this).getCollisionGetter() instanceof Level collisionGetter ? collisionGetter : null;
        LevelChunk chunk = ChunkUtils.getLayerChunkOrClientChunk(chunkPos, level);

        if (chunk != null) {
            return chunk;
        }
        return getChunk(x, z);
    }
}
