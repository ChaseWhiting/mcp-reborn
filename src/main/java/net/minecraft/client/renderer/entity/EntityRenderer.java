package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
   protected final EntityRendererManager entityRenderDispatcher;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;
   protected boolean output = false;
   private Minecraft minecraft = Minecraft.getInstance();

   @Nullable
   public List<EntityRenderState.LeashState> leashStates;

   protected AxisAlignedBB getBoundingBoxForCulling(T t) {
      return ((Entity)t).getBoundingBox();
   }


   protected EntityRenderer(EntityRendererManager p_i46179_1_) {
      this.entityRenderDispatcher = p_i46179_1_;
   }

   public final int getPackedLightCoords(T p_229100_1_, float p_229100_2_) {
      BlockPos blockpos = new BlockPos(p_229100_1_.getLightProbePosition(p_229100_2_));
      return LightTexture.pack(this.getBlockLightLevel(p_229100_1_, blockpos), this.getSkyLightLevel(p_229100_1_, blockpos));
   }

   protected int getSkyLightLevel(T p_239381_1_, BlockPos p_239381_2_) {
      return p_239381_1_.level.getBrightness(LightType.SKY, p_239381_2_);
   }

   protected int getBlockLightLevel(T p_225624_1_, BlockPos p_225624_2_) {
      return p_225624_1_.isOnFire() ? 15 : p_225624_1_.level.getBrightness(LightType.BLOCK, p_225624_2_);
   }

   public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (!p_225626_1_.shouldRender(p_225626_3_, p_225626_5_, p_225626_7_)) {
         return false;
      } else if (p_225626_1_.noCulling) {
         return true;
      } else {
         AxisAlignedBB axisalignedbb = getBoundingBoxForCulling(p_225626_1_).inflate(0.5D);
         if (axisalignedbb.hasNaN() || axisalignedbb.getSize() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(p_225626_1_.getX() - 2.0D, p_225626_1_.getY() - 2.0D, p_225626_1_.getZ() - 2.0D, p_225626_1_.getX() + 2.0D, p_225626_1_.getY() + 2.0D, p_225626_1_.getZ() + 2.0D);
         }

         return p_225626_2_.isVisible(axisalignedbb);
      }
   }

   public Vector3d getRenderOffset(T p_225627_1_, float p_225627_2_) {
      return Vector3d.ZERO;
   }

   public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      if (this.shouldShowName(entity)) {
         this.renderNameTag(entity, entity.getDisplayName(), matrixStack, buffer, packedLight);
      }
      if(entity.isAlive() && entity instanceof LivingEntity)
         this.renderHealth(entity, entity.getDisplayName(), matrixStack, buffer, packedLight);


      this.addLeashStates(entity, partialTicks);

      if (this.leashStates != null) {
         for (EntityRenderState.LeashState leashState : leashStates) {
            renderLeash(matrixStack, buffer, leashState);
         }
      }

