package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishRenderer extends EntityRenderer<FishingBobberEntity> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/hooks/metal_fishing_hook.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

   // Tunable constants pulled out of the original code for clarity
   private static final float BOBBER_SCALE = 0.5F;
   private static final double FIRST_PERSON_FOV_SCALE = 1.0D / 100.0D;
   private static final double LINE_OFFSET_SIDE = 0.35D;
   private static final double LINE_OFFSET_BACK = 0.8D;
   private static final float LINE_SEGMENTS = 16.0F;

   public FishRenderer(EntityRendererManager dispatcher) {
      super(dispatcher);
   }

   @Override
   public void render(FishingBobberEntity bobber, float entityYaw, float partialTicks,
                      MatrixStack poseStack, IRenderTypeBuffer buffers, int packedLight) {
      PlayerEntity angler = bobber.getPlayerOwner();
      if (angler == null) return;

      poseStack.pushPose();

      // 1) Render the bobber sprite (billboarded quad)
      renderBobberSprite(poseStack, buffers, packedLight);

      // 2) Render the fishing line between the angler and the bobber
      renderFishingLine(bobber, angler, partialTicks, poseStack, buffers);

      poseStack.popPose();

      super.render(bobber, entityYaw, partialTicks, poseStack, buffers, packedLight);
   }

   // ----------------------- Bobber -----------------------
   private void renderBobberSprite(MatrixStack poseStack, IRenderTypeBuffer buffers, int packedLight) {
      poseStack.pushPose();
      poseStack.scale(BOBBER_SCALE, BOBBER_SCALE, BOBBER_SCALE);
      poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

      MatrixStack.Entry entry = poseStack.last();
      Matrix4f pose = entry.pose();
      Matrix3f normal = entry.normal();
      IVertexBuilder vb = buffers.getBuffer(RENDER_TYPE);

      // Draw a unit quad centered at origin
      vertex(vb, pose, normal, packedLight, 0.0F, 0, 0, 1);
      vertex(vb, pose, normal, packedLight, 1.0F, 0, 1, 1);
      vertex(vb, pose, normal, packedLight, 1.0F, 1, 1, 0);
      vertex(vb, pose, normal, packedLight, 0.0F, 1, 0, 0);

      poseStack.popPose();
   }

   // ------------------------ Line ------------------------
   private void renderFishingLine(FishingBobberEntity bobber, PlayerEntity angler, float partialTicks,
                                  MatrixStack poseStack, IRenderTypeBuffer buffers) {
      // Compute hand side (which side the line originates from)
      int handSign = angler.getMainArm() == HandSide.RIGHT ? 1 : -1;
      ItemStack mainHand = angler.getMainHandItem();
      if (mainHand.getItem() != Items.FISHING_ROD) {
         handSign = -handSign;
      }

      // Start point at the angler's hand (in world space)
      HandAnchor anchor = computeHandAnchor(angler, handSign, partialTicks);

      // End point at the bobber (in world space)
      double bobX = MathHelper.lerp(partialTicks, bobber.xo, bobber.getX());
      double bobY = MathHelper.lerp(partialTicks, bobber.yo, bobber.getY()) + 0.25D;
      double bobZ = MathHelper.lerp(partialTicks, bobber.zo, bobber.getZ());

      // Vector from bobber to hand
      float dx = (float) (anchor.x - bobX);
      float dy = (float) (anchor.y - bobY) + anchor.yOffset;
      float dz = (float) (anchor.z - bobZ);

      IVertexBuilder lineBuilder = buffers.getBuffer(RenderType.lines());
      Matrix4f currentPose = poseStack.last().pose();

      for (int seg = 0; seg < (int) LINE_SEGMENTS; ++seg) {
         stringVertex(dx, dy, dz, lineBuilder, currentPose, fraction(seg, LINE_SEGMENTS));
         stringVertex(dx, dy, dz, lineBuilder, currentPose, fraction(seg + 1, LINE_SEGMENTS));
      }
   }

   private static class HandAnchor {
      final double x, y, z; // world-space position of the line start
      final float yOffset;  // crouch/pose-dependent offset applied later in curve
      HandAnchor(double x, double y, double z, float yOffset) {
         this.x = x; this.y = y; this.z = z; this.yOffset = yOffset;
      }
   }

   private HandAnchor computeHandAnchor(PlayerEntity angler, int handSign, float partialTicks) {
      // Camera-dependent handling (first vs third person)
      boolean isFirstPerson = (this.entityRenderDispatcher.options == null
              || this.entityRenderDispatcher.options.getCameraType().isFirstPerson())
              && angler == Minecraft.getInstance().player;

      float bodyRot = MathHelper.lerp(partialTicks, angler.yBodyRotO, angler.yBodyRot) * ((float) Math.PI / 180F);
      double sin = MathHelper.sin(bodyRot);
      double cos = MathHelper.cos(bodyRot);

      double x, y, z;
      float extraYOffset;

      if (isFirstPerson) {
         double fovScale = this.entityRenderDispatcher.options.fov * FIRST_PERSON_FOV_SCALE;
         Vector3d offset = new Vector3d(handSign * -0.36D * fovScale, -0.045D * fovScale, 0.4D);

         offset = offset.xRot(-MathHelper.lerp(partialTicks, angler.xRotO, angler.xRot) * ((float) Math.PI / 180F));
         offset = offset.yRot(-MathHelper.lerp(partialTicks, angler.yRotO, angler.yRot) * ((float) Math.PI / 180F));

         float attack = angler.getAttackAnim(partialTicks);
         float swing = MathHelper.sin(MathHelper.sqrt(attack) * (float) Math.PI);
         offset = offset.yRot(swing * 0.5F).xRot(-swing * 0.7F);

         x = MathHelper.lerp(partialTicks, angler.xo, angler.getX()) + offset.x;
         y = MathHelper.lerp(partialTicks, angler.yo, angler.getY()) + offset.y;
         z = MathHelper.lerp(partialTicks, angler.zo, angler.getZ()) + offset.z;
         extraYOffset = angler.getEyeHeight();
      } else {
         // Third person: place near hand/shoulder
         double side = handSign * LINE_OFFSET_SIDE;
         x = MathHelper.lerp(partialTicks, angler.xo, angler.getX()) - cos * side - sin * LINE_OFFSET_BACK;
         y = angler.yo + angler.getEyeHeight() + (angler.getY() - angler.yo) * partialTicks - 0.45D;
         z = MathHelper.lerp(partialTicks, angler.zo, angler.getZ()) - sin * side + cos * LINE_OFFSET_BACK;
         extraYOffset = angler.isCrouching() ? -0.1875F : 0.0F;
      }

      return new HandAnchor(x, y, z, extraYOffset);
   }

   // ---------------------- Utilities ----------------------
   private static float fraction(int idx, float total) {
      return idx / total;
   }

   private static void vertex(IVertexBuilder vb, Matrix4f pose, Matrix3f normal, int light,
                              float u, int vY, int uTex, int vTex) {
      vb.vertex(pose, u - 0.5F, (float) vY - 0.5F, 0.0F)
              .color(255, 255, 255, 255)
              .uv((float) uTex, (float) vTex)
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(light)
              .normal(normal, 0.0F, 1.0F, 0.0F)
              .endVertex();
   }

   private static void stringVertex(float dx, float dy, float dz, IVertexBuilder vb, Matrix4f pose, float t) {
      // Quadratic-ish sag: y uses t^2 + t for a slight curve
      vb.vertex(pose,
                      dx * t,
                      dy * (t * t + t) * 0.5F + 0.25F,
                      dz * t)
              .color(0, 0, 0, 255)
              .endVertex();
   }

   @Override
   public ResourceLocation getTextureLocation(FishingBobberEntity entity) {
      return TEXTURE_LOCATION;
   }
}
