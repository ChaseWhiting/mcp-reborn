package net.minecraft.entity.gumbeeper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class GumballRenderer extends EntityRenderer<GumballEntity> {
    static String ID = "minecraft";
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation(ID, "textures/entity/gumball/gumball_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation(ID, "textures/entity/gumball/gumball_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation(ID, "textures/entity/gumball/gumball_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation(ID, "textures/entity/gumball/gumball_3.png");
    private static final ResourceLocation TEXTURE_4 = new ResourceLocation(ID, "textures/entity/gumball/gumball_4.png");
    private static final ResourceLocation TEXTURE_5 = new ResourceLocation(ID, "textures/entity/gumball/gumball_5.png");
    private static final ResourceLocation TEXTURE_6 = new ResourceLocation(ID, "textures/entity/gumball/gumball_6.png");
    private static final ResourceLocation TEXTURE_7 = new ResourceLocation(ID, "textures/entity/gumball/gumball_7.png");
    private static final ResourceLocation TEXTURE_8 = new ResourceLocation(ID, "textures/entity/gumball/gumball_8.png");
    private static final ResourceLocation TEXTURE_9 = new ResourceLocation(ID, "textures/entity/gumball/gumball_9.png");
    private static final ResourceLocation TEXTURE_10 = new ResourceLocation(ID, "textures/entity/gumball/gumball_10.png");
    private static final ResourceLocation TEXTURE_EXPLODING = new ResourceLocation(ID, "textures/entity/gumball/gumball_exploding.png");

    public GumballRenderer(EntityRendererManager context) {
        super(context);
    }

    public void render(GumballEntity entity, float entityYaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLight) {
        poseStack.pushPose();
        float explodeAmount = entity.getExplodeProgress(partialTicks);
        float scale = entity.isExplosive() ? 0.5F + explodeAmount * 0.2F : 0.25F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MatrixStack.Entry posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        IVertexBuilder vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, 1F);
        if(entity.isExplosive()){
            float explodeColorChange = entity.getBounces() >= entity.getMaximumBounces() ? 1.0F - 0.5F * (1F + MathHelper.sin((entity.tickCount + partialTicks) * 0.9F)) : 0.0F;
            IVertexBuilder vertexconsumer2 = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_EXPLODING));
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, explodeColorChange);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    private static void vertex(IVertexBuilder vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int p_253829_, float x, int y, int u, int v, float alpha) {
        vertexConsumer.vertex(matrix4f, x - 0.5F, (float)y - 0.25F, 0.0F).color(1F, 1F, 1F, alpha).uv((float)u, (float)v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_253829_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(GumballEntity gumballEntity) {
        switch (gumballEntity.getColor()){
            case 0:
                return TEXTURE_0;
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case 3:
                return TEXTURE_3;
            case 4:
                return TEXTURE_4;
            case 5:
                return TEXTURE_5;
            case 6:
                return TEXTURE_6;
            case 7:
                return TEXTURE_7;
            case 8:
                return TEXTURE_8;
            case 9:
                return TEXTURE_9;
            case 10:
                return TEXTURE_10;
            default:
                return TEXTURE_0;
        }
    }
}