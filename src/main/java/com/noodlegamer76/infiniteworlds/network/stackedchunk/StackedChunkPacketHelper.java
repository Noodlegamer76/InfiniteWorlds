package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class StackedChunkPacketHelper {

    public static byte[] serializeChunkData(StackedChunk chunk) {
        return serializeChunkData(List.of(chunk));
    }

    public static byte[] serializeChunkData(List<StackedChunk> chunks) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(chunks.size());

        for (StackedChunk chunk : chunks) {
            buffer.writeInt(chunk.getPos().x);
            buffer.writeInt(chunk.getPos().y);
            buffer.writeInt(chunk.getPos().z);

            FriendlyByteBuf chunkBuffer = new FriendlyByteBuf(Unpooled.buffer());
            for (LevelChunkSection section : chunk.getSections()) {
                section.write(chunkBuffer);
            }

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
