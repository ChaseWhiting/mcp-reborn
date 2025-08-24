package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlazeModel<T extends BlazeEntity> extends HierarchicalModel<T> {
   private final ModelRenderer[] upperBodyParts;
   private final ModelRenderer root = new ModelRenderer(this, "root"); //
   private final ModelRenderer head = new ModelRenderer(this, 0, 0);
   private final ImmutableList<ModelRenderer> parts;

   public BlazeModel() {
      root.setPos(0, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      head.setName("head"); //
      this.upperBodyParts = new ModelRenderer[12];

      for(int i = 0; i < this.upperBodyParts.length; ++i) {
         this.upperBodyParts[i] = new ModelRenderer(this, 0, 16);
         int number = i == 0 ? 1 : i + 1;//
         this.upperBodyParts[i].setName("stick" + number);//
         this.upperBodyParts[i].addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
         root.addChild(this.upperBodyParts[i]);
      }

      Builder<ModelRenderer> builder = ImmutableList.builder();
      builder.add(this.head);
      builder.addAll(Arrays.asList(this.upperBodyParts));
      this.parts = builder.build();
      root.addChild(this.head);//
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float ageInTicks, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, ageInTicks, p_225597_5_, p_225597_6_);
      if (!p_225597_1_.isDying()) {
         float f = ageInTicks * (float)Math.PI * -0.1F;

         for(int i = 0; i < 4; ++i) {
            this.upperBodyParts[i].y = -2.0F + MathHelper.cos(((float)(i * 2) + ageInTicks) * 0.25F);
            this.upperBodyParts[i].x = MathHelper.cos(f) * 9.0F;
            this.upperBodyParts[i].z = MathHelper.sin(f) * 9.0F;
            ++f;
         }

         f = ((float)Math.PI / 4F) + ageInTicks * (float)Math.PI * 0.03F;

         for(int j = 4; j < 8; ++j) {
            this.upperBodyParts[j].y = 2.0F + MathHelper.cos(((float)(j * 2) + ageInTicks) * 0.25F);
            this.upperBodyParts[j].x = MathHelper.cos(f) * 7.0F;
            this.upperBodyParts[j].z = MathHelper.sin(f) * 7.0F;
            ++f;
         }

         f = 0.47123894F + ageInTicks * (float)Math.PI * -0.05F;

         for(int k = 8; k < 12; ++k) {
            this.upperBodyParts[k].y = 11.0F + MathHelper.cos(((float)k * 1.5F + ageInTicks) * 0.5F);
            this.upperBodyParts[k].x = MathHelper.cos(f) * 5.0F;
            this.upperBodyParts[k].z = MathHelper.sin(f) * 5.0F;
            ++f;
         }
         this.animateWalk(BlazeAnimation.WALKING, p_225597_2_, p_225597_3_, 1.7f, 1.7f);//

      }
      this.animate(p_225597_1_.chargeState, BlazeAnimation.CHARGE, ageInTicks);
      this.animate(p_225597_1_.deathAnimationState, BlazeAnimation.DEATH, ageInTicks);
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
   }

   @Override
   public ImmutableSet<ModelRenderer> getAllParts() {
      return this.parts.stream().collect(ImmutableSet.toImmutableSet());//
   }

   @Override
   public ModelRenderer root() {
      return this.root;//
   }
}