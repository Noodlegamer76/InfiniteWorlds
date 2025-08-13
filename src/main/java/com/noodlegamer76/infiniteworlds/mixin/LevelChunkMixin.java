package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.util.ChunkUtils;
import com.noodlegamer76.infiniteworlds.mixin.accessor.ChunkAccessAccessor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Shadow
    public abstract Level getLevel();

    @Inject(
            method = "getBlockState",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getBlockStateStacked(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Level level = getLevel();

        if (level == null) return;

        StackedChunkPos chunkPos = new StackedChunkPos(pos);
        LevelChunk stackedChunk = ChunkUtils.getLayerChunkOrClientChunk(chunkPos, level);
        if (stackedChunk != null) {
            cir.setReturnValue(stackedChunk.getBlockState(pos));
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (getLevel().isDebug()) {
            BlockState blockstate = null;
            if (y == 60) {
                blockstate = Blocks.BARRIER.defaultBlockState();
            }

            if (y == 70) {
                blockstate = DebugLevelSource.getBlockStateFor(x, z);
            }

            cir.setReturnValue(blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate);
        } else {
            try {
                int l = getLevel().getSectionIndex(y);
                if (l >= 0 && l < ((ChunkAccessAccessor) this).getSections().length) {
                    LevelChunkSection levelchunksection = ((ChunkAccessAccessor) this).getSections()[l];
                    if (!levelchunksection.hasOnlyAir()) {
                        cir.setReturnValue(levelchunksection.getBlockState(x & 15, y & 15, z & 15));
                        return;
                    }
                }

                cir.setReturnValue(Blocks.AIR.defaultBlockState());
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting block state");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
                crashreportcategory.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelChunk) (Object) this, x, y, z));
                throw new ReportedException(crashreport);
            }
        }
    }
}
