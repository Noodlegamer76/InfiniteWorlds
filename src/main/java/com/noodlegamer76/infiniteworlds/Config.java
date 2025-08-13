package com.noodlegamer76.infiniteworlds;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue VERTICAL_SIMULATION_DISTANCE = BUILDER
            .comment("The vertical render distance.")
            .defineInRange("verticalRenderDistance", 8, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue VERTICAL_RENDER_DISTANCE = BUILDER
            .comment("The vertical simulation distance. This number is measured in (value * world height), and is centered rather than stretched.")
            .defineInRange("verticalRenderDistance", 8, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MAX_CHUNKS_PER_TICK = BUILDER
            .comment("how many chunk per tick to load, excluding vanilla areas.")
            .defineInRange("chunksPerTick", 7, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MAX_CHUNKS_RENDER_PER_TICK = BUILDER
            .comment("how many chunk per tick to render, excluding vanilla areas.")
            .defineInRange("chunksRenderPerTick", 7, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
