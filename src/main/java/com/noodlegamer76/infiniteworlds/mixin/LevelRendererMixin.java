package com.noodlegamer76.infiniteworlds.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunk;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.ChunkRenderSection;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import com.noodlegamer76.infiniteworlds.level.chunk.storage.StackedChunkStorage;
import com.noodlegamer76.infiniteworlds.mixin.accessor.VertexBufferAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

import static com.noodlegamer76.infiniteworlds.InfiniteWorlds.LOGGER;

@Mixin(targets = "net.minecraft.client.renderer.LevelRenderer")
public abstract class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private double yTransparentOld;

    @Shadow private double zTransparentOld;

    @Shadow private double xTransparentOld;

    @Shadow @Final private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;

    @Shadow @Nullable private SectionRenderDispatcher sectionRenderDispatcher;

    @Shadow private int ticks;

    @Shadow public abstract Frustum getFrustum();

    @Inject(method = "renderSectionLayer", at = @At("TAIL"))
    private void renderExtraSections(RenderType renderType, double x, double y, double z,
                                     Matrix4f frustrumMatrix, Matrix4f projectionMatrix,
                                     CallbackInfo ci) {

        RenderSystem.assertOnRenderThread();
        renderType.setupRenderState();

        ShaderInstance shaderInstance = RenderSystem.getShader();
        shaderInstance.setDefaultUniforms(VertexFormat.Mode.QUADS, frustrumMatrix, projectionMatrix, this.minecraft.getWindow());
        shaderInstance.apply();

        Uniform uniform = shaderInstance.CHUNK_OFFSET;

        var sections = StackedChunkRenderer.getStackedSections().values();

        if (renderType == RenderType.translucent()) {
            List<ChunkRenderSection> sortedSections = new ArrayList<>(sections);
            sortedSections.sort(Comparator.comparingDouble(ChunkRenderSection::getCachedDistance).reversed());

            for (ChunkRenderSection section : sortedSections) {
                shaderInstance.apply();
                SectionRenderDispatcher.RenderSection renderSection = section.getRenderSection();
                if (renderSection.getCompiled().isEmpty(renderType)) continue;

                VertexBuffer vb = renderSection.getBuffer(renderType);
                BlockPos pos = renderSection.getOrigin();

                if (uniform != null) {
                    uniform.set((float)(pos.getX() - x), (float)(pos.getY() - y), (float)(pos.getZ() - z));
                    uniform.upload();
                }

                try {
                    if (((VertexBufferAccessor) vb).getMode() == null) continue;
                    vb.bind();
                    vb.draw();
                } catch (Throwable t) {
                    LOGGER.warn("Skipping draw of invalid vertex buffer for section {}: {}", renderSection.getOrigin(), t.toString());
                }
            }
        } else {
            for (ChunkRenderSection section : sections) {
                shaderInstance.apply();
                SectionRenderDispatcher.RenderSection renderSection = section.getRenderSection();
                if (renderSection.getCompiled().isEmpty(renderType)) continue;

                VertexBuffer vb = renderSection.getBuffer(renderType);
                BlockPos pos = renderSection.getOrigin();

                if (uniform != null) {
                    uniform.set((float)(pos.getX() - x), (float)(pos.getY() - y), (float)(pos.getZ() - z));
                    uniform.upload();
                }

                try {
                    if (((VertexBufferAccessor) vb).getMode() == null) continue;
                    vb.bind();
                    vb.draw();
                } catch (Throwable t) {
                    LOGGER.warn("Skipping draw of invalid vertex buffer for section {}: {}", renderSection.getOrigin(), t.toString());
                }
            }
        }

        if (uniform != null) {
            uniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderInstance.clear();
        VertexBuffer.unbind();
        this.minecraft.getProfiler().pop();
        //ClientHooks.dispatchRenderStage(renderType, (LevelRenderer)(Object)this, frustrumMatrix, projectionMatrix, ticks, this.minecraft.gameRenderer.getMainCamera(), getFrustum());
        renderType.clearRenderState();
    }



}
