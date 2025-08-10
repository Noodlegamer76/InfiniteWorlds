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
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            ClientLevel clientLevel = mc.level;
            if (clientLevel == null) return;

            List<StackedChunk> chunks = StackedChunkLoader.loadChunksFromPayload(clientLevel, payload);
            for (StackedChunk chunk : chunks) {
                StackedChunkStorage.put(chunk);
                StackedChunkRenderer.addStackedChunk(chunk.getPos());
            }
        });
    }

}
