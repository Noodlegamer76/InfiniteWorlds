package com.noodlegamer76.infiniteworlds.event;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.level.dimension.loading.RenderDistanceManagers;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = InfiniteWorlds.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void joinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            RenderDistanceManagers.onPlayerJoin(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void leaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            RenderDistanceManagers.onPlayerLeave(serverPlayer);
        }
    }
}
