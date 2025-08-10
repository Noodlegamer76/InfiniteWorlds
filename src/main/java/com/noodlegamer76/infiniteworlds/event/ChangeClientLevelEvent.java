package com.noodlegamer76.infiniteworlds.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = InfiniteWorlds.MODID)
public class ChangeClientLevelEvent {

    @SubscribeEvent
    public static void unloadClientLevelEvent(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            StackedChunkRenderer.clear();
            StackedChunkStorage.clear();
        }
    }

    @SubscribeEvent
    public static void loadClientLevelEvent(LevelEvent.Load event) {
    }
}
