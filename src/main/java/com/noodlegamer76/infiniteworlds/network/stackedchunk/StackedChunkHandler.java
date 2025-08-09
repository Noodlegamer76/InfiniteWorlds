package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class StackedChunkHandler {

    public static void handle(StackedChunkPayload payload, IPayloadContext context) {
        StackedChunk levelChunk = StackedChunkLoader.loadChunkFromPayload(Minecraft.getInstance().level, payload);
        StackedChunkStorage.put(levelChunk);
        StackedChunkRenderer.addStackedChunk(levelChunk);
    }

}
