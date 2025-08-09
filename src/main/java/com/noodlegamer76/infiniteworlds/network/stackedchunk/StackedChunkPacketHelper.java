package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StackedChunkPacketHelper {

    public static byte[] serializeChunkData(StackedChunk chunk) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

        try {
            // Iterate through our chunk's sections and write each one to the buffer.
            for (LevelChunkSection section : chunk.getSections()) {
                section.write(buffer);
            }
            // Copy the data from the buffer to the byte array
            buffer.getBytes(0, byteArrayOutputStream, buffer.writerIndex());
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            // Handle any potential IO errors
            e.printStackTrace();
            return new byte[0];
        } finally {
            buffer.release();
        }
    }
}