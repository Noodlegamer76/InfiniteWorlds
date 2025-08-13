package com.noodlegamer76.infiniteworlds.event;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.dimension.loading.LayerRenderDistanceManager;
import com.noodlegamer76.infiniteworlds.level.dimension.loading.LayerTicketManager;
import com.noodlegamer76.infiniteworlds.level.dimension.loading.RenderDistanceManagers;
import com.noodlegamer76.infiniteworlds.level.dimension.storage.LayerIndexStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = InfiniteWorlds.MODID)
public class TickEvents {
    private static int tick = 0;

    @SubscribeEvent
    public static void preTick(ServerTickEvent.Pre event) {
        if (++tick > Integer.MAX_VALUE - 1000) tick = 0;

        event.getServer().getAllLevels().forEach(level -> {
            if (!level.dimension().location().toString().endsWith("_layer_1")) {
                LayerTicketManager manager = LayerIndexStorage.getLayerIndexManager(level).ticketManager;

                if (tick % 20 == 0) {
                    try {
                        manager.updatePlayerTickets();
                    } catch (Exception e) {
                        InfiniteWorlds.LOGGER.error("Error updating player tickets for level " + level.dimension().location(), e);
                    }
                }

                manager.processQueuedTickets();
            }
        });

        RenderDistanceManagers.tick(event.getServer());
    }


}