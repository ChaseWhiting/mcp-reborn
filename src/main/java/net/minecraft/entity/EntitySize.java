package net.minecraft.entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class EntitySize {
   public final float width;
   public final float height;
   public final boolean fixed;

   public final EntityAttachments attachments;
   public final float eyeHeight;



   public EntitySize(float f, float f2, boolean bl) {
      this(f, f2, EntitySize.defaultEyeHeight(f2), EntityAttachments.createDefault(f, f2), bl);
   }

   public AxisAlignedBB makeBoundingBox(Vector3d p_242286_1_) {
      return this.makeBoundingBox(p_242286_1_.x, p_242286_1_.y, p_242286_1_.z);
   }

   public AxisAlignedBB makeBoundingBox(double p_242285_1_, double p_242285_3_, double p_242285_5_) {
      float f = this.width / 2.0F;
      float f1 = this.height;
      return new AxisAlignedBB(p_242285_1_ - (double)f, p_242285_3_, p_242285_5_ - (double)f, p_242285_1_ + (double)f, p_242285_3_ + (double)f1, p_242285_5_ + (double)f);
   }



   public EntitySize scale(float f) {
      return this.scale(f, f);
   }

   public EntitySize scale(float f, float f2) {
      if (this.fixed || f == 1.0f && f2 == 1.0f) {
         return this;
      }
      return new EntitySize(this.width * f, this.height * f2, this.eyeHeight * f2, this.attachments.scale(f, f2, f), false);
   }

   public static EntitySize scalable(float p_220314_0_, float p_220314_1_) {
      return new EntitySize(p_220314_0_, p_220314_1_, false);
   }

   public static EntitySize fixed(float p_220311_0_, float p_220311_1_) {
      return new EntitySize(p_220311_0_, p_220311_1_, true);
   }

   public EntitySize withEyeHeight(float f) {
      return new EntitySize(this.width, this.height, f, this.attachments, this.fixed);
   }

   public EntitySize(float p_i50388_1_, float p_i50388_2_, float eyeHeight, EntityAttachments entityAttachments, boolean p_i50388_3_) {
      this.width = p_i50388_1_;
      this.height = p_i50388_2_;
      this.fixed = p_i50388_3_;
      this.eyeHeight = eyeHeight;
      this.attachments = entityAttachments;
   }

   public EntitySize withAttachments(EntityAttachments.Builder builder) {
      return new EntitySize(this.width, this.height, this.eyeHeight, builder.build(this.width, this.height), this.fixed);
   }



   private static float defaultEyeHeight(float f) {
      return f * 0.85f;
   }

   public String toString() {
      return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
   }
}