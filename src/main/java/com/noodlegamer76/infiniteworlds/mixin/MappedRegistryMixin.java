package com.noodlegamer76.infiniteworlds.mixin;

import com.noodlegamer76.infiniteworlds.level.util.LayerUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
   //@Inject(
   //        method = "register(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lnet/minecraft/core/RegistrationInfo;)Lnet/minecraft/core/Holder$Reference;",
   //        at = @At("RETURN")
   //)
   //private void registerLayerDimensionTypes(int id, ResourceKey<T> key, T value, RegistrationInfo info, CallbackInfoReturnable<Holder.Reference<T>> cir) {
   //    if (!key.location().equals(Registries.DIMENSION_TYPE.location())) return;

   //    WritableRegistry<DimensionType> dimRegistry = (WritableRegistry<DimensionType>) (Object) this;

   //    Map<ResourceKey<DimensionType>, DimensionType> newTypes = new HashMap<>();
   //    for (DimensionType parentDimType : dimRegistry) {
   //        ResourceLocation parentId = dimRegistry.getKey(parentDimType);
   //        if (parentId == null) continue;

   //        DimensionType customDimType = new DimensionType(
   //                parentDimType.fixedTime(),
   //                parentDimType.hasSkyLight(),
   //                parentDimType.hasCeiling(),
   //                parentDimType.ultraWarm(),
   //                parentDimType.natural(),
   //                parentDimType.coordinateScale(),
   //                parentDimType.bedWorks(),
   //                parentDimType.respawnAnchorWorks(),
   //                2032 - 16,
   //                16,
   //                16,
   //                parentDimType.infiniburn(),
   //                parentDimType.effectsLocation(),
   //                parentDimType.ambientLight(),
   //                parentDimType.monsterSettings()
   //        );

   //        ResourceKey<DimensionType> customKey = ResourceKey.create(
   //                Registries.DIMENSION_TYPE,
   //                LayerUtils.getDimLocation(parentId, 1)
   //        );

   //        newTypes.put(customKey, customDimType);
   //    }

   //    for (Map.Entry<ResourceKey<DimensionType>, DimensionType> entry : newTypes.entrySet()) {
   //        dimRegistry.register(entry.getKey(), entry.getValue(), RegistrationInfo.BUILT_IN);
   //    }
   //}
}
