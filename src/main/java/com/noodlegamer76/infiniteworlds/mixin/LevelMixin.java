package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow public abstract LevelChunk getChunkAt(BlockPos pos);

    @Shadow public abstract void markAndNotifyBlock(BlockPos p_46605_, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState p_46606_, int p_46607_, int p_46608_);

    @Shadow public abstract void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState1, int i);

    @Shadow public abstract void onBlockStateChange(BlockPos pos, BlockState blockState, BlockState newState);

    @Shadow @Final public boolean isClientSide;

    @Inject(
            method = "getBlockState",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getBlockStateFix(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        StackedChunkPos chunkPos = new StackedChunkPos(pos);
        StackedChunk chunk = StackedChunkStorage.get(chunkPos);

        if (chunk != null) {
            cir.setReturnValue(chunk.getBlockState(pos));
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setBlockStateFix(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        StackedChunkPos chunkPos = new StackedChunkPos(pos);
        StackedChunk chunk = StackedChunkStorage.get(chunkPos);

        if (chunk != null) {
            BlockState oldState = chunk.getBlockState(pos);
            if (oldState == state) {
                cir.setReturnValue(false);
                return;
            }

            chunk.setBlockState(pos, state, (flags & 64) != 0);

           //if (isClientSide) {
           //    StackedChunkRenderer.markDirty(chunkPos);
           //}

            this.sendBlockUpdated(pos, oldState, state, flags);
            this.onBlockStateChange(pos, oldState, state);

            cir.setReturnValue(true);
        }

    }
}
