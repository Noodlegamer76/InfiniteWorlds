package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import io.netty.buffer.Unpooled;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.List;

public class StackedChunkLoader {

    public static List<StackedChunk> loadChunksFromPayload(ClientLevel clientLevel, StackedChunkPayload payload) {
        FriendlyByteBuf buffer = payload.getChunkDataBuffer();
        int count = buffer.readVarInt();

        List<StackedChunk> chunks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();

            int length = buffer.readVarInt();
            byte[] chunkData = new byte[length];
            buffer.readBytes(chunkData);

            StackedChunkPos pos = new StackedChunkPos(x, y, z);
            StackedChunk chunk = new StackedChunk(clientLevel, pos);

            FriendlyByteBuf chunkBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(chunkData));
            chunk.getSection(0).read(chunkBuffer);
            chunkBuffer.release();

            chunks.add(chunk);
        }

        return chunks;
    }

}
