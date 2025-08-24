package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.herobrine.HerobrineEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HerobrineRenderer extends LivingRenderer<HerobrineEntity, PlayerModel<HerobrineEntity>> {
   private static final ResourceLocation HEROBRINE_TEXTURE = new ResourceLocation("textures/entity/herobrine.png");

   public HerobrineRenderer(EntityRendererManager renderManager) {
      super(renderManager, new PlayerModel<>(0.0F, false), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ArrowLayer<>(this));
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new SpinAttackEffectLayer<>(this));
      this.addLayer(new BeeStingerLayer<>(this));
   }

   @Override
   public void render(HerobrineEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      this.setModelProperties(entity);
      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
   }

   @Override
   public Vector3d getRenderOffset(HerobrineEntity entity, float partialTicks) {
      return entity.isCrouching() ? new Vector3d(0.0D, -0.125D, 0.0D) : super.getRenderOffset(entity, partialTicks);
   }

   private void setModelProperties(HerobrineEntity entity) {
      PlayerModel<HerobrineEntity> model = this.getModel();
      if (entity.isSpectator()) {
         model.setAllVisible(false);
         model.head.visible = true;
         model.hat.visible = true;
      } else {
         model.setAllVisible(true);
         model.hat.visible = true;
         model.jacket.visible = true;
         model.leftPants.visible = true;
         model.rightPants.visible = true;
         model.leftSleeve.visible = true;
         model.rightSleeve.visible = true;
         model.crouching = entity.isCrouching();
         BipedModel.ArmPose mainHandPose = getArmPose(entity, Hand.MAIN_HAND);
         BipedModel.ArmPose offHandPose = getArmPose(entity, Hand.OFF_HAND);
         if (mainHandPose.isTwoHanded()) {
            offHandPose = entity.getOffhandItem().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
         }

         if (entity.getMainArm() == HandSide.RIGHT) {
            model.rightArmPose = mainHandPose;
            model.leftArmPose = offHandPose;
         } else {
            model.rightArmPose = offHandPose;
            model.leftArmPose = mainHandPose;
         }
      }
   }

   private static BipedModel.ArmPose getArmPose(HerobrineEntity entity, Hand hand) {
      ItemStack itemstack = entity.getItemInHand(hand);
      if (itemstack.isEmpty()) {
         return BipedModel.ArmPose.EMPTY;
      } else {
         if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
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

            if (useaction == UseAction.CROSSBOW && hand == entity.getUsedItemHand()) {
               return BipedModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else if (!entity.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
            return BipedModel.ArmPose.CROSSBOW_HOLD;
         }

         return BipedModel.ArmPose.ITEM;
      }
   }

   @Override
   public ResourceLocation getTextureLocation(HerobrineEntity entity) {
      return HEROBRINE_TEXTURE;
   }

   @Override
   protected void scale(HerobrineEntity entity, MatrixStack matrixStack, float partialTickTime) {
      float scale = 0.9375F;
      matrixStack.scale(scale, scale, scale);
   }

   @Override
   protected void renderNameTag(HerobrineEntity entity, ITextComponent displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      matrixStack.pushPose();
      super.renderNameTag(entity, displayName, matrixStack, buffer, packedLight);
      matrixStack.popPose();
   }

   public void renderRightHand(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, HerobrineEntity entity) {
      this.renderHand(matrixStack, buffer, packedLight, entity, this.getModel().rightArm, this.getModel().rightSleeve);
   }

   public void renderLeftHand(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, HerobrineEntity entity) {
      this.renderHand(matrixStack, buffer, packedLight, entity, this.getModel().leftArm, this.getModel().leftSleeve);
   }

   private void renderHand(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, HerobrineEntity entity, ModelRenderer armRenderer, ModelRenderer sleeveRenderer) {
      PlayerModel<HerobrineEntity> model = this.getModel();
      this.setModelProperties(entity);
      model.attackTime = 0.0F;
      model.crouching = false;
      model.swimAmount = 0.0F;
      model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      armRenderer.xRot = 0.0F;
      armRenderer.render(matrixStack, buffer.getBuffer(RenderType.entitySolid(entity.getSkinTextureLocation())), packedLight, OverlayTexture.NO_OVERLAY);
      sleeveRenderer.xRot = 0.0F;
      sleeveRenderer.render(matrixStack, buffer.getBuffer(RenderType.entityTranslucent(entity.getSkinTextureLocation())), packedLight, OverlayTexture.NO_OVERLAY);
   }

   @Override
   protected void setupRotations(HerobrineEntity entity, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
      float swimAmount = entity.getSwimAmount(partialTicks);
      if (entity.isFallFlying()) {
         super.setupRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f1 = (float) entity.getFallFlyingTicks() + partialTicks;
         float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entity.isAutoSpinAttack()) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entity.xRot)));
         }

         Vector3d viewVector = entity.getViewVector(partialTicks);
         Vector3d motionVector = entity.getDeltaMovement();
         double d0 = Entity.getHorizontalDistanceSqr(motionVector);
         double d1 = Entity.getHorizontalDistanceSqr(viewVector);
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (motionVector.x * viewVector.x + motionVector.z * viewVector.z) / Math.sqrt(d0 * d1);
            double d3 = motionVector.x * viewVector.z - motionVector.z * viewVector.x;
            matrixStack.mulPose(Vector3f.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
         }
      } else if (swimAmount > 0.0F) {
         super.setupRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f3 = entity.isInWater() ? -90.0F - entity.xRot : -90.0F;
         float f4 = MathHelper.lerp(swimAmount, 0.0F, f3);
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
         if (entity.isVisuallySwimming()) {
            matrixStack.translate(0.0D, -1.0D, 0.3F);
         }
      } else {
         super.setupRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
      }
   }
}
