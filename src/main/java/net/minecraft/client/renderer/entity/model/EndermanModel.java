package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.EndermanAnimation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanModel<T extends LivingEntity> extends HierarchicalModel<T> {
   private final ModelRenderer root;
   private final ModelRenderer body;
   private final ModelRenderer head;
   private final ModelRenderer headwear;
   private final ModelRenderer right_arm;
   private final ModelRenderer left_arm;
   private final ModelRenderer right_leg;
   private final ModelRenderer left_leg;
   public boolean carrying = false;
   public boolean creepy = false;
   private final AnimationState angryState = new AnimationState();




   public EndermanModel() {
      super();
      texWidth = 64;
      texHeight = 32;
      this.root = new ModelRenderer(this, "root");
      root.setPos(0, 0, 0);
      body = new ModelRenderer(this, "body");
      body.setPos(0.0F, -15.0F, 0.0F);
      body.texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);
      root.addChild(body);
      head = new ModelRenderer(this, "head");
      head.setPos(0.0F, 0.5F, 0.0F);
      head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
      headwear = new ModelRenderer(this, "headwear");
      headwear.setPos(0.0F, 0.5F, 0.0F);
      headwear.texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F, false);
      body.addChild(headwear);
      body.addChild(head);

      right_arm = new ModelRenderer(this, "right_arm");
      right_arm.setPos(-5.0F, 2F, 0.0F);
      right_arm.texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, 0.0F, false);
      body.addChild(right_arm);
      left_arm = new ModelRenderer(this, "left_arm");
      left_arm.setPos(5.0F, 2F, 0.0F);
      left_arm.texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, 0.0F, true);
      body.addChild(left_arm);
      right_leg = new ModelRenderer(this, "right_leg");
      right_leg.setPos(-2.0F, -6.0F, 0.0F);
      right_leg.texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, 0.0F, false);

      left_leg = new ModelRenderer(this, "left_leg");
      left_leg.setPos(2.2F, -6.0F, 0.0F);
      left_leg.texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, 0.0F, true);

      root.addChildren(right_leg, left_leg);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
      super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      this.animate(((EndermanEntity)entity).hurtState, EndermanAnimation.HURT, ageInTicks, 1.0F);
      if (!creepy) {
         animateWalk(EndermanAnimation.WALK, limbSwing, limbSwingAmount, 3F, 5F);
      } else {
         animateWalk(EndermanAnimation.WALK_ANGRY, limbSwing, limbSwingAmount, 3F, 5F);
      }
      if (carrying && entity instanceof EndermanEntity) {
         animate(((EndermanEntity)entity).holdingBlockState, EndermanAnimation.HOLD_BLOCK, ageInTicks, 1.0F);
      }

      if (this.creepy) {
         angryState.animateWhen(true, (int) ageInTicks);
         this.head.setPos(0, -3, 0);
         this.animate(angryState, EndermanAnimation.ANGRY, ageInTicks, 1.0F);
      } else {
         this.head.setPos(0, 0.5F, 0);
      }

      this.animate(((EndermanEntity)entity).attackState, EndermanAnimation.ATTACK, ageInTicks);
      this.headTurn(head, netHeadYaw, headPitch);
      this.headTurn(headwear, netHeadYaw, headPitch);

   }

   @Override
   public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
      super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
   }

   @Override
   public ImmutableSet<ModelRenderer> getAllParts() {
      return ImmutableSet.of(root, this.body, this.head, this.headwear, this.left_arm, this.right_arm, this.left_leg, this.right_leg);
   }

   @Override
   public ModelRenderer root() {
      return root;
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.xRot = x;
      modelRenderer.yRot = y;
      modelRenderer.zRot = z;
   }
}