package com.noodlegamer76.infiniteworlds.event;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import com.noodlegamer76.infiniteworlds.network.stackedchunk.StackedChunkHandler;
import com.noodlegamer76.infiniteworlds.network.stackedchunk.StackedChunkPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = InfiniteWorlds.MODID)
public class RegisterPayloads {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                StackedChunkPayload.TYPE,
                StackedChunkPayload.STREAM_CODEC,
                StackedChunkHandler::handle
        );

    }
}
