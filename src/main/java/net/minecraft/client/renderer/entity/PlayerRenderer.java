package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.*;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public PlayerRenderer(EntityRendererManager p_i46102_1_) {
      this(p_i46102_1_, false);
   }

   public PlayerRenderer(EntityRendererManager p_i46103_1_, boolean p_i46103_2_) {
      super(p_i46103_1_, new PlayerModel<>(0.0F, p_i46103_2_), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ArrowLayer<>(this));
      this.addLayer(new Deadmau5HeadLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new ParrotVariantLayer<>(this));
      this.addLayer(new SpinAttackEffectLayer<>(this));
      this.addLayer(new BeeStingerLayer<>(this));
   }

   public void render(AbstractClientPlayerEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      this.setModelProperties(p_225623_1_);
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public Vector3d getRenderOffset(AbstractClientPlayerEntity p_225627_1_, float p_225627_2_) {
      return p_225627_1_.isCrouching() ? new Vector3d(0.0D, -0.125D, 0.0D) : super.getRenderOffset(p_225627_1_, p_225627_2_);
   }

   private void setModelProperties(AbstractClientPlayerEntity p_177137_1_) {
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
      if (p_177137_1_.isSpectator()) {
         playermodel.setAllVisible(false);
         playermodel.head.visible = true;
         playermodel.hat.visible = true;
      } else {
         playermodel.setAllVisible(true);
         playermodel.hat.visible = p_177137_1_.isModelPartShown(PlayerModelPart.HAT);
         playermodel.jacket.visible = p_177137_1_.isModelPartShown(PlayerModelPart.JACKET);
         playermodel.leftPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
         playermodel.rightPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
         playermodel.leftSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
         playermodel.rightSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
         playermodel.crouching = p_177137_1_.isCrouching();
         BipedModel.ArmPose bipedmodel$armpose = getArmPose(p_177137_1_, Hand.MAIN_HAND);
         BipedModel.ArmPose bipedmodel$armpose1 = getArmPose(p_177137_1_, Hand.OFF_HAND);
         if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = p_177137_1_.getOffhandItem().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
         }

         if (p_177137_1_.getMainArm() == HandSide.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
         } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
         }
      }

   }

   private static BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity p_241741_0_, Hand p_241741_1_) {
      ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);
      if (itemstack.isEmpty()) {
         return BipedModel.ArmPose.EMPTY;
      } else {
         if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0) {
            UseAction useaction = itemstack.getUseAnimation();
            if (useaction == UseAction.BLOCK) {
               return BipedModel.ArmPose.BLOCK;
            }

            if (useaction == UseAction.BOW) {
               return BipedModel.ArmPose.BOW_AND_ARROW;
            }

            if (useaction == UseAction.SPEAR) {
               return BipedModel.ArmPose.THROW_SPEAR;
            }

            if (useaction == UseAction.CROSSBOW && p_241741_1_ == p_241741_0_.getUsedItemHand()) {
               return BipedModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else if (!p_241741_0_.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
            return BipedModel.ArmPose.CROSSBOW_HOLD;
         } else if (!p_241741_0_.swinging && itemstack.getItem() == Items.GILDED_CROSSBOW && GildedCrossbowItem.isCharged(itemstack)) {
            return BipedModel.ArmPose.CROSSBOW_HOLD;
         } else if (!p_241741_0_.swinging && itemstack.getItem() instanceof AbstractCrossbowItem && AbstractCrossbowItem.isCharged(itemstack)) {
            return BipedModel.ArmPose.CROSSBOW_HOLD;
         }

         return BipedModel.ArmPose.ITEM;
      }
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayerEntity p_110775_1_) {
      return p_110775_1_.getSkinTextureLocation();
   }

   protected void scale(AbstractClientPlayerEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = 0.9375F;
      p_225620_2_.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTag(AbstractClientPlayerEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
      p_225629_3_.pushPose();
      if (d0 < 100.0D) {
         Scoreboard scoreboard = p_225629_1_.getScoreboard();
         ScoreObjective scoreobjective = scoreboard.getDisplayObjective(2);
         if (scoreobjective != null) {
            Score score = scoreboard.getOrCreatePlayerScore(p_225629_1_.getScoreboardName(), scoreobjective);
            super.renderNameTag(p_225629_1_, (new StringTextComponent(Integer.toString(score.getScore()))).append(" ").append(scoreobjective.getDisplayName()), p_225629_3_, p_225629_4_, p_225629_5_);
            p_225629_3_.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
         }
      }

      super.renderNameTag(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
      p_225629_3_.popPose();
   }

   public void renderRightHand(MatrixStack p_229144_1_, IRenderTypeBuffer p_229144_2_, int p_229144_3_, AbstractClientPlayerEntity p_229144_4_) {
      this.renderHand(p_229144_1_, p_229144_2_, p_229144_3_, p_229144_4_, (this.model).rightArm, (this.model).rightSleeve);
   }

   public void renderLeftHand(MatrixStack p_229146_1_, IRenderTypeBuffer p_229146_2_, int p_229146_3_, AbstractClientPlayerEntity p_229146_4_) {
      this.renderHand(p_229146_1_, p_229146_2_, p_229146_3_, p_229146_4_, (this.model).leftArm, (this.model).leftSleeve);
   }

   private void renderHand(MatrixStack p_229145_1_, IRenderTypeBuffer p_229145_2_, int p_229145_3_, AbstractClientPlayerEntity p_229145_4_, ModelRenderer p_229145_5_, ModelRenderer p_229145_6_) {
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
      this.setModelProperties(p_229145_4_);
      playermodel.attackTime = 0.0F;
      playermodel.crouching = false;
      playermodel.swimAmount = 0.0F;
      playermodel.setupAnim(p_229145_4_, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      p_229145_5_.xRot = 0.0F;
      p_229145_5_.render(p_229145_1_, p_229145_2_.getBuffer(RenderType.entitySolid(p_229145_4_.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
      p_229145_6_.xRot = 0.0F;
      p_229145_6_.render(p_229145_1_, p_229145_2_.getBuffer(RenderType.entityTranslucent(p_229145_4_.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
   }

   protected void setupRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
      float swimAmount = player.getSwimAmount(partialTicks);

      if (player.isFallFlying()) {
         super.setupRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float fallFlyingTicks = (float) player.getFallFlyingTicks() + partialTicks;
         float fallFlyingProgress = MathHelper.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);

         if (!player.isAutoSpinAttack()) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(fallFlyingProgress * (-90.0F - player.xRot)));
         }

         Vector3d viewVector = player.getViewVector(partialTicks);
         Vector3d deltaMovement = player.getDeltaMovement();
         double horizontalDistanceMovement = Entity.getHorizontalDistanceSqr(deltaMovement);
         double horizontalDistanceView = Entity.getHorizontalDistanceSqr(viewVector);

         if (horizontalDistanceMovement > 0.0D && horizontalDistanceView > 0.0D) {
            double dotProduct = (deltaMovement.x * viewVector.x + deltaMovement.z * viewVector.z) / Math.sqrt(horizontalDistanceMovement * horizontalDistanceView);
            double crossProduct = deltaMovement.x * viewVector.z - deltaMovement.z * viewVector.x;
            matrixStack.mulPose(Vector3f.YP.rotation((float) (Math.signum(crossProduct) * Math.acos(dotProduct))));
         }
      } else if (swimAmount > 0.0F) {
         super.setupRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float swimRotation = player.isInWater() ? -90.0F - player.xRot : -90.0F;
         float lerpedSwimRotation = MathHelper.lerp(swimAmount, 0.0F, swimRotation);
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(lerpedSwimRotation));

         if (player.isVisuallySwimming()) {
            matrixStack.translate(0.0D, -1.0D, 0.3D);
         }
      } else {
         super.setupRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
      }
   }
}