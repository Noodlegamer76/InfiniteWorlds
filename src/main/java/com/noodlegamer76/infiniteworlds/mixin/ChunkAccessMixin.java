package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
    @Shadow
    @Final
    protected ChunkPos chunkPos;

    @Inject(
            method = "isSectionEmpty",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isSectionEmpty(int y, CallbackInfoReturnable<Boolean> cir) {
        StackedChunkPos pos = new StackedChunkPos(chunkPos.x, y, chunkPos.z);
        StackedChunk chunk = StackedChunkStorage.get(pos);

        if (chunk != null) {
            cir.setReturnValue(chunk.isSectionEmpty(y));
        }
    }
}
