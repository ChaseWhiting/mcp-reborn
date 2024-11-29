package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.AllayAnimation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.allay.AllayEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllayModel<T extends AllayEntity> extends HierarchicalModel<T> implements IHasArm {
   private final ModelRenderer root;
   private final ModelRenderer head;
   private final ModelRenderer body;
   private final ModelRenderer rightArm;
   private final ModelRenderer leftArm;
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing;

   public AllayModel() {
      this.texWidth = 32;
      this.texHeight = 32;

      // Root part
      root = new ModelRenderer(this);
      root.setPos(0.0F, 23.5F, 0.0F);

      // Head
      this.head = new ModelRenderer(this);
      this.head.setPos(0.0F, -3.99F, 0.0F);
      this.head.texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F);
      root.addChild(head);

      // Body
      this.body = new ModelRenderer(this);
      this.body.setPos(0.0F, -4.0F, 0.0F);
      this.body.texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F);
      this.body.texOffs(0, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, -0.2F);
      root.addChild(body);

      // Right Arm
      this.rightArm = new ModelRenderer(this);
      this.rightArm.setPos(-1.75F, 0.5F, 0.0F);
      this.rightArm.texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, -0.01F);
      this.body.addChild(rightArm);

      // Left Arm
      this.leftArm = new ModelRenderer(this);
      this.leftArm.setPos(1.75F, 0.5F, 0.0F);
      this.leftArm.texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, -0.01F);
      this.body.addChild(leftArm);

      // Right Wing
      this.rightWing = new ModelRenderer(this);
      this.rightWing.setPos(-0.5F, 0.0F, 0.6F);
      this.rightWing.texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F);
      this.body.addChild(rightWing);

      // Left Wing
      this.leftWing = new ModelRenderer(this);
      this.leftWing.setPos(0.5F, 0.0F, 0.6F);
      this.leftWing.texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F);
      this.body.addChild(leftWing);
   }

   @Override
   public void setupAnim(AllayEntity allay, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setupAnim((T) allay, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

      // Animation for flying (wing flapping)

         this.animateWalk(AllayAnimation.ALLAY_FLAP, limbSwing, limbSwingAmount, 5.5f, 3.0f);


      // Animation for holding item
      if (allay.hasItemInHand()) {
         this.animate(allay.holdingItemAnimationState, AllayAnimation.ALLAY_HOLD_ITEM, ageInTicks);
      }

      // Animation for dancing
      if (allay.isDancing()) {
         this.animate(allay.danceAnimationState, AllayAnimation.ALLAY_DANCE, ageInTicks);
      }
   }




   @Override
   public void translateToHand(HandSide side, MatrixStack matrixStack) {
      this.root.translateAndRotate(matrixStack);
      this.body.translateAndRotate(matrixStack);
      if (side == HandSide.RIGHT) {
         this.rightArm.translateAndRotate(matrixStack);
      } else {
         this.leftArm.translateAndRotate(matrixStack);
      }
   }

   @Override
   public ImmutableSet<ModelRenderer> getAllParts() {
      return ImmutableSet.of(this.root, this.head, this.body, this.rightArm, this.leftArm, this.rightWing, this.leftWing);
   }

   @Override
   public ModelRenderer root() {
      return this.root;
   }
}
