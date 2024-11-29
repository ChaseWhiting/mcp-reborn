package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatModel<T extends BatEntity> extends HierarchicalModel<T> {
   protected ModelRenderer root;
   protected ModelRenderer head;
   protected ModelRenderer body;
   protected ModelRenderer rightWing;
   protected ModelRenderer leftWing;
   protected ModelRenderer rightWingTip;
   protected ModelRenderer leftWingTip;
   protected ModelRenderer feet;

   public BatModel() {
      this.texWidth = 32;
      this.texHeight = 32;

      // Root part
      root = new ModelRenderer(this, "root");
      root.setPos(0.0F, 0.0F, 0.0F);

      // Body
      this.body = new ModelRenderer(this, "body");
      this.body.texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F);
      this.body.setPos(0.0F, 17.0F, 0.0F);
      root.addChild(body);

      // Head
      this.head = new ModelRenderer(this, "head");
      this.head.texOffs(0, 7).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F);
      this.head.setPos(0.0F, 17.0F, 0.01F);
      root.addChild(head);

      // Right Ear
      ModelRenderer rightEar = new ModelRenderer(this, "right_ear");
      rightEar.texOffs(1, 15).addBox(-2.5F, -4.0F, 0.0F, 3.0F, 5.0F, 0.05F);
      rightEar.setPos(-1.5F, -2.0F, 0F);
      this.head.addChild(rightEar);

      // Left Ear
      ModelRenderer leftEar = new ModelRenderer(this, "left_ear");
      leftEar.texOffs(8, 15).addBox(-0.1F, -3.0F, 0.0F, 3.0F, 5.0F, 0.05F);
      leftEar.setPos(1.1F, -3.0F, 0F);
      this.head.addChild(leftEar);

      // Right Wing
      this.rightWing = new ModelRenderer(this, "right_wing");
      this.rightWing.texOffs(12, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F);
      this.rightWing.setPos(-1.5F, 0.0F, 0.0F);
      this.body.addChild(this.rightWing);

      // Right Wing Tip
      this.rightWingTip = new ModelRenderer(this, "right_wing_tip");
      this.rightWingTip.texOffs(16, 0).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F);
      this.rightWingTip.setPos(-2.0F, 0.0F, 0.0F);
      this.rightWing.addChild(this.rightWingTip);

      // Left Wing
      this.leftWing = new ModelRenderer(this, "left_wing");
      this.leftWing.texOffs(12, 7).addBox(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F);
      this.leftWing.setPos(1.5F, 0.0F, 0.0F);
      this.body.addChild(this.leftWing);

      // Left Wing Tip
      this.leftWingTip = new ModelRenderer(this, "left_wing_tip");
      this.leftWingTip.texOffs(16, 8).addBox(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F);
      this.leftWingTip.setPos(2.0F, 0.0F, 0.0F);
      this.leftWing.addChild(this.leftWingTip);

      // Feet
      this.feet = new ModelRenderer(this, "feet");
      this.feet.texOffs(16, 16).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F);
      this.feet.setPos(0.0F, 5.0F, 0.0F);
      this.body.addChild(feet);
   }

   @Override
   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entity.isResting()) {
         this.applyHeadRotation(entity.yRot);
      }
      this.animate(entity.flyAnimationState, BatAnimation.BAT_FLYING, ageInTicks, 1.0f);
      this.animate(entity.restAnimationState, BatAnimation.BAT_RESTING, ageInTicks, 1.0f);
   }

   @Override
   public ImmutableSet<ModelRenderer> getAllParts() {
      return ImmutableSet.of(this.body, this.feet, this.head, this.rightWing, this.leftWing, this.rightWingTip, this.leftWingTip);
   }

   @Override
   public ModelRenderer root() {
      return this.root;
   }

   private void applyHeadRotation(float $$0) {
      this.head.yRot = $$0 * ((float)Math.PI / 180);
   }
}
