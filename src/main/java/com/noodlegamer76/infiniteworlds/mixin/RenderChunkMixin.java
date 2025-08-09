package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.renderer.chunk.RenderChunk")
public class RenderChunkMixin {

   //@Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
   //private void getBlockStateInject(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {

   //    int stackedY = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getY());

   //    StackedChunkPos chunkPos = new StackedChunkPos(
   //            net.minecraft.core.SectionPos.blockToSectionCoord(pos.getX()),
   //            stackedY,
   //            net.minecraft.core.SectionPos.blockToSectionCoord(pos.getZ())
   //    );

   //    StackedChunk stacked = StackedChunkStorage.getChunks().get(chunkPos);
   //    if (stacked != null) {
   //        BlockState state = stacked.getBlockState(pos);
   //        cir.setReturnValue(state);
   //    }
   //}
}
