package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.ChunkRenderSection;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ViewArea.class)
public class ViewAreaMixin {

    @Inject(
            method = "setDirty",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setStackedChunkDirty(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread, CallbackInfo ci) {
        StackedChunkPos chunkPos = new StackedChunkPos(sectionX, sectionY, sectionZ);
        ChunkRenderSection section = StackedChunkRenderer.getStackedSections().get(chunkPos);

        if (section != null && section.getRenderSection() != null) {
            section.getRenderSection().setDirty(reRenderOnMainThread);
        }
    }
}
