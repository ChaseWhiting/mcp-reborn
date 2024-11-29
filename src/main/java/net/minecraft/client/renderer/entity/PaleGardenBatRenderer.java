package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.client.renderer.entity.model.PaleGardenBatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.PaleGardenBatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaleGardenBatRenderer extends MobRenderer<PaleGardenBatEntity, PaleGardenBatModel> {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/pale_garden_bat.png");

   public PaleGardenBatRenderer(EntityRendererManager p_i46192_1_) {
      super(p_i46192_1_, new PaleGardenBatModel(), 0.25F);
   }

   public ResourceLocation getTextureLocation(PaleGardenBatEntity p_110775_1_) {
      return BAT_LOCATION;
   }

//      protected void setupRotations(PaleGardenBatEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
//      if (!p_225621_1_.isResting()) {
//         p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(60.0F));
//      }
//
//      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
//   }

}