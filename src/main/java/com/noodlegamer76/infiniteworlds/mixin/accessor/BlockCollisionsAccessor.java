package com.noodlegamer76.infiniteworlds.mixin.accessor;

import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockCollisions.class)
public interface BlockCollisionsAccessor {

    @Accessor(value = "collisionGetter")
    CollisionGetter getCollisionGetter();
}
