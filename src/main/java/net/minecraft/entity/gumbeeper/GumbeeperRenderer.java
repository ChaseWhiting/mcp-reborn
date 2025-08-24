package net.minecraft.entity.gumbeeper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class GumbeeperRenderer extends MobRenderer<GumbeeperEntity, GumbeeperModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/gumbeeper.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("textures/entity/gumbeeper_glass.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation("textures/entity/gumbeeper_explode.png");
    private static final ResourceLocation TEXTURE_POSSESSED = new ResourceLocation("textures/entity/gumbeeper_possessed.png");

    public GumbeeperRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new GumbeeperModel(0.0F), 0.8F);
        this.addLayer(new LayerGlow());
        this.addLayer(new GumbeeperEnergySwirlLayer(this));
        //this.addLayer(new LicowitchPossessionLayer<>(this, gumbeeperEntity -> TEXTURE_EXPLODE));
    }

    protected void scale(GumbeeperEntity mob, MatrixStack poseStack, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(GumbeeperEntity entity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(GumbeeperEntity deepOneMageEntity, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(deepOneMageEntity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    class LayerGlow extends LayerRenderer<GumbeeperEntity, GumbeeperModel> {

        public LayerGlow() {
            super(GumbeeperRenderer.this);
        }

        public void render(MatrixStack poseStack, IRenderTypeBuffer bufferIn, int packedLightIn, GumbeeperEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityTranslucentCull(TEXTURE_GLASS));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder2, packedLightIn, LivingRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            float explodeProgress = entitylivingbaseIn.getExplodeProgress(partialTicks);
            float alpha = (float)(Math.sin(ageInTicks * 1.2F) + 1F) * 0.5F * explodeProgress * 0.8F;
            IVertexBuilder ivertexbuilder4 = bufferIn.getBuffer(RenderType.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder4, packedLightIn, LivingRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

        }
    }
}