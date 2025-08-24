package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.alexsmobsport.citadel.advanced.AdvancedModelBox;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.RoadrunnerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.roadrunner.RoadrunnerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RoadrunnerHeldItemLayer extends LayerRenderer<RoadrunnerEntity, RoadrunnerModel> {
   public RoadrunnerHeldItemLayer(IEntityRenderer<RoadrunnerEntity, RoadrunnerModel> p_i50938_1_) {
      super(p_i50938_1_);
   }

   public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, RoadrunnerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      ItemStack itemstack = entity.getItemBySlot(EquipmentSlotType.MAINHAND);
      if (!itemstack.isEmpty()) {
         matrixStack.pushPose();

         if (entity.isBaby()) {
            matrixStack.scale(0.8F, 0.8F, 0.8F);
            matrixStack.translate(0.0D, 0.5D, 0.209375D);
         } else {
            matrixStack.scale(0.5F, 0.5F, 0.5F);
         }

         // Apply root -> body normally
         this.getParentModel().root.translateAndRotate(matrixStack);
         this.getParentModel().body.translateAndRotate(matrixStack);

         // Instead of translating neck manually,
         // go directly to applying the beak's full transform
         AdvancedModelBox beak = this.getParentModel().beak;

         // Traverse up the hierarchy manually: neck -> beak
         // first apply neck base offset
         matrixStack.translate(
                 this.getParentModel().neck.rotationPointX / 16F,
                 this.getParentModel().neck.rotationPointY / 16F,
                 this.getParentModel().neck.rotationPointZ / 16F
         );

         matrixStack.mulPose(Vector3f.ZP.rotation(this.getParentModel().neck.rotateAngleZ));
         matrixStack.mulPose(Vector3f.YP.rotation(this.getParentModel().neck.rotateAngleY));
         matrixStack.mulPose(Vector3f.XP.rotation(this.getParentModel().neck.rotateAngleX));

         // now apply beak
         matrixStack.translate(
                 beak.rotationPointX / 16F,
                 beak.rotationPointY / 16F,
                 beak.rotationPointZ / 16F
         );

         matrixStack.mulPose(Vector3f.ZP.rotation(beak.rotateAngleZ));
         matrixStack.mulPose(Vector3f.YP.rotation(beak.rotateAngleY));
         matrixStack.mulPose(Vector3f.XP.rotation(beak.rotateAngleX));

         // Now you're EXACTLY in the beak's world position/rotation

         // Adjust where the item sits inside the beak
         matrixStack.translate(0.01F, 0.73F, -0.8F); // tweak as needed
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F)); // Rotate item to horizontal

         Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, buffer, packedLight);

         matrixStack.popPose();
      }
   }




   public Vector3f translate() {
      return new Vector3f(this.getParentModel().beak.offsetX + getParentModel().beak.defaultOffsetX, this.getParentModel().beak.offsetY + getParentModel().beak.defaultOffsetY, this.getParentModel().beak.offsetZ + getParentModel().beak.defaultOffsetZ);
   }
}