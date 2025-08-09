package com.noodlegamer76.infiniteworlds.mixin.accessor;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.LevelRenderer")
public interface LevelRendererAccessor {

    @Accessor("sectionRenderDispatcher")
    SectionRenderDispatcher getSectionRenderDispatcher();
}
