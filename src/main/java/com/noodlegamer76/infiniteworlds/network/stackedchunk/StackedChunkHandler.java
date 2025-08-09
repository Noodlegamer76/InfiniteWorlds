package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;


public class StackedChunkHandler {

    public static void handle(StackedChunkPayload payload, IPayloadContext context) {
        List<StackedChunk> levelChunks = StackedChunkLoader.loadChunksFromPayload(Minecraft.getInstance().level, payload);
        for (StackedChunk chunk: levelChunks) {
            StackedChunkStorage.put(chunk);
            StackedChunkRenderer.addStackedChunk(chunk);
        }
    }

}
