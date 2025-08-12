package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class StackedChunkPacketHelper {

    public static byte[] serializeChunkData(StackedChunkInfo chunk) {
        return serializeChunkData(List.of(chunk));
    }

    public static byte[] serializeChunkData(List<StackedChunkInfo> chunks) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(chunks.size());

        for (StackedChunkInfo chunk : chunks) {
            buffer.writeInt(chunk.pos().x);
            buffer.writeInt(chunk.pos().y);
            buffer.writeInt(chunk.pos().z);

            FriendlyByteBuf chunkBuffer = new FriendlyByteBuf(Unpooled.buffer());
            chunk.chunk().getSection(chunk.offset()).write(chunkBuffer);

            buffer.writeVarInt(chunkBuffer.readableBytes());
            buffer.writeBytes(chunkBuffer);
            chunkBuffer.release();
        }

        byte[] result = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), result);
        buffer.release();
        return result;
    }
}