//       if (entity instanceof Leashable leashable) {
//           Entity entity1 = leashable.getLeashHolder();
//           if (entity1 != null) {
//              this.renderLeash(entity, partialTicks, matrixStack, buffer, entity1);
//           }
//       }
   }

   private <E extends Entity> void renderLeash(T p_229118_1_, float p_229118_2_, MatrixStack p_229118_3_, IRenderTypeBuffer p_229118_4_, E p_229118_5_) {
      p_229118_3_.pushPose();

      float yBodyRot = p_229118_1_ instanceof LivingEntity livingEntity ? livingEntity.yBodyRot : p_229118_1_.yRot;
      float yBodyRotO = p_229118_1_ instanceof LivingEntity livingEntity ? livingEntity.yBodyRotO : p_229118_1_.yRotO;

      Vector3d vector3d = p_229118_5_.getRopeHoldPosition(p_229118_2_);
      double d0 = (double)(MathHelper.lerp(p_229118_2_, yBodyRot, yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
      Vector3d vector3d1 = p_229118_1_.getLeashOffset();
      double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
      double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
      double d3 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.xo, p_229118_1_.getX()) + d1;
      double d4 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.yo, p_229118_1_.getY()) + vector3d1.y;
      double d5 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.zo, p_229118_1_.getZ()) + d2;
      p_229118_3_.translate(d1, vector3d1.y, d2);
      float f = (float)(vector3d.x - d3);
      float f1 = (float)(vector3d.y - d4);
      float f2 = (float)(vector3d.z - d5);
      float f3 = 0.05F;
      IVertexBuilder ivertexbuilder = p_229118_4_.getBuffer(RenderType.leash());
      Matrix4f matrix4f = p_229118_3_.last().pose();
      float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.05F / 2.0F;
      float f5 = f2 * f4;
      float f6 = f * f4;
      BlockPos blockpos = new BlockPos(p_229118_1_.getEyePosition(p_229118_2_));
      BlockPos blockpos1 = new BlockPos(p_229118_5_.getEyePosition(p_229118_2_));
      int i = this.getBlockLightLevel(p_229118_1_, blockpos);
      int j = this.entityRenderDispatcher.getRenderer(p_229118_5_).getBlockLightLevel(p_229118_5_, blockpos1);
      int k = p_229118_1_.level.getBrightness(LightType.SKY, blockpos);
      int l = p_229118_1_.level.getBrightness(LightType.SKY, blockpos1);
      renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.05F, 0.05F, f5, f6);
      renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.05F, 0.0F, f5, f6);
      p_229118_3_.popPose();
   }

   public static void renderSide(IVertexBuilder p_229119_0_, Matrix4f p_229119_1_, float p_229119_2_, float p_229119_3_, float p_229119_4_, int p_229119_5_, int p_229119_6_, int p_229119_7_, int p_229119_8_, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
      int i = 24;

      for(int j = 0; j < 24; ++j) {
         float f = (float)j / 23.0F;
         int k = (int)MathHelper.lerp(f, (float)p_229119_5_, (float)p_229119_6_);
         int l = (int)MathHelper.lerp(f, (float)p_229119_7_, (float)p_229119_8_);
         int i1 = LightTexture.pack(k, l);
         addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
         addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
      }

   }

   public static void addVertexPair(IVertexBuilder p_229120_0_, Matrix4f p_229120_1_, int p_229120_2_, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
      float f = 0.5F;
      float f1 = 0.4F;
      float f2 = 0.3F;
      if (p_229120_9_ % 2 == 0) {
         f *= 0.7F;
         f1 *= 0.7F;
         f2 *= 0.7F;
      }

      float f3 = (float)p_229120_9_ / (float)p_229120_8_;
      float f4 = p_229120_3_ * f3;
      float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
      float f6 = p_229120_5_ * f3;
      if (!p_229120_10_) {
         p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      }

      p_229120_0_.vertex(p_229120_1_, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      if (p_229120_10_) {
         p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      }

   }

   private static void renderLeash(MatrixStack matrixStack, IRenderTypeBuffer bufferSource, EntityRenderState.LeashState leashState) {
      float dx = (float)(leashState.end.x - leashState.start.x);
      float dy = (float)(leashState.end.y - leashState.start.y);
      float dz = (float)(leashState.end.z - leashState.start.z);

      float horizontalScale = MathHelper.invSqrt(dx * dx + dz * dz) * 0.05f / 2.0f;
      float offsetX = dz * horizontalScale;
      float offsetZ = dx * horizontalScale;

      matrixStack.pushPose();
      matrixStack.translate(leashState.offset);

      IVertexBuilder vertexConsumer = bufferSource.getBuffer(RenderType.leash());
      Matrix4f matrix = matrixStack.last().pose();

      int segments = 24;
      for (int i = 0; i < segments; ++i) {
         float t0 = (float)i / segments;
         float t1 = (float)(i + 1) / segments;

         int blockLight0 = (int)MathHelper.lerp(t0, leashState.startBlockLight, leashState.endBlockLight);
         int skyLight0 = (int)MathHelper.lerp(t0, leashState.startSkyLight, leashState.endSkyLight);
         int light0 = LightTexture.pack(blockLight0, skyLight0);

         int blockLight1 = (int)MathHelper.lerp(t1, leashState.startBlockLight, leashState.endBlockLight);
         int skyLight1 = (int)MathHelper.lerp(t1, leashState.startSkyLight, leashState.endSkyLight);
         int light1 = LightTexture.pack(blockLight1, skyLight1);

         float band0 = i % 2 == 0 ? 0.7f : 1.0f;
         float r0 = 0.5f * band0, g0 = 0.4f * band0, b0 = 0.3f * band0;
         float band1 = (i + 1) % 2 == 0 ? 0.7f : 1.0f;
         float r1 = 0.5f * band1, g1 = 0.4f * band1, b1 = 0.3f * band1;

         float x0 = dx * t0;
         float y0 = leashState.slack
                 ? (dy > 0.0f ? dy * t0 * t0 : dy - dy * (1.0f - t0) * (1.0f - t0))
                 : dy * t0;
         float z0 = dz * t0;

         float x1 = dx * t1;
         float y1 = leashState.slack
                 ? (dy > 0.0f ? dy * t1 * t1 : dy - dy * (1.0f - t1) * (1.0f - t1))
                 : dy * t1;
         float z1 = dz * t1;


         float tl_x = x0 - offsetX;
         float tl_y = y0 + 0.05f;
         float tl_z = z0 + offsetZ;
         float tr_x = x0 + offsetX;
         float tr_y = y0 + 0.0f;
         float tr_z = z0 - offsetZ;
         float br_x = x1 + offsetX;
         float br_y = y1 + 0.0f;
         float br_z = z1 - offsetZ;
         float bl_x = x1 - offsetX;
         float bl_y = y1 + 0.05f;
         float bl_z = z1 + offsetZ;

         vertexConsumer.vertex(matrix, tl_x, tl_y, tl_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, tr_x, tr_y, tr_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, br_x, br_y, br_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();

         vertexConsumer.vertex(matrix, tl_x, tl_y, tl_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, br_x, br_y, br_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();
         vertexConsumer.vertex(matrix, bl_x, bl_y, bl_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();

// Compute perpendicular offset (90° rotated)
         float perpOffsetX = -offsetZ;
         float perpOffsetZ = offsetX;

// Recalculate corner positions using the perpendicular offset
         float pt_tl_x = x0 - perpOffsetX;
         float pt_tl_y = y0 + 0.05f;
         float pt_tl_z = z0 + perpOffsetZ;
         float pt_tr_x = x0 + perpOffsetX;
         float pt_tr_y = y0 + 0.0f;
         float pt_tr_z = z0 - perpOffsetZ;
         float pt_br_x = x1 + perpOffsetX;
         float pt_br_y = y1 + 0.0f;
         float pt_br_z = z1 - perpOffsetZ;
         float pt_bl_x = x1 - perpOffsetX;
         float pt_bl_y = y1 + 0.05f;
         float pt_bl_z = z1 + perpOffsetZ;

// Draw the second ribbon (perpendicular to the first)
         vertexConsumer.vertex(matrix, pt_tl_x, pt_tl_y, pt_tl_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, pt_tr_x, pt_tr_y, pt_tr_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, pt_br_x, pt_br_y, pt_br_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();

         vertexConsumer.vertex(matrix, pt_tl_x, pt_tl_y, pt_tl_z).color(r0, g0, b0, 1.0f).uv2(light0).endVertex();
         vertexConsumer.vertex(matrix, pt_br_x, pt_br_y, pt_br_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();
         vertexConsumer.vertex(matrix, pt_bl_x, pt_bl_y, pt_bl_z).color(r1, g1, b1, 1.0f).uv2(light1).endVertex();

      }


      matrixStack.popPose();
   }



   private static void addVertexPair(IVertexBuilder vertexConsumer, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, int n, boolean bl,
                                     EntityRenderState.LeashState leashState) {

      float f8 = (float)n / 24.0f;
      int n2 = (int)MathHelper.lerp(f8, leashState.startBlockLight, leashState.endBlockLight);
      int n3 = (int)MathHelper.lerp(f8, leashState.startSkyLight, leashState.endSkyLight);
      int n4 = LightTexture.pack(n2, n3);
      float f9 = n % 2 == (bl ? 1 : 0) ? 0.7f : 1.0f;
      float f10 = 0.5f * f9;
      float f11 = 0.4f * f9;
      float f12 = 0.3f * f9;
      float f13 = f * f8;
      float f14 = leashState.slack ? (f2 > 0.0f ? f2 * f8 * f8 : f2 - f2 * (1.0f - f8) * (1.0f - f8)) : f2 * f8;
      float f15 = f3 * f8;

      vertexConsumer.vertex(matrix4f, f13 - f6, f14 + f5, f15 + f7).color(f10, f11, f12, 1.0f).setLight(n4).endVertex();
      vertexConsumer.vertex(matrix4f, f13 + f6, f14 + f4 - f5, f15 - f7).color(f10, f11, f12, 1.0f).setLight(n4).endVertex();
   }



   public void addLeashStates(T t, float f) {
      Entity entity = (Entity) t;
      if (t instanceof Leashable leashable) {
         Entity holder = leashable.getLeashHolder();
         if (holder != null) {
            float bodyRot = entity.getPreciseBodyRotation(f) * ((float) Math.PI / 180);
            Vector3d leashOffset = leashable.getLeashOffset(f);
            BlockPos pos = BlockPos.containing(entity.getEyePosition(f));
            BlockPos holderPos = BlockPos.containing(holder.getEyePosition(f));
            int blockLight = this.getBlockLightLevel(t, pos);
            //int holderBlockLight = this.entityRenderDispatcher.getRenderer(holder).getBlockLightLevel(holder, holderPos);
            int holderBlockLight = this.getBlockLightLevel(t, holderPos);
            int skyLight = entity.level().getBrightness(LightType.SKY, pos);
            int holderSkyLight = entity.level().getBrightness(LightType.SKY, holderPos);

            boolean quad = holder.supportQuadLeashAsHolder() && leashable.supportQuadLeash();
            int count = quad ? 4 : 1;

            if (leashStates == null || leashStates.size() != count) {
               leashStates = new ArrayList<>(count);
               for (int i = 0; i < count; ++i) {
                  leashStates.add(new EntityRenderState.LeashState());
               }
            }

            if (quad) {
               float holderRot = holder.getPreciseBodyRotation(f) * ((float) Math.PI / 180);
               Vector3d holderPosVec = holder.getPosition(f);
               Vector3d[] leashOffsets = leashable.getQuadLeashOffsets();
               Vector3d[] holderOffsets = holder.getQuadLeashHolderOffsets();

               for (int i = 0; i < count; ++i) {
                  var leashState = leashStates.get(i);
                  leashState.offset = leashOffsets[i].yRot(-bodyRot);
                  leashState.start = entity.getPosition(f).add(leashState.offset);
                  leashState.end = holderPosVec.add(holderOffsets[i].yRot(-holderRot));
                  leashState.startBlockLight = blockLight;
                  leashState.endBlockLight = holderBlockLight;
                  leashState.startSkyLight = skyLight;
                  leashState.endSkyLight = holderSkyLight;
                  leashState.slack = false;
               }
            } else {
               Vector3d offset = leashOffset.yRot(-bodyRot);
               EntityRenderState.LeashState leashState = leashStates.get(0);
               leashState.offset = offset;
               leashState.start = entity.getPosition(f).add(offset);
               leashState.end = holder.getRopeHoldPosition(f);
               leashState.startBlockLight = blockLight;
               leashState.endBlockLight = holderBlockLight;
               leashState.startSkyLight = skyLight;
               leashState.endSkyLight = holderSkyLight;
            }
         } else {
            leashStates = null;
         }
      } else {
         leashStates = null;
      }
   }

   protected boolean shouldShowName(T entity) {
      return entity.shouldShowName() && entity.hasCustomName();
   }

   public abstract ResourceLocation getTextureLocation(T entity);

   public FontRenderer getFont() {
      return this.entityRenderDispatcher.getFont();
   }

   protected void renderNameTag(T entity, ITextComponent displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      double distanceSquared = this.entityRenderDispatcher.distanceToSqr(entity);
      if (!(distanceSquared > 4096.0D)) {
         boolean isDiscrete = !entity.isDiscrete();
         float yOffset = entity.getNameTagOffsetY();
         int yOffsetAdjustment = "deadmau5".equals(displayName.getString()) ? -10 : 0;
         matrixStack.pushPose();
         matrixStack.translate(0.0D, (double) yOffset, 0.0D);
         matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
         matrixStack.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = matrixStack.last().pose();
         float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
         FontRenderer fontRenderer = this.getFont();
         float textWidth = (float) (-fontRenderer.width(displayName) / 2);
         fontRenderer.drawInBatch(displayName, textWidth, (float) yOffsetAdjustment, 553648127, false, matrix4f, buffer, isDiscrete, backgroundColor, packedLight);
         if (isDiscrete) {
            fontRenderer.drawInBatch(displayName, textWidth, (float) yOffsetAdjustment, -1, false, matrix4f, buffer, false, 0, packedLight);
         }

//         // Displaying health
//         String healthText = String.format("Health: %.1f / %.1f", entity.getHealth(), entity.getMaxHealth());
//         int healthYOffset = yOffsetAdjustment + -15; // Adjust as needed
//         float healthTextWidth = (float) (-fontRenderer.width(healthText) / 2);
//         fontRenderer.drawInBatch(healthText, healthTextWidth, (float) healthYOffset, 553648127, false, matrix4f, buffer, isDiscrete, backgroundColor, packedLight);
//         if (isDiscrete) {
//            fontRenderer.drawInBatch(healthText, healthTextWidth, (float) healthYOffset, -1, false, matrix4f, buffer, false, 0, packedLight);
//         }

         matrixStack.popPose();
      }
   }

   public void extractAndSaveTexture(T entity) {
      // Get the texture location from the entity
      ResourceLocation textureLocation = getTextureLocation(entity);

      // Get the resource manager from Minecraft instance
      IResourceManager resourceManager = minecraft.getResourceManager();

      // Get the resource (texture) as an input stream
      try (IResource resource = resourceManager.getResource(textureLocation)) {
         InputStream inputStream = resource.getInputStream();
         String textureFileName = textureLocation.getPath().substring(textureLocation.getPath().lastIndexOf('/') + 1);

         // Define the output file path
         File outputFile = new File("C:\\Users\\infow\\Downloads\\texture\\" + textureFileName);

         // Ensure the directories exist
         Files.createDirectories(outputFile.toPath().getParent());

         // Write the texture to the specified output file
         try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            IOUtils.copy(inputStream, outputStream);
            System.out.println("Texture saved to " + outputFile.getAbsolutePath());
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   protected void renderHealth(T entity, ITextComponent displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
//      if (!output) {
//         extractAndSaveTexture(entity);
//         output = true;
//      }
      double distanceSquared = this.entityRenderDispatcher.distanceToSqr(entity);
      double distance = Math.sqrt(distanceSquared);

      List<Entity> entities = entity.level.getEntitiesOfClass(
              Entity.class,
              entity.getBoundingBox().inflate(1D, 1D, 1D),
              entity1 -> entity1 != entity
                      && !(entity1 instanceof PlayerEntity)
                      && !(entity1 instanceof ItemEntity)
                      && !(entity1 instanceof AbstractArrowEntity)
                      && !(entity1 instanceof ArmorStandEntity)
                      && !(entity1 instanceof AbstractMinecartEntity)
                      && !(entity1 instanceof ExperienceOrbEntity)
                      && !(entity1 instanceof FishingBobberEntity)
                      && !(entity1 instanceof BoatEntity)
                      && !(entity1 instanceof HangingEntity)
                      && !(entity1 instanceof EnderDragonPartEntity)
                      && !(entity1 instanceof LightningBoltEntity));
      ITextComponent maxHealthComponent = new TranslationTextComponent("attribute.name.generic.max_health");

      if (minecraft.options.showEntityHealth() && !minecraft.options.hideGui && entities.isEmpty() && !(distance > 8.5D) && !(entity instanceof CreakingEntity) && !(entity instanceof WitherEntity) && !(entity instanceof EnderDragonEntity) && entity != minecraft.player.getVehicle() && !displayName.getString().equals(minecraft.getUser().getName())) {
         if (entity.isInvisible()) return;
         boolean isDiscrete = !entity.isDiscrete();
         FontRenderer fontRenderer = this.getFont();
         float yOffset = entity.getBbHeight() + 0.5F;


         matrixStack.pushPose();
         matrixStack.translate(0.0D, (double) yOffset, 0.0D);
         matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
         matrixStack.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = matrixStack.last().pose();


         int yOffsetAdjustment = "deadmau5".equals(displayName.getString()) ? -10 : 0;
         String healthText = String.format("%s: %.1f / %.1f", maxHealthComponent.getString(), entity.getHealth(), entity.getMaxHealth());

         int healthYOffset = yOffsetAdjustment + (shouldShowName(entity) ? -15 : 0);
         float healthTextWidth = (float) (-fontRenderer.width(healthText) / 2);

         float backgroundOpacity = minecraft.options.getBackgroundOpacity(0.25F);
         int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

         fontRenderer.drawInBatch(healthText, healthTextWidth, (float) healthYOffset, 553648127, false, matrix4f, buffer, isDiscrete, backgroundColor, packedLight);
         if (isDiscrete) {
            fontRenderer.drawInBatch(healthText, healthTextWidth, (float) healthYOffset, -1, false, matrix4f, buffer, false, 0, packedLight);
         }
         matrixStack.popPose();
      }
   }

   public EntityRendererManager getDispatcher() {
      return this.entityRenderDispatcher;
   }
}