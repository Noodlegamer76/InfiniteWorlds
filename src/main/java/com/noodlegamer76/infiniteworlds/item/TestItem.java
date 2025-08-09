package com.noodlegamer76.infiniteworlds.item;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.ChunkRenderSection;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import com.noodlegamer76.infiniteworlds.level.chunk.util.FillChunk;
import com.noodlegamer76.infiniteworlds.network.stackedchunk.StackedChunkPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            StackedChunkStorage.clear();
            StackedChunkRenderer.clear();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        StackedChunk chunk = StackedChunkStorage.getOrCreate(level, new StackedChunkPos(x, y + 20, z));
                        FillChunk.fillChunkWithDirt(chunk);
                        StackedChunkPayload payload = new StackedChunkPayload(chunk);
                        ((ServerPlayer) player).connection.send(payload);
                    }
                }
            }
        }
        if (level.isClientSide() && player.isShiftKeyDown()) {
            System.out.println(StackedChunkStorage.getChunks().size());
            boolean isCompiled = Minecraft.getInstance().levelRenderer.isSectionCompiled(new BlockPos(0, 16 * 20, 0));
            System.out.println(isCompiled);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
