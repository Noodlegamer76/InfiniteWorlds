package com.noodlegamer76.infiniteworlds.level.util;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class LayerUtils {

    public static ResourceKey<Level> getLevelKey(ServerLevel baseLevel, int layer) {
        return ResourceKey.create(Registries.DIMENSION,
                ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, (baseLevel.dimension().location().toString()).replaceFirst(":", "_") + "_layer_" + layer));
    }

    public static ResourceKey<DimensionType> getTypeKey(ServerLevel baseLevel, int layer) {
        return ResourceKey.create(Registries.DIMENSION_TYPE,
                ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, (baseLevel.dimension().location().toString()).replaceFirst(":", "_") + "_layer_" + layer));
    }

    public static ResourceLocation getDimLocation(ResourceLocation parent, int layer) {
        return ResourceLocation.fromNamespaceAndPath(InfiniteWorlds.MODID, (parent.toString()).replaceFirst(":", "_") + "_layer_" + layer);
    }
}
