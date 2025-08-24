package net.minecraft.client.renderer.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHelper {
   public static void animateCrossbowHold(ModelRenderer p_239104_0_, ModelRenderer p_239104_1_, ModelRenderer p_239104_2_, boolean p_239104_3_) {
      ModelRenderer modelrenderer = p_239104_3_ ? p_239104_0_ : p_239104_1_;
      ModelRenderer modelrenderer1 = p_239104_3_ ? p_239104_1_ : p_239104_0_;
      modelrenderer.yRot = (p_239104_3_ ? -0.3F : 0.3F) + p_239104_2_.yRot;
      modelrenderer1.yRot = (p_239104_3_ ? 0.6F : -0.6F) + p_239104_2_.yRot;
      modelrenderer.xRot = (-(float)Math.PI / 2F) + p_239104_2_.xRot + 0.1F;
      modelrenderer1.xRot = -1.5F + p_239104_2_.xRot;
   }

   public static void animateCrossbowHold(ModelPart p_239104_0_, ModelPart p_239104_1_, ModelPart p_239104_2_, boolean p_239104_3_) {
      ModelPart modelrenderer = p_239104_3_ ? p_239104_0_ : p_239104_1_;
      ModelPart modelrenderer1 = p_239104_3_ ? p_239104_1_ : p_239104_0_;
      modelrenderer.yRot = (p_239104_3_ ? -0.3F : 0.3F) + p_239104_2_.yRot;
      modelrenderer1.yRot = (p_239104_3_ ? 0.6F : -0.6F) + p_239104_2_.yRot;
      modelrenderer.xRot = (-(float)Math.PI / 2F) + p_239104_2_.xRot + 0.1F;
      modelrenderer1.xRot = -1.5F + p_239104_2_.xRot;
   }

   public static void animateCrossbowCharge(ModelRenderer p_239102_0_, ModelRenderer p_239102_1_, LivingEntity p_239102_2_, boolean p_239102_3_) {
      ModelRenderer modelrenderer = p_239102_3_ ? p_239102_0_ : p_239102_1_;
      ModelRenderer modelrenderer1 = p_239102_3_ ? p_239102_1_ : p_239102_0_;
      modelrenderer.yRot = p_239102_3_ ? -0.8F : 0.8F;
      modelrenderer.xRot = -0.97079635F;
      modelrenderer1.xRot = modelrenderer.xRot;
      float f = (float)CrossbowItem.getChargeDuration(p_239102_2_.getUseItem());
      float f1 = MathHelper.clamp((float)p_239102_2_.getTicksUsingItem(), 0.0F, f);
      float f2 = f1 / f;
      modelrenderer1.yRot = MathHelper.lerp(f2, 0.4F, 0.85F) * (float)(p_239102_3_ ? 1 : -1);
      modelrenderer1.xRot = MathHelper.lerp(f2, modelrenderer1.xRot, (-(float)Math.PI / 2F));
   }

   public static void animateCrossbowCharge(ModelPart p_239102_0_, ModelPart p_239102_1_, LivingEntity p_239102_2_, boolean p_239102_3_) {
      ModelPart modelrenderer = p_239102_3_ ? p_239102_0_ : p_239102_1_;
      ModelPart modelrenderer1 = p_239102_3_ ? p_239102_1_ : p_239102_0_;
      modelrenderer.yRot = p_239102_3_ ? -0.8F : 0.8F;
      modelrenderer.xRot = -0.97079635F;
      modelrenderer1.xRot = modelrenderer.xRot;
      float f = (float)CrossbowItem.getChargeDuration(p_239102_2_.getUseItem());
      float f1 = MathHelper.clamp((float)p_239102_2_.getTicksUsingItem(), 0.0F, f);
      float f2 = f1 / f;
      modelrenderer1.yRot = MathHelper.lerp(f2, 0.4F, 0.85F) * (float)(p_239102_3_ ? 1 : -1);
      modelrenderer1.xRot = MathHelper.lerp(f2, modelrenderer1.xRot, (-(float)Math.PI / 2F));
   }

   public static <T extends Mob> void swingWeaponDown(ModelRenderer p_239103_0_, ModelRenderer p_239103_1_, T p_239103_2_, float p_239103_3_, float p_239103_4_) {
      float f = MathHelper.sin(p_239103_3_ * (float)Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - p_239103_3_) * (1.0F - p_239103_3_)) * (float)Math.PI);
      p_239103_0_.zRot = 0.0F;
      p_239103_1_.zRot = 0.0F;
      p_239103_0_.yRot = 0.15707964F;
      p_239103_1_.yRot = -0.15707964F;
      if (p_239103_2_.getMainArm() == HandSide.RIGHT) {
         p_239103_0_.xRot = -1.8849558F + MathHelper.cos(p_239103_4_ * 0.09F) * 0.15F;
         p_239103_1_.xRot = -0.0F + MathHelper.cos(p_239103_4_ * 0.19F) * 0.5F;
         p_239103_0_.xRot += f * 2.2F - f1 * 0.4F;
         p_239103_1_.xRot += f * 1.2F - f1 * 0.4F;
      } else {
         p_239103_0_.xRot = -0.0F + MathHelper.cos(p_239103_4_ * 0.19F) * 0.5F;
         p_239103_1_.xRot = -1.8849558F + MathHelper.cos(p_239103_4_ * 0.09F) * 0.15F;
         p_239103_0_.xRot += f * 1.2F - f1 * 0.4F;
         p_239103_1_.xRot += f * 2.2F - f1 * 0.4F;
      }

      bobArms(p_239103_0_, p_239103_1_, p_239103_4_);
   }

   public static void bobArms(ModelRenderer p_239101_0_, ModelRenderer p_239101_1_, float p_239101_2_) {
      p_239101_0_.zRot += MathHelper.cos(p_239101_2_ * 0.09F) * 0.05F + 0.05F;
      p_239101_1_.zRot -= MathHelper.cos(p_239101_2_ * 0.09F) * 0.05F + 0.05F;
      p_239101_0_.xRot += MathHelper.sin(p_239101_2_ * 0.067F) * 0.05F;
      p_239101_1_.xRot -= MathHelper.sin(p_239101_2_ * 0.067F) * 0.05F;
   }

   public static void bobModelPart(ModelRenderer modelPart, float f, float f2) {
      modelPart.zRot += f2 * (MathHelper.cos(f * 0.09f) * 0.05f + 0.05f);
      modelPart.xRot += f2 * (MathHelper.sin(f * 0.067f) * 0.05f);
   }

   public static void bobModelPart(ModelPart modelPart, float f, float f2) {
      modelPart.zRot += f2 * (MathHelper.cos(f * 0.09f) * 0.05f + 0.05f);
      modelPart.xRot += f2 * (MathHelper.sin(f * 0.067f) * 0.05f);
   }

   public static void animateZombieArms(ModelRenderer p_239105_0_, ModelRenderer p_239105_1_, boolean p_239105_2_, float p_239105_3_, float p_239105_4_) {
      float f = MathHelper.sin(p_239105_3_ * (float)Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - p_239105_3_) * (1.0F - p_239105_3_)) * (float)Math.PI);
      p_239105_1_.zRot = 0.0F;
      p_239105_0_.zRot = 0.0F;
      p_239105_1_.yRot = -(0.1F - f * 0.6F);
      p_239105_0_.yRot = 0.1F - f * 0.6F;
      float f2 = -(float)Math.PI / (p_239105_2_ ? 1.5F : 2.25F);
      p_239105_1_.xRot = f2;
      p_239105_0_.xRot = f2;
      p_239105_1_.xRot += f * 1.2F - f1 * 0.4F;
      p_239105_0_.xRot += f * 1.2F - f1 * 0.4F;
      bobArms(p_239105_1_, p_239105_0_, p_239105_4_);
   }

   public static void animateShieldHolding(ModelRenderer leftArm, ModelRenderer rightArm, boolean isLeftHanded, float attackTime, float partialTicks) {
      float swingProgress = MathHelper.sin(attackTime * (float)Math.PI);
      float swingProgressInverse = MathHelper.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float)Math.PI);

      rightArm.zRot = 0.0F;
      leftArm.zRot = 0.0F;

      if (isLeftHanded) {
         leftArm.yRot = -(0.1F - swingProgress * 0.6F);
         rightArm.yRot = 0.1F - swingProgress * 0.6F;
      } else {
         rightArm.yRot = -(0.1F - swingProgress * 0.6F);
         leftArm.yRot = 0.1F - swingProgress * 0.6F;
      }

      float armBaseRotation = -(float)Math.PI / 2.25F;
      rightArm.xRot = armBaseRotation;
      leftArm.xRot = armBaseRotation;

      if (isLeftHanded) {
         leftArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
         rightArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
      } else {
         rightArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
         leftArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
      }

      // Additional adjustments to better hold the shield
      if (isLeftHanded) {
         leftArm.xRot += 0.5F; // Adjust the arm to look more natural when holding a shield
         leftArm.zRot += 0.1F; // Slight tilt for better positioning
         leftArm.yRot += 0.6F; // Move arm inward
      } else {
         rightArm.xRot += 0.5F; // Adjust the arm to look more natural when holding a shield
         rightArm.zRot += 0.1F; // Slight tilt for better positioning
         rightArm.yRot -= 0.5F; // Move arm inward
      }

      bobArms(rightArm, leftArm, partialTicks);
   }

   public static void animateShieldHoldingNoHandItem(ModelRenderer leftArm, ModelRenderer rightArm, boolean isLeftHanded, float attackTime, float partialTicks) {
      float swingProgress = MathHelper.sin(attackTime * (float)Math.PI);
      float swingProgressInverse = MathHelper.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float)Math.PI);

      rightArm.zRot = 0.0F;
      leftArm.zRot = 0.0F;

      if (isLeftHanded) {
         leftArm.yRot = -(0.1F - swingProgress * 0.6F);
         rightArm.yRot = 0.1F - swingProgress * 0.6F;
      } else {
         rightArm.yRot = -(0.1F - swingProgress * 0.6F);
         leftArm.yRot = 0.1F - swingProgress * 0.6F;
      }

      float armBaseRotation = -(float)Math.PI / 2.25F;
      rightArm.xRot = armBaseRotation;
      leftArm.xRot = armBaseRotation;

      if (isLeftHanded) {
         leftArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
         rightArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
      } else {
         rightArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
         leftArm.xRot += swingProgress * 1.2F - swingProgressInverse * 0.4F;
      }

      // Additional adjustments to better hold the shield
      if (isLeftHanded) {
         leftArm.xRot += 0.5F; // Adjust the arm to look more natural when holding a shield
         leftArm.zRot += 0.1F; // Slight tilt for better positioning
         leftArm.yRot += 0.6F; // Move arm inward

         // Animate the right arm for attacking
         rightArm.xRot = armBaseRotation + (swingProgress * 1.2F - swingProgressInverse * 0.4F);
         rightArm.zRot = 0F;
         rightArm.yRot = 0.1F - swingProgress * 0.6F; // Slight rotation for attack animation
      } else {
         rightArm.xRot += 0.5F; // Adjust the arm to look more natural when holding a shield
         rightArm.zRot += 0.1F; // Slight tilt for better positioning
         rightArm.yRot += 0.5F; // Move arm inward

         // Animate the left arm for attacking
         leftArm.xRot = armBaseRotation + (swingProgress * 1.2F - swingProgressInverse * 0.4F);
         leftArm.zRot = 0F;
         leftArm.yRot = 0.1F - swingProgress * 0.6F; // Slight rotation for attack animation
      }

      bobArms(rightArm, leftArm, partialTicks);
   }
}