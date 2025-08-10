package com.noodlegamer76.infiniteworlds.mixin.accessor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {

    @Accessor("tickTime")
    boolean getTickTime();

    @Accessor("customSpawners")
    List<CustomSpawner> getCustomSpawners();
}
