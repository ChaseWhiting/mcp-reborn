package net.minecraft.client.renderer.entity.model.newmodels.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.newmodels.HumanoidArmorModel;
import net.minecraft.client.renderer.entity.model.newmodels.HumanoidModel;
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

public class NewPlayerRenderer
extends LivingRenderer<AbstractClientPlayerEntity, NewPlayerModel<AbstractClientPlayerEntity>> {



    public NewPlayerRenderer(EntityRendererManager context, boolean bl) {
        super(context, new NewPlayerModel<>(context.bakeLayer(bl ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), bl), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(bl ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(bl ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new NewArrowLayer<>(this));
        this.addLayer(new NewDeadmau5HeadLayer(this));
        this.addLayer(new NewCapeLayer(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new NewParrotVariantLayer<>(this));
        this.addLayer(new NewSpinAttackEffectLayer<>(this));
        this.addLayer(new NewBeeStingerLayer<>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity abstractClientPlayer, float f, float f2, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n) {
        this.setModelProperties(abstractClientPlayer);
        super.render(abstractClientPlayer, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public Vector3d getRenderOffset(AbstractClientPlayerEntity abstractClientPlayer, float f) {
        if (abstractClientPlayer.isCrouching()) {
            return new Vector3d(0.0, -0.125, 0.0);
        }
        return super.getRenderOffset(abstractClientPlayer, f);
    }

    private void setModelProperties(AbstractClientPlayerEntity abstractClientPlayer) {
        NewPlayerModel playerModel = (NewPlayerModel)this.getModel();
        if (abstractClientPlayer.isSpectator()) {
            playerModel.setAllVisible(false);
            playerModel.head.visible = true;
            playerModel.hat.visible = true;
        } else {
            playerModel.setAllVisible(true);
            playerModel.hat.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playerModel.jacket.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playerModel.leftPants.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playerModel.rightPants.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playerModel.leftSleeve.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playerModel.rightSleeve.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playerModel.crouching = abstractClientPlayer.isCrouching();
            HumanoidModel.ArmPose armPose = NewPlayerRenderer.getArmPose(abstractClientPlayer, Hand.MAIN_HAND);
            HumanoidModel.ArmPose armPose2 = NewPlayerRenderer.getArmPose(abstractClientPlayer, Hand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                HumanoidModel.ArmPose armPose3 = armPose2 = abstractClientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }
            if (abstractClientPlayer.getMainArm() == HandSide.RIGHT) {
                playerModel.rightArmPose = armPose;
                playerModel.leftArmPose = armPose2;
            } else {
                playerModel.rightArmPose = armPose2;
                playerModel.leftArmPose = armPose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayerEntity abstractClientPlayer, Hand interactionHand) {
        ItemStack itemStack = abstractClientPlayer.getItemInHand(interactionHand);
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (abstractClientPlayer.getUsedItemHand() == interactionHand && abstractClientPlayer.getUseItemRemainingTicks() > 0) {
            UseAction useAnim = itemStack.getUseAnimation();
            if (useAnim == UseAction.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if (useAnim == UseAction.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAnim == UseAction.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if (useAnim == UseAction.CROSSBOW && interactionHand == abstractClientPlayer.getUsedItemHand()) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAnim == UseAction.SPYGLASS) {
                return HumanoidModel.ArmPose.SPYGLASS;
            }
            if (useAnim == UseAction.TOOT_HORN) {
                return HumanoidModel.ArmPose.TOOT_HORN;
            }
        } else if (!abstractClientPlayer.swinging && (itemStack.getItem() instanceof AbstractCrossbowItem || itemStack.getItem() instanceof CrossbowItem || itemStack.getItem() instanceof GildedCrossbowItem) && CrossbowItem.isCharged(itemStack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayerEntity abstractClientPlayer) {
        return abstractClientPlayer.getSkinTextureLocation();
    }

    @Override
    protected void scale(AbstractClientPlayerEntity abstractClientPlayer, MatrixStack poseStack, float f) {
        float f2 = 0.9375f;
        poseStack.scale(0.9375f, 0.9375f, 0.9375f);
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

    private void renderHand(MatrixStack p_229145_1_, IRenderTypeBuffer p_229145_2_, int p_229145_3_, AbstractClientPlayerEntity p_229145_4_, ModelPart p_229145_5_, ModelPart p_229145_6_) {
        NewPlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
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

