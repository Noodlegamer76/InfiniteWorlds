package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StackedChunkPayload implements CustomPacketPayload {
    public static final Type<StackedChunkPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, "stacked_chunk")
    );

    private final byte[] chunkDataBuffer;

    public static final StreamCodec<RegistryFriendlyByteBuf, StackedChunkPayload> STREAM_CODEC =
            StreamCodec.of(
                    // Encode method
                    StackedChunkPayload::encode,
                    // Decode method
                    StackedChunkPayload::decode
            );

    public StackedChunkPayload(StackedChunk levelChunk) {
        this.chunkDataBuffer = StackedChunkPacketHelper.serializeChunkData(levelChunk);
    }

    public StackedChunkPayload(List<StackedChunk> levelChunks) {
        this.chunkDataBuffer = StackedChunkPacketHelper.serializeChunkData(levelChunks);
    }

    private StackedChunkPayload(byte[] chunkDataBuffer) {
        this.chunkDataBuffer = chunkDataBuffer;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, StackedChunkPayload payload) {
        buffer.writeByteArray(payload.chunkDataBuffer);
    }

    private static StackedChunkPayload decode(RegistryFriendlyByteBuf buffer) {
        byte[] chunkDataBuffer = buffer.readByteArray();
        return new StackedChunkPayload(chunkDataBuffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public FriendlyByteBuf getChunkDataBuffer() {
        return new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(this.chunkDataBuffer));
    }
}