package com.noodlegamer76.infiniteworlds.level.chunk.render;

import com.noodlegamer76.infiniteworlds.Config;
import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import com.noodlegamer76.infiniteworlds.mixin.accessor.LevelRendererAccessor;
import com.noodlegamer76.infiniteworlds.mixin.accessor.SectionRenderDispatcherAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StackedChunkRenderer {
    private static Camera camera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    private static final Map<StackedChunkPos, ChunkRenderSection> stackedSections = new ConcurrentHashMap<>();
    private static final PriorityQueue<ChunkRenderSection> dirtySections =
            new PriorityQueue<>(Comparator.comparingDouble(ChunkRenderSection::getCachedDistance));

    private static final List<ChunkRenderSection> removedSections = Collections.synchronizedList(new ArrayList<>());
    private static final Queue<StackedChunkPos> unbuilt = new ConcurrentLinkedQueue<>();

    private static SectionRenderDispatcher dispatcher;
    private static ClientLevel level;
    private static Vec3 lastCameraPos = Vec3.ZERO;

    public static void init(ClientLevel level) {
        if (dispatcher == null || ((SectionRenderDispatcherAccessor) dispatcher).isClosed()) {
            LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
            dispatcher = ((LevelRendererAccessor) renderer).getSectionRenderDispatcher();
            StackedChunkRenderer.level = level;
        }
    }

    public static void clear() {
        synchronized (removedSections) {
            for (ChunkRenderSection section : stackedSections.values()) {
                section.setRemoved(true);
                removedSections.add(section);
                dirtySections.remove(section);
            }
        }
        stackedSections.clear();
        removeAllRemovedChunks();
    }

    public static ChunkRenderSection getChunkRenderSection(SectionPos pos) {
        return stackedSections.get(new StackedChunkPos(pos.x(), pos.y(), pos.z()));
    }

    public static void removeStackedChunk(StackedChunkPos pos) {
        ChunkRenderSection section = stackedSections.remove(pos);
        if (section != null) {
            section.setRemoved(true);
            synchronized (removedSections) {
                removedSections.add(section);
            }
        }
    }

    public static void markDirty(StackedChunkPos pos) {
        ChunkRenderSection section = stackedSections.get(pos);
        if (section != null && !dirtySections.contains(section)) {
            dirtySections.add(section);
        }
    }

    public static void processChunks() {
        int yRenderDistance = Config.VERTICAL_RENDER_DISTANCE.get();
        int renderDistance = (int) Minecraft.getInstance().levelRenderer.getLastViewDistance();

        Vec3 camPos = camera().getPosition();
        int camChunkX = (int) Math.floor(camPos.x / 16.0);
        int camChunkY = (int) Math.floor(camPos.y / 16.0);
        int camChunkZ = (int) Math.floor(camPos.z / 16.0);

        for (StackedChunkPos pos : new ArrayList<>(stackedSections.keySet())) {
            int dx = Math.abs(pos.x - camChunkX);
            int dy = Math.abs(pos.y - camChunkY);
            int dz = Math.abs(pos.z - camChunkZ);

            double horizontalDist = Math.sqrt(dx * dx + dz * dz);

            if (horizontalDist > renderDistance || dy > yRenderDistance) {
                StackedChunkStorage.remove(pos);
                removeStackedChunk(pos);
            }
        }

        removeAllRemovedChunks();
        buildUnbuiltSections();

        if (!camPos.equals(lastCameraPos)) {
            lastCameraPos = camPos;

            List<ChunkRenderSection> tempList;
            synchronized (dirtySections) {
                tempList = new ArrayList<>(dirtySections);
            }
            for (ChunkRenderSection section : tempList) {
                section.updateDistance(camPos);
            }
            synchronized (dirtySections) {
                dirtySections.clear();
                dirtySections.addAll(tempList);
            }
        }

        int rebuildBudget = Config.MAX_CHUNKS_RENDER_PER_TICK.get();
        while (rebuildBudget-- > 0) {
            boolean rebuilt = rebuildNextDirtyChunk();
            if (!rebuilt) break;
        }
    }

    public static boolean rebuildNextDirtyChunk() {
        ChunkRenderSection section;
        synchronized (dirtySections) {
            section = dirtySections.poll();
        }
        if (section == null) return false;

        if (section.isRemoved()) {
            InfiniteWorlds.LOGGER.error("Trying to rebuild removed chunk: {}, Removed chunks shouldn't be in the queue", section.getPos());
            return true;
        }
        if (!stackedSections.containsValue(section)) {
            stackedSections.remove(section.getPos());
            section.close();
            return true;
        }

        section.rebuild(level);
        return true;
    }

    public static void buildUnbuiltSections() {
        if (dispatcher == null) return;
        StackedChunkPos pos;
        while ((pos = unbuilt.poll()) != null) {
            int originX = pos.x * 16;
            int originY = pos.y * 16;
            int originZ = pos.z * 16;

            SectionRenderDispatcher.RenderSection renderSection = dispatcher.new RenderSection(0, originX, originY, originZ);
            renderSection.setOrigin(originX, originY, originZ);

            ChunkRenderSection chunkRenderSection = new ChunkRenderSection(pos, renderSection);
            stackedSections.put(pos, chunkRenderSection);

            markDirty(pos);
        }
    }

    public static void removeAllRemovedChunks() {
        List<ChunkRenderSection> sectionsToRemove;
        synchronized (removedSections) {
            sectionsToRemove = new ArrayList<>(removedSections);
            removedSections.clear();
        }

        for (ChunkRenderSection section : sectionsToRemove) {
            dirtySections.remove(section);
            stackedSections.remove(section.getPos());
            section.close();
        }
    }

    public static void addStackedChunk(StackedChunkPos pos) {
        if (!unbuilt.contains(pos)) {
            unbuilt.add(pos);
        }
    }

    public static Map<StackedChunkPos, ChunkRenderSection> getStackedSections() {
        return stackedSections;
    }

    public static PriorityQueue<ChunkRenderSection> getDirtySections() {
        return dirtySections;
    }

    public static List<ChunkRenderSection> getRemovedSections() {
        return removedSections;
    }

    public static SectionRenderDispatcher getDispatcher() {
        return dispatcher;
    }

    public static Queue<StackedChunkPos> getUnbuilt() {
        return unbuilt;
    }
}
