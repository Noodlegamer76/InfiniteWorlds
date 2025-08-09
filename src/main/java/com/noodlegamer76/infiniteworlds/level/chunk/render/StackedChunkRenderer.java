package com.noodlegamer76.infiniteworlds.level.chunk.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.mixin.accessor.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class StackedChunkRenderer {
    private static final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    private static final Map<StackedChunkPos, ChunkRenderSection> stackedSections = new HashMap<>();
    private static final PriorityQueue<ChunkRenderSection> dirtySections =
            new PriorityQueue<>(Comparator.comparingDouble(ChunkRenderSection::getCachedDistance));
    private static final List<ChunkRenderSection> removedSections = new ArrayList<>();

    private static SectionRenderDispatcher dispatcher;
    private static ClientLevel level;
    private static Vec3 lastCameraPos = Vec3.ZERO;

    public static void init(ClientLevel level) {
        if (dispatcher == null) {
            LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
            dispatcher = ((LevelRendererAccessor) renderer).getSectionRenderDispatcher();
            StackedChunkRenderer.level = level;
        }
    }

    public static void addRenderSection(ChunkRenderSection section) {
        stackedSections.put(section.getPos(), section);
        markDirty(section.getPos());
    }

    public static void clear() {
        for (ChunkRenderSection section : stackedSections.values()) {
            section.setRemoved(true);
            removedSections.add(section);
            dirtySections.remove(section);
        }
    }

    public static void removeStackedChunk(StackedChunkPos pos) {
        ChunkRenderSection section = stackedSections.remove(pos);
        if (section != null) {
            section.setRemoved(true);
            removedSections.add(section);
        }
    }

    public static void markDirty(StackedChunkPos pos) {
        ChunkRenderSection section = stackedSections.get(pos);
        if (section != null && !dirtySections.contains(section)) {
            dirtySections.add(section);
        }
    }

    public static void processChunks() {
        StackedChunkRenderer.removeAllRemovedChunks();

        Vec3 camPos = camera.getPosition();
        if (!camPos.equals(lastCameraPos)) {
            lastCameraPos = camPos;

            List<ChunkRenderSection> tempList = new ArrayList<>(dirtySections);
            for (ChunkRenderSection section : tempList) {
                section.updateDistance(camPos);
            }
            dirtySections.clear();
            dirtySections.addAll(tempList);

        }

        StackedChunkRenderer.rebuildNextDirtyChunk();
    }

    public static void rebuildNextDirtyChunk() {
        ChunkRenderSection section = dirtySections.poll();
        if (section != null) {
            if (section.isRemoved()) {
                InfiniteWorlds.LOGGER.error("Trying to rebuild removed chunk: {}, Removed chunks shouldn't be in the queue", section.getPos());
                return;
            }
            if (!stackedSections.containsValue(section)) {
                stackedSections.remove(section.getPos());
                section.close();
                return;
            }
            section.rebuild(level);
        }
    }

    public static void removeAllRemovedChunks() {
        List<ChunkRenderSection> sectionsToRemove = new ArrayList<>(removedSections);
        removedSections.clear();

        for (ChunkRenderSection section : sectionsToRemove) {
            dirtySections.remove(section);
            stackedSections.remove(section.getPos());
            section.close();
        }
    }

    public static void addStackedChunk(StackedChunk chunk) {
        StackedChunkPos pos = chunk.getPos();

        int originX = pos.x * 16;
        int originY = pos.y * 16;
        int originZ = pos.z * 16;

        SectionRenderDispatcher.RenderSection renderSection = dispatcher.new RenderSection(0, originX, originY, originZ);
        renderSection.setOrigin(originX, originY, originZ);

        ChunkRenderSection chunkRenderSection = new ChunkRenderSection(pos, renderSection);
        stackedSections.put(pos, chunkRenderSection);

        markDirty(pos);
    }

    public static Map<StackedChunkPos, ChunkRenderSection> getStackedSections() {
        return stackedSections;
    }

    public static Queue<ChunkRenderSection> getDirtySections() {
        return dirtySections;
    }

    public static SectionRenderDispatcher getDispatcher() {
        return dispatcher;
    }
}
