package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AbstractCapeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public CapeLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50950_1_) {
      super(p_i50950_1_);
   }

   public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      ItemStack chest = player.getItemBySlot(EquipmentSlotType.CHEST);
      if (chest.getItem() instanceof AbstractCapeItem) {
         renderWithCustomCape(matrixStack, buffer, light, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
      } else if (player.isCapeLoaded() && !player.isInvisible() && player.isModelPartShown(PlayerModelPart.CAPE) && player.getCloakTextureLocation() != null) {
         ItemStack chestItemStack = player.getItemBySlot(EquipmentSlotType.CHEST);
         if (!(chestItemStack.getItem() == Items.ELYTRA)) {
            matrixStack.pushPose();
            matrixStack.translate(0.0D, 0.0D, 0.125D);
            double d0 = MathHelper.lerp(partialTicks, player.xCloakO, player.xCloak) - MathHelper.lerp(partialTicks, player.xo, player.getX());
            double d1 = MathHelper.lerp(partialTicks, player.yCloakO, player.yCloak) - MathHelper.lerp(partialTicks, player.yo, player.getY());
            double d2 = MathHelper.lerp(partialTicks, player.zCloakO, player.zCloak) - MathHelper.lerp(partialTicks, player.zo, player.getZ());
            float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
            double d3 = (double) MathHelper.sin(f * ((float) Math.PI / 180F));
            double d4 = (double) (-MathHelper.cos(f * ((float) Math.PI / 180F)));
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
            f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
               f2 = 0.0F;
            }

            float f4 = MathHelper.lerp(partialTicks, player.oBob, player.bob);
            f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * f4;
            if (player.isCrouching()) {
               f1 += 25.0F;
            }

            matrixStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));
            IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entitySolid(player.getCloakTextureLocation()));
            this.getParentModel().renderCloak(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
         }
      }
   }

   public void renderWithCustomCape(MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         ItemStack chestItemStack = player.getItemBySlot(EquipmentSlotType.CHEST);
         AbstractCapeItem capeItem = (AbstractCapeItem) chestItemStack.getItem();
         ResourceLocation customCapeLocation = capeItem.getCapeID();

       matrixStack.pushPose();
       matrixStack.translate(0.0D, 0.0D, 0.125D);
       double d0 = MathHelper.lerp(partialTicks, player.xCloakO, player.xCloak) - MathHelper.lerp(partialTicks, player.xo, player.getX());
       double d1 = MathHelper.lerp(partialTicks, player.yCloakO, player.yCloak) - MathHelper.lerp(partialTicks, player.yo, player.getY());
       double d2 = MathHelper.lerp(partialTicks, player.zCloakO, player.zCloak) - MathHelper.lerp(partialTicks, player.zo, player.getZ());
       float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
       double d3 = (double) MathHelper.sin(f * ((float) Math.PI / 180F));
       double d4 = (double) (-MathHelper.cos(f * ((float) Math.PI / 180F)));
       float f1 = (float) d1 * 10.0F;
       f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
       float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
       f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
       float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
       f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
       if (f2 < 0.0F) {
           f2 = 0.0F;
       }

       float f4 = MathHelper.lerp(partialTicks, player.oBob, player.bob);
       f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * f4;
       if (player.isCrouching()) {
           f1 += 25.0F;
       }

       matrixStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
       matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
       matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));
         IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entitySolid(customCapeLocation));
         this.getParentModel().renderCloak(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
         matrixStack.popPose();
      }
}