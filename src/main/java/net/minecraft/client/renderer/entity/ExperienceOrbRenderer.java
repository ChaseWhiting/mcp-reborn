package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrbEntity> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

   public ExperienceOrbRenderer(EntityRendererManager p_i46178_1_) {
      super(p_i46178_1_);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrbEntity p_225624_1_, BlockPos p_225624_2_) {
      return MathHelper.clamp(super.getBlockLightLevel(p_225624_1_, p_225624_2_) + 7, 0, 15);
   }

   public void render(ExperienceOrbEntity orbEntity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      matrixStack.pushPose();

      int iconIndex = orbEntity.getIcon();

      // UV coordinates for the texture
      float minU = (float)(iconIndex % 4 * 16) / 64.0F;
      float maxU = (float)(iconIndex % 4 * 16 + 16) / 64.0F;
      float minV = (float)(iconIndex / 4 * 16) / 64.0F;
      float maxV = (float)(iconIndex / 4 * 16 + 16) / 64.0F;

      // Orb size constants
      float orbScale = 1.0F;
      float orbHalfSize = 0.5F;
      float orbQuarterSize = 0.25F;

      // Color modulation constants
      float colorMaxValue = 255.0F;
      float tickOffset = ((float) orbEntity.tickCount + partialTicks) / 2.0F;

      // Calculate color values based on tick count and partial ticks
      int red = (int) ((MathHelper.sin(tickOffset) + 1.0F) * 0.5F * colorMaxValue);
      int green = 255;
      int blue = (int) ((MathHelper.sin(tickOffset + 4.1887903F) + 1.0F) * 0.1F * colorMaxValue);

      // Transformations
      matrixStack.translate(0.0D, 0.1D, 0.0D);
      matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

      // Scaling
      float scale = 0.3F;
      matrixStack.scale(scale, scale, scale);

      // Vertex buffer and transformation matrices
      IVertexBuilder vertexBuilder = buffer.getBuffer(RENDER_TYPE);
      MatrixStack.Entry matrixEntry = matrixStack.last();
      Matrix4f poseMatrix = matrixEntry.pose();
      Matrix3f normalMatrix = matrixEntry.normal();

      // Render the vertices
      vertex(vertexBuilder, poseMatrix, normalMatrix, -orbHalfSize, -orbQuarterSize, red, green, blue, minU, maxV, packedLight);
      vertex(vertexBuilder, poseMatrix, normalMatrix, orbHalfSize, -orbQuarterSize, red, green, blue, maxU, maxV, packedLight);
      vertex(vertexBuilder, poseMatrix, normalMatrix, orbHalfSize, orbHalfSize + orbQuarterSize, red, green, blue, maxU, minV, packedLight);
      vertex(vertexBuilder, poseMatrix, normalMatrix, -orbHalfSize, orbHalfSize + orbQuarterSize, red, green, blue, minU, minV, packedLight);

      matrixStack.popPose();
      super.render(orbEntity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
   }

   private static void vertex(IVertexBuilder p_229102_0_, Matrix4f p_229102_1_, Matrix3f p_229102_2_, float p_229102_3_, float p_229102_4_, int p_229102_5_, int p_229102_6_, int p_229102_7_, float p_229102_8_, float p_229102_9_, int p_229102_10_) {
      p_229102_0_.vertex(p_229102_1_, p_229102_3_, p_229102_4_, 0.0F).color(p_229102_5_, p_229102_6_, p_229102_7_, 128).uv(p_229102_8_, p_229102_9_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229102_10_).normal(p_229102_2_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getTextureLocation(ExperienceOrbEntity p_110775_1_) {
      return EXPERIENCE_ORB_LOCATION;
   }
}