package com.noodlegamer76.infiniteworlds.level.util;

import com.google.common.collect.ImmutableList;
import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public class CreateLayer {
    public static ServerLevel createLayer(ServerLevel baseLevel, int layer) {
        if (layer == 0) {
            InfiniteWorlds.LOGGER.warn("Tried to create a layer at level 0, Please report this to the mod author");
            return null;
        }

        ResourceKey<Level> key = LayerUtils.getLevelKey(baseLevel, layer);

        MinecraftServer server = baseLevel.getServer();
        LevelStorageSource.LevelStorageAccess levelStorage = LevelCreationVariables.getStorageSource();
        ChunkProgressListener listener = LevelCreationVariables.getListener();
        Map<ResourceKey<Level>, ServerLevel> levels = LevelCreationVariables.getLevels();

        LevelStem stem = createStemForLayers(server, baseLevel, 1);

        DerivedLevelData derivedleveldata = new DerivedLevelData(server.getWorldData(), server.getWorldData().overworldData());

        ServerLevel newLevel = new ServerLevel(
                server,
                LevelCreationVariables.getExecutor(),
                levelStorage,
                derivedleveldata,
                key,
                stem,
                listener,
                false,
                baseLevel.getSeed(),
                ImmutableList.of(),
                false,
                baseLevel.getRandomSequences()
        );

        return newLevel;
    }

    public static LevelStem createStemForLayers(MinecraftServer server, ServerLevel baseLevel, int layer) {
        RegistryAccess registryAccess = server.registryAccess();

        Registry<DimensionType> dimensionTypes = registryAccess.registryOrThrow(Registries.DIMENSION_TYPE);
        ResourceLocation dimTypeHolder = dimensionTypes.getKey(baseLevel.dimensionType());
        ResourceKey<DimensionType> dimTypeKey = ResourceKey.create(Registries.DIMENSION_TYPE, dimTypeHolder);

        Holder<DimensionType> holder = dimensionTypes.getHolderOrThrow(dimTypeKey);

        ChunkGenerator generator = baseLevel.getChunkSource().getGenerator();

        return new LevelStem(holder, generator);
    }
}
