package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RainbowArrowRenderer<T extends AbstractArrowEntity> extends EntityRenderer<T> {
   public RainbowArrowRenderer(EntityRendererManager p_i46193_1_) {
      super(p_i46193_1_);
   }

   @Override
   public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      matrixStack.pushPose();
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot)));

      float shake = (float) entity.shakeTime - partialTicks;
      if (shake > 0.0F) {
         float shakeAngle = -MathHelper.sin(shake * 3.0F) * shake;
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(shakeAngle));
      }

      matrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
      matrixStack.translate(-4.0D, 0.0D, 0.0D);

      MatrixStack.Entry entry = matrixStack.last();
      Matrix4f matrix4f = entry.pose();
      Matrix3f matrix3f = entry.normal();

      // First pass: base texture
      IVertexBuilder builder = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
      renderModel(matrix4f, matrix3f, builder, packedLight);

      //matrixStack.scale(1.001F, 1.001F, 1.001F); // Avoid z-fighting
      Matrix4f glowMatrix = matrixStack.last().pose();
      Matrix3f glowNormal = matrixStack.last().normal();

      IVertexBuilder glint = buffer.getBuffer(RenderType.flatRainbowGlint);
      renderModel(glowMatrix, glowNormal, glint, 0xF000F0); // full bright

      matrixStack.popPose();

      // Render glint
      matrixStack.pushPose();

      matrixStack.popPose();

      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
   }

   private void renderModel(Matrix4f matrix, Matrix3f normal, IVertexBuilder builder, int light) {
      this.vertex(matrix, normal, builder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
      this.vertex(matrix, normal, builder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

      for (int j = 0; j < 4; ++j) {
         matrix = matrix.copy();
         matrix.multiply(Vector3f.XP.rotationDegrees(90.0F));
         this.vertex(matrix, normal, builder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
         this.vertex(matrix, normal, builder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
         this.vertex(matrix, normal, builder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
         this.vertex(matrix, normal, builder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
      }
   }

   public void vertex(Matrix4f matrix4f, Matrix3f normalMatrix, IVertexBuilder builder, int x, int y, int z,
                      float u, float v, int nx, int ny, int nz, int lightmap) {
      builder.vertex(matrix4f, (float) x, (float) y, (float) z)
              .color(255, 255, 255, 255)
              .uv(u, v)
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(lightmap)
              .normal(normalMatrix, (float) nx, (float) nz, (float) ny)
              .endVertex();
   }

   public static IVertexBuilder getRainbowFoilBuffer(IRenderTypeBuffer bufferSource, RenderType baseRenderType) {
      return VertexBuilderUtils.create(
              bufferSource.getBuffer(RenderType.rainbowGlint()), // Glint render type
              bufferSource.getBuffer(baseRenderType)               // Normal render type
      );
   }



}