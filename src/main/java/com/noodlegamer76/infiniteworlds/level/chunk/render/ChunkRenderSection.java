package com.noodlegamer76.infiniteworlds.level.chunk.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

public class ChunkRenderSection {
    private final StackedChunkPos pos;
    private final SectionRenderDispatcher.RenderSection renderSection;
    private boolean removed = false;
    private double cachedDistance;

    public ChunkRenderSection(StackedChunkPos pos, SectionRenderDispatcher.RenderSection renderSection) {
        this.pos = pos;
        this.renderSection = renderSection;
    }

    public StackedChunkPos getPos() {
        return pos;
    }

    public SectionRenderDispatcher.RenderSection getRenderSection() {
        return renderSection;
    }

    public void rebuild(ClientLevel level) {
        RenderChunkContext.set(pos.y);
        try {
            RenderRegionCache cache = new RenderRegionCache();

            SectionRenderDispatcher dispatcher = StackedChunkRenderer.getDispatcher();
            dispatcher.rebuildSectionSync(renderSection, cache);
            dispatcher.uploadAllPendingUploads();
        } finally {
            RenderChunkContext.clear();
        }
    }

    public void close() {
        RenderSystem.recordRenderCall(renderSection::releaseBuffers);
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void updateDistance(Vec3 camPos) {
        cachedDistance = getPos().getWorldPosition()
                .getBottomCenter()
                .distanceToSqr(camPos);
    }

    public double getCachedDistance() {
        return cachedDistance;
    }

}
