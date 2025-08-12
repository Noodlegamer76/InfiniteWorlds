package com.noodlegamer76.infiniteworlds.level.dimension.storage;

import com.noodlegamer76.infiniteworlds.level.dimension.LayerIndexManager;
import com.noodlegamer76.infiniteworlds.mixin.accessor.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class LayerIndexStorage {
    public static final Map<ServerLevel, LayerIndexManager> layerIndexManagers = new HashMap<>();

    public static LayerIndexManager getLayerIndexManager(ServerLevel level) {
        return layerIndexManagers.computeIfAbsent(level, l -> new LayerIndexManager(level));
    }
}
