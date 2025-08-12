package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.Config;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.util.LoadUtils;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Shadow @Final public ServerLevel level;


   //@Inject(
   //        method = "addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;Z)V",
   //        at = @At("RETURN")
   //)
   //private <T> void onAddRegionTicket(
   //        TicketType<T> type,
   //        ChunkPos pos,
   //        int distance,
   //        T value,
   //        boolean forceTicks,
   //        CallbackInfo ci
   //) {
   //    if (type == TicketType.PLAYER) {
   //        ServerChunkCache self = (ServerChunkCache) (Object) this;
   //        ServerLevel level = (ServerLevel) self.getLevel();

   //        StackedChunkPos stackedPos = new StackedChunkPos(pos.x, 0, pos.z);

   //        int horizontalRadius = 1;
   //        int verticalRadius = Config.VERTICAL_RENDER_DISTANCE.getAsInt();

   //        LoadUtils.addTicketToStackedChunks(type, stackedPos, horizontalRadius, verticalRadius, value, forceTicks, level);
   //    }
   //}

   //@Inject(
   //        method = "removeRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;Z)V",
   //        at = @At("RETURN")
   //)
   //private <T> void onRemoveRegionTicket(
   //        TicketType<T> type,
   //        ChunkPos pos,
   //        int distance,
   //        T value,
   //        boolean forceTicks,
   //        CallbackInfo ci
   //) {
   //    if (type == TicketType.PLAYER) {
   //        ServerChunkCache self = (ServerChunkCache) (Object) this;
   //        ServerLevel level = (ServerLevel) self.getLevel();

   //        StackedChunkPos stackedPos = new StackedChunkPos(pos.x, 0, pos.z);

   //        int horizontalRadius = 1;
   //        int verticalRadius = Config.VERTICAL_RENDER_DISTANCE.getAsInt();

   //        LoadUtils.removeTicketFromStackedChunks(type, stackedPos, horizontalRadius, verticalRadius, value, forceTicks, level);
   //    }
   //}

}
