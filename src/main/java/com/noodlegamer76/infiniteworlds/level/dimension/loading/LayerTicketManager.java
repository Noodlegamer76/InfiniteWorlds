package com.noodlegamer76.infiniteworlds.level.dimension.loading;

import com.noodlegamer76.infiniteworlds.Config;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndex;
import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndexManager;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import com.noodlegamer76.infiniteworlds.level.util.LoadUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class LayerTicketManager {
    private final ServerLevel baseLevel;
    private final Set<LayerIndex> activeTickets = new HashSet<>();
    private final Map<LayerIndex, Boolean> keepChunks = new HashMap<>();

    private final Map<LayerIndex, TicketEntry> ticketsToAdd = new HashMap<>();
    private final Map<LayerIndex, TicketEntry> ticketsToRemove = new HashMap<>();

    private final Deque<StackedChunkPos> indicesToCreate = new ArrayDeque<>();
    private final Set<StackedChunkPos> indicesToCreateSet = new HashSet<>();

    private static final int MAX_TICKETS_PER_TICK = Math.max(1, Config.MAX_CHUNKS_PER_TICK.get());
    private static final int CREATE_INDICES_PER_TICK = 100;

    public LayerTicketManager(ServerLevel baseLevel) {
        this.baseLevel = baseLevel;
    }

    public void updatePlayerTickets() {
        for (LayerIndex index : activeTickets) {
            keepChunks.put(index, false);
        }

        for (ServerPlayer player : baseLevel.getServer().getPlayerList().getPlayers()) {
            if (!player.level().dimension().equals(baseLevel.dimension())) {
                continue;
            }

            LayerIndexManager manager = LayerIndexStorage.getLayerIndexManager(baseLevel);
            AABB playerTicketArea = getPlayerTicketArea(player);

            int worldHeightInSections = Math.max(1, baseLevel.dimensionType().height() / 16);
            List<StackedChunkPos> chunks = getStackedChunksInAABB(playerTicketArea);

            for (StackedChunkPos chunk : chunks) {
                LayerIndex index = manager.savedData.getLayer(chunk);
                if (index == null) {
                    if (indicesToCreateSet.add(chunk)) {
                        indicesToCreate.add(chunk);
                    }
                    continue;
                }
                keepChunks.put(index, true);
            }
        }

        for (Map.Entry<LayerIndex, Boolean> entry : keepChunks.entrySet()) {
            LayerIndex index = entry.getKey();
            boolean keep = entry.getValue();
            double distance = getDistanceToClosestPlayer(index);

            if (keep) {
                ticketsToRemove.remove(index);
                if (!activeTickets.contains(index)) {
                    ticketsToAdd.put(index, new TicketEntry(index, distance));
                } else {
                    ticketsToAdd.remove(index);
                }
            } else {
                ticketsToAdd.remove(index);
                if (activeTickets.contains(index)) {
                    ticketsToRemove.put(index, new TicketEntry(index, distance));
                } else {
                    ticketsToRemove.remove(index);
                }
            }
        }

        keepChunks.clear();
    }

    public void processQueuedTickets() {
        int created = 0;
        LayerIndexManager manager = LayerIndexStorage.getLayerIndexManager(baseLevel);
        while (created < CREATE_INDICES_PER_TICK && !indicesToCreate.isEmpty()) {
            StackedChunkPos pos = indicesToCreate.poll();
            indicesToCreateSet.remove(pos);
            if (manager.savedData.getLayer(pos) == null) {
                manager.createNewLayerIndex(pos, baseLevel.dimensionType().height());
            }
            created++;
        }

        List<TicketEntry> addEntries = new ArrayList<>(ticketsToAdd.values());
        addEntries.sort(Comparator.comparingDouble(e -> e.distance));

        int processed = 0;
        for (TicketEntry entry : addEntries) {
            if (processed >= MAX_TICKETS_PER_TICK) break;
            if (!activeTickets.contains(entry.index)) {
                addPlayerRegionTicket(entry.index, baseLevel);
                activeTickets.add(entry.index);
                ticketsToAdd.remove(entry.index);
                processed++;
            } else {
                ticketsToAdd.remove(entry.index);
            }
        }

        List<TicketEntry> removeEntries = new ArrayList<>(ticketsToRemove.values());
        removeEntries.sort(Comparator.comparingDouble(e -> e.distance));

        processed = 0;
        for (TicketEntry entry : removeEntries) {
            if (processed >= MAX_TICKETS_PER_TICK) break;
            if (activeTickets.contains(entry.index)) {
                removePlayerRegionTicket(entry.index, baseLevel);
                activeTickets.remove(entry.index);
                ticketsToRemove.remove(entry.index);
                processed++;
            } else {
                ticketsToRemove.remove(entry.index);
            }
        }
    }

    public Set<LayerIndex> getActiveTickets() {
        return Collections.unmodifiableSet(activeTickets);
    }

    private double getDistanceToClosestPlayer(LayerIndex index) {
        double minDistSq = Double.MAX_VALUE;
        for (ServerPlayer player : baseLevel.getServer().getPlayerList().getPlayers()) {
            if (!player.level().dimension().equals(baseLevel.dimension())) continue;
            double dx = player.getX() - index.mainPos().x * 16;
            double dy = player.getY() - index.mainPos().y * 16;
            double dz = player.getZ() - index.mainPos().z * 16;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < minDistSq) minDistSq = distSq;
        }
        return Math.sqrt(minDistSq);
    }

    public synchronized void addPlayerRegionTicket(LayerIndex index, ServerLevel baseLevel) {
        LoadUtils.addTicketToStackedChunk(TicketType.FORCED, index.mainPos(), index.layerPos(), true, baseLevel);
    }

    public synchronized void removePlayerRegionTicket(LayerIndex index, ServerLevel baseLevel) {
        LoadUtils.removeTicketFromStackedChunk(TicketType.FORCED, index.mainPos(), index.layerPos(), true, baseLevel);
    }

    public List<StackedChunkPos> getStackedChunksInAABB(AABB box) {
        List<StackedChunkPos> chunks = new ArrayList<>();

        int worldHeightInSections = Math.max(1, baseLevel.dimensionType().height() / 16);
        int vanillaMinSection = baseLevel.getMinSection();
        int vanillaMaxSection = baseLevel.getMaxSection();

        int minChunkX = SectionPos.blockToSectionCoord((int) Math.floor(box.minX));
        int maxChunkX = SectionPos.blockToSectionCoord((int) Math.floor(box.maxX));
        int minChunkZ = SectionPos.blockToSectionCoord((int) Math.floor(box.minZ));
        int maxChunkZ = SectionPos.blockToSectionCoord((int) Math.floor(box.maxZ));

        int minSectionY = SectionPos.blockToSectionCoord((int) Math.floor(box.minY));
        int maxSectionY = SectionPos.blockToSectionCoord((int) Math.floor(box.maxY));

        int relativeMinY = minSectionY - vanillaMinSection;
        int relativeMaxY = maxSectionY - vanillaMinSection;

        int baseMinY = vanillaMinSection + Math.floorDiv(relativeMinY, worldHeightInSections) * worldHeightInSections;
        int baseMaxY = vanillaMinSection + Math.floorDiv(relativeMaxY, worldHeightInSections) * worldHeightInSections;

        if (baseMaxY < baseMinY) {
            int temp = baseMaxY;
            baseMaxY = baseMinY;
            baseMinY = temp;
        }

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int baseY = baseMinY; baseY <= baseMaxY; baseY += worldHeightInSections) {
                if (isVanillaBaseLayerOverlap(baseY, worldHeightInSections, vanillaMinSection, vanillaMaxSection)) continue;
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    chunks.add(new StackedChunkPos(x, baseY, z));
                }
            }
        }

        return chunks;
    }


    private boolean isVanillaBaseLayerOverlap(int baseYInSections, int heightInSections, int vanillaMinSection, int vanillaMaxSection) {
        if (heightInSections <= 0) return false;
        int layerMaxY = baseYInSections + heightInSections;
        return baseYInSections < vanillaMaxSection + 1 && layerMaxY > vanillaMinSection;
    }

    public static AABB getPlayerTicketArea(ServerPlayer player) {
        int verticalRenderDistance = Config.VERTICAL_SIMULATION_DISTANCE.get() * 16;
        int horizontalRenderDistance = player.server.getPlayerList().getViewDistance() * 16;
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        return new AABB(
                px - horizontalRenderDistance,
                py - verticalRenderDistance,
                pz - horizontalRenderDistance,
                px + horizontalRenderDistance,
                py + verticalRenderDistance,
                pz + horizontalRenderDistance
        );
    }

    private record TicketEntry(LayerIndex index, double distance) {}
}
