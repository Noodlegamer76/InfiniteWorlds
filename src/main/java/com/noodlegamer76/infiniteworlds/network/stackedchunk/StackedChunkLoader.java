package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class StackedChunkLoader {

    public static StackedChunk loadChunkFromPayload(ClientLevel clientLevel, StackedChunkPayload payload) {
        int chunkX = payload.x;
        int chunkY = payload.y;
        int chunkZ = payload.z;

        StackedChunkPos chunkPos = new StackedChunkPos(chunkX, chunkY, chunkZ);
        StackedChunk chunk = new StackedChunk(clientLevel, chunkPos);

        FriendlyByteBuf buffer = payload.getChunkDataBuffer();

        LevelChunkSection[] sections = chunk.getSections();
        for (int i = 0; i < sections.length; i++) {
            sections[i].read(buffer);
        }

        return chunk;
    }
}
