package com.noodlegamer76.infiniteworlds.network.stackedchunk;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class StackedChunkPayload implements CustomPacketPayload {
    public static final Type<StackedChunkPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, "stacked_chunk")
    );

    private final StackedChunk levelChunk;
    public final int x;
    public final int y;
    public final int z;
    private final byte[] chunkDataBuffer;

    public static final StreamCodec<RegistryFriendlyByteBuf, StackedChunkPayload> STREAM_CODEC =
            StreamCodec.of(
                    // Encode method
                    StackedChunkPayload::encode,
                    // Decode method
                    StackedChunkPayload::decode
            );

    public StackedChunkPayload(StackedChunk levelChunk) {
        this.levelChunk = levelChunk;
        this.x = levelChunk.getPos().x;
        this.y = levelChunk.getPos().y;
        this.z = levelChunk.getPos().z;

        this.chunkDataBuffer = StackedChunkPacketHelper.serializeChunkData(levelChunk);
    }

    private StackedChunkPayload(int x, int y, int z, byte[] chunkDataBuffer) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.levelChunk = null;
        this.chunkDataBuffer = chunkDataBuffer;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, StackedChunkPayload payload) {
        buffer.writeInt(payload.x);
        buffer.writeInt(payload.y);
        buffer.writeInt(payload.z);
        buffer.writeByteArray(payload.chunkDataBuffer);
    }

    private static StackedChunkPayload decode(RegistryFriendlyByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        byte[] chunkDataBuffer = buffer.readByteArray();
        return new StackedChunkPayload(x, y, z, chunkDataBuffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public FriendlyByteBuf getChunkDataBuffer() {
        return new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(this.chunkDataBuffer));
    }
}