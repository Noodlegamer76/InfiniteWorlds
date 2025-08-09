package com.noodlegamer76.infiniteworlds.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.noodlegamer76.infiniteworlds.level.chunk.StackedChunkPos;
import com.noodlegamer76.infiniteworlds.level.chunk.render.ChunkRenderSection;
import com.noodlegamer76.infiniteworlds.level.chunk.render.StackedChunkRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

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

        if (renderType == RenderType.translucent()) {
            this.minecraft.getProfiler().push("translucent_sort");
            double d0 = x - this.yTransparentOld;
            double d1 = y - this.yTransparentOld;
            double d2 = z - this.zTransparentOld;
            if (d0 * d0 + d1 * d1 + d2 * d2 > (double) 1.0F) {
                int i = SectionPos.posToSectionCoord(x);
                int j = SectionPos.posToSectionCoord(y);
                int k = SectionPos.posToSectionCoord(z);
                boolean flag = i != SectionPos.posToSectionCoord(this.xTransparentOld) || k != SectionPos.posToSectionCoord(this.zTransparentOld) || j != SectionPos.posToSectionCoord(this.yTransparentOld);
                this.xTransparentOld = x;
                this.yTransparentOld = y;
                this.zTransparentOld = z;
                int l = 0;

                for (ChunkRenderSection section : StackedChunkRenderer.getStackedSections().values()) {
                    if (sectionRenderDispatcher == null) continue;
                    if (l < 15 && (flag || section.getRenderSection().isAxisAlignedWith(i, j, k)) && section.getRenderSection().resortTransparency(renderType, this.sectionRenderDispatcher)) {
                        ++l;
                    }
                }
            }
        }

        this.minecraft.getProfiler().pop();

        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> "render_" + renderType);
        boolean flag1 = renderType != RenderType.translucent();
        ObjectListIterator<SectionRenderDispatcher.RenderSection> objectlistiterator = this.visibleSections.listIterator(flag1 ? 0 : this.visibleSections.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();
        shaderinstance.setDefaultUniforms(VertexFormat.Mode.QUADS, frustrumMatrix, projectionMatrix, this.minecraft.getWindow());
        shaderinstance.apply();
        Uniform uniform = shaderinstance.CHUNK_OFFSET;

        while(true) {
            if (flag1) {
                if (!objectlistiterator.hasNext()) {
                    break;
                }
            } else if (!objectlistiterator.hasPrevious()) {
                break;
            }

            SectionRenderDispatcher.RenderSection section = flag1 ? objectlistiterator.next() : objectlistiterator.previous();
            if (!section.getCompiled().isEmpty(renderType)) {
                VertexBuffer vertexbuffer = section.getBuffer(renderType);
                BlockPos blockpos = section.getOrigin();
                if (uniform != null) {
                    uniform.set((float)((double)blockpos.getX() - x), (float)((double)blockpos.getY() - y), (float)((double)blockpos.getZ() - z));
                    uniform.upload();
                }

                vertexbuffer.bind();
                vertexbuffer.draw();
            }
        }

        for (Map.Entry<StackedChunkPos, ChunkRenderSection> entry : StackedChunkRenderer.getStackedSections().entrySet()) {
            SectionRenderDispatcher.RenderSection section = entry.getValue().getRenderSection();
            if (section.getCompiled().isEmpty(renderType)) continue;
            VertexBuffer vb = section.getBuffer(renderType);
            uniform.set(
                    (float) (section.getOrigin().getX() - x),
                    (float) (section.getOrigin().getY() - y),
                    (float) (section.getOrigin().getZ() - z)
            );
            uniform.upload();
            vb.bind();
            vb.draw();
        }

        if (uniform != null) {
            uniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderinstance.clear();
        VertexBuffer.unbind();
        this.minecraft.getProfiler().pop();
        ClientHooks.dispatchRenderStage(renderType, (LevelRenderer) (Object) this, frustrumMatrix, projectionMatrix, ticks, this.minecraft.gameRenderer.getMainCamera(), getFrustum());
        renderType.clearRenderState();
    }
}
