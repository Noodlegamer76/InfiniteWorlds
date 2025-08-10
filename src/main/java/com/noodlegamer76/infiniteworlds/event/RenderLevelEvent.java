package com.noodlegamer76.infiniteworlds.event;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = InfiniteWorlds.MODID)
public class RenderLevelEvent {

    @SubscribeEvent
    public static void renderLevelEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            StackedChunkRenderer.init(Minecraft.getInstance().level);
            StackedChunkRenderer.processChunks();
        }
    }
}
