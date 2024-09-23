package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
   protected final EntityRendererManager entityRenderDispatcher;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;
   protected boolean output = false;
   private Minecraft minecraft = Minecraft.getInstance();

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
         AxisAlignedBB axisalignedbb = p_225626_1_.getBoundingBoxForCulling().inflate(0.5D);
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
         float yOffset = entity.getBbHeight() + 0.5F;
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

      if (minecraft.options.showEntityHealth() && !minecraft.options.hideGui && entities.isEmpty() && !(distance > 8.5D) && !(entity instanceof WitherEntity) && !(entity instanceof EnderDragonEntity) && !displayName.getString().equals(minecraft.getUser().getName())) {
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