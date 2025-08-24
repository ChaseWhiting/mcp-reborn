package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public class LookController {
   protected final Mob mob;
   protected float yMaxRotSpeed;
   protected float xMaxRotAngle;
   protected boolean hasWanted;
   protected int lookAtCooldown;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;

   public LookController(Mob p_i1613_1_) {
      this.mob = p_i1613_1_;
   }

   public void setLookAt(Vector3d p_220674_1_) {
      this.setLookAt(p_220674_1_.x, p_220674_1_.y, p_220674_1_.z);
   }

   public void setLookAt(BlockPos pos) {
      this.setLookAt(pos.asVector());
   }

   public void setLookAt(Entity targetEntity, float p_75651_2_, float p_75651_3_) {
      if (targetEntity == null) return;
      this.setLookAt(targetEntity.getX(), getWantedY(targetEntity), targetEntity.getZ(), p_75651_2_, p_75651_3_);
   }

   public void setLookAt(double p_220679_1_, double p_220679_3_, double p_220679_5_) {
      this.setLookAt(p_220679_1_, p_220679_3_, p_220679_5_, (float)this.mob.getHeadRotSpeed(), (float)this.mob.getMaxHeadXRot());
   }

   public void setLookAt(double p_75650_1_, double p_75650_3_, double p_75650_5_, float p_75650_7_, float p_75650_8_) {
      this.wantedX = p_75650_1_;
      this.wantedY = p_75650_3_;
      this.wantedZ = p_75650_5_;
      this.yMaxRotSpeed = p_75650_7_;
      this.xMaxRotAngle = p_75650_8_;
      this.hasWanted = true;
      this.lookAtCooldown = 2;

   }

   public void tick() {
      if (this.resetXRotOnTick()) {
         this.mob.xRot = (0.0f);
      }
      if (this.lookAtCooldown > 0) {
         --this.lookAtCooldown;
         this.getYRotDOptional().ifPresent(f -> {
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, f.floatValue(), this.yMaxRotSpeed);
         });
         this.getXRotDOptional().ifPresent(f -> this.mob.xRot = (this.rotateTowards(this.mob.xRot, f.floatValue(), this.xMaxRotAngle)));
      } else {
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0f);
      }
      this.clampHeadRotationToBody();
   }

   protected void clampHeadRotationToBody() {
      if (!this.mob.getNavigation().isDone()) {
         this.mob.yHeadRot = MathHelper.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, this.mob.getMaxHeadYRot());
      }
   }

   protected boolean resetXRotOnTick() {
      return true;
   }

   public boolean isHasWanted() {
      return this.hasWanted;
   }

   public boolean isLookingAtTarget() {
      return this.lookAtCooldown > 0;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   protected float getXRotD() {
      double d0 = this.wantedX - this.mob.getX();
      double d1 = this.wantedY - this.mob.getEyeY();
      double d2 = this.wantedZ - this.mob.getZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      return (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
   }

   protected float getYRotD() {
      double d0 = this.wantedX - this.mob.getX();
      double d1 = this.wantedZ - this.mob.getZ();
      return (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
   }

   protected float rotateTowards(float p_220675_1_, float p_220675_2_, float p_220675_3_) {
      float f = MathHelper.degreesDifference(p_220675_1_, p_220675_2_);
      float f1 = MathHelper.clamp(f, -p_220675_3_, p_220675_3_);
      return p_220675_1_ + f1;
   }

   private static double getWantedY(Entity p_220676_0_) {
      return p_220676_0_ instanceof LivingEntity ? p_220676_0_.getEyeY() : (p_220676_0_.getBoundingBox().minY + p_220676_0_.getBoundingBox().maxY) / 2.0D;
   }

   protected Optional<Float> getXRotDOptional() {
      double d = this.wantedX - this.mob.getX();
      double d2 = this.wantedY - this.mob.getEyeY();
      double d3 = this.wantedZ - this.mob.getZ();
      double d4 = Math.sqrt(d * d + d3 * d3);
      return Math.abs(d2) > (double)1.0E-5f || Math.abs(d4) > (double)1.0E-5f ? Optional.of(Float.valueOf((float)(-(MathHelper.atan2(d2, d4) * 57.2957763671875)))) : Optional.empty();
   }

   protected Optional<Float> getYRotDOptional() {
      double d = this.wantedX - this.mob.getX();
      double d2 = this.wantedZ - this.mob.getZ();
      return Math.abs(d2) > (double)1.0E-5f || Math.abs(d) > (double)1.0E-5f ? Optional.of(Float.valueOf((float)(MathHelper.atan2(d2, d) * 57.2957763671875) - 90.0f)) : Optional.empty();
   }
}