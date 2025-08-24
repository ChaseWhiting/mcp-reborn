package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ColorVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.enderiophage.EntityEnderiophage;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderEnderiophage extends MobRenderer<EntityEnderiophage, ModelEnderiophage> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderiophage.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("textures/entity/enderiophage_glass.png");
    private static final ResourceLocation TEXTURE_NO_GLASS = new ResourceLocation("textures/entity/enderiophage_no_glass.png");

    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("textures/entity/enderiophage_glow.png");
    private static final ResourceLocation TEXTURE_OVERWORLD = new ResourceLocation("textures/entity/enderiophage_overworld_glass.png");
    private static final ResourceLocation TEXTURE_OVERWORLD_NO_GLASS = new ResourceLocation("textures/entity/enderiophage_overworld.png");
    private static final ResourceLocation TEXTURE_OVERWORLD_GLOW = new ResourceLocation("textures/entity/enderiophage_overworld_glow.png");
    private static final ResourceLocation TEXTURE_NETHER = new ResourceLocation("textures/entity/enderiophage_nether.png");
    private static final ResourceLocation TEXTURE_NETHER_GLASS = new ResourceLocation("textures/entity/enderiophage_nether_glass.png");
    private static final ResourceLocation TEXTURE_NETHER_NO_GLASS = new ResourceLocation("textures/entity/enderiophage_nether_no_glass.png");

    private static final ResourceLocation TEXTURE_NETHER_GLOW = new ResourceLocation("textures/entity/enderiophage_nether_glow.png");

    public RenderEnderiophage(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelEnderiophage(), 0.5F);
        this.addLayer(new EnderiophageEyesLayer(this));
        this.addLayer(new EnderiophageGlassLayer(this));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityEnderiophage p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
        if (p_230496_3_) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (p_230496_2_) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return p_230496_4_ ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected void scale(EntityEnderiophage entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entitylivingbaseIn.prevEnderiophageScale + (entitylivingbaseIn.getPhageScale() - entitylivingbaseIn.prevEnderiophageScale) * partialTickTime;
        matrixStackIn.scale(0.8F * scale, 0.8F * scale, 0.8F * scale);
    }


    public ResourceLocation getTextureLocation(EntityEnderiophage entity) {
        return entity.getVariant() == 2 ? entity.hasRepairedCapsid() ? entity.getCapsidColour() == -1 ? TEXTURE_NETHER_GLASS : TEXTURE_NETHER_NO_GLASS : TEXTURE_NETHER : entity.getVariant() == 1 ? entity.getCapsidColour() == -1 ? TEXTURE_OVERWORLD : TEXTURE_OVERWORLD_NO_GLASS : entity.hasRepairedCapsid() ? entity.getCapsidColour() == -1 ? TEXTURE_GLASS : TEXTURE_NO_GLASS : TEXTURE;
    }

    static class EnderiophageEyesLayer extends AbstractEyesLayer<EntityEnderiophage, ModelEnderiophage> {

        public EnderiophageEyesLayer(RenderEnderiophage p_i50928_1_) {
            super(p_i50928_1_);
        }


        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityEnderiophage entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType(entitylivingbaseIn));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType renderType() {
            return RenderType.getGhost(TEXTURE_GLOW);
        }

        public RenderType getRenderType(EntityEnderiophage entity) {
            return RenderType.getGhost(entity.getVariant() == 2 ? TEXTURE_NETHER_GLOW : entity.getVariant() == 1 ?  TEXTURE_OVERWORLD_GLOW : TEXTURE_GLOW);
        }
    }

    static class EnderiophageGlassLayer extends LayerRenderer<EntityEnderiophage, ModelEnderiophage> {
        private static final ResourceLocation GLASS_TEXTURE = new ResourceLocation("textures/entity/enderiophage/glass.png");

        public EnderiophageGlassLayer(RenderEnderiophage m) {
            super(m);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityEnderiophage entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            int colorId = entity.getCapsidColour();
            if (colorId == -1 || entity.getVariant() != 1 && !entity.hasRepairedCapsid()) {
                return;
            }

            float[] rgb = getDyeColor(colorId);

            IVertexBuilder baseBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(GLASS_TEXTURE));
            IVertexBuilder coloredBuilder = new ColorVertexBuilder(baseBuilder, rgb[0], rgb[1], rgb[2], 1.0F);

            this.getParentModel().renderToBuffer(matrixStackIn, coloredBuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }


        private float[] getDyeColor(int dyeId) {
            int color;

            switch (dyeId) {
                case 0 -> color = 0xF9FFFE;
                case 1 -> color = 0xF9801D;
                case 2 -> color = 0xC74EBD;
                case 3 -> color = 0x3AB3DA;
                case 4 -> color = 0xFED83D;
                case 5 -> color = 0x80C71F;
                case 6 -> color = 0xF38BAA;
                case 7 -> color = 0x474F52;
                case 8 -> color = 0x9D9D97;
                case 9 -> color = 0x169C9C;
                case 10 -> color = 0x8932B8;
                case 11 -> color = 0x3C44AA;
                case 12 -> color = 0x835432;
                case 13 -> color = 0x5E7C16;
                case 14 -> color = 0xB02E26;
                case 15 -> color = 0x1D1D21;
                default -> color = 0xFFFFFF;
            }

            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            return new float[] { r, g, b };
        }
    }


}