package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.model.RokfiskModel;
import net.minecraft.entity.passive.fish.RokfiskEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RokfiskRenderer extends MobRenderer<RokfiskEntity, RokfiskModel<RokfiskEntity>> {
   private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/rokfisk.png");

   public RokfiskRenderer(EntityRendererManager manager) {
      super(manager, new RokfiskModel<>(manager.bakeLayer(ModelLayers.ROKFISK)), 0.4F);
   }

   public ResourceLocation getTextureLocation(RokfiskEntity entity) {
      return SALMON_LOCATION;
   }

//   protected void setupRotations(RokfiskEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
//      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
//      float f = 1.0F;
//      float f1 = 1.0F;
//      if (!p_225621_1_.isInLava()) {
//         f = 1.3F;
//         f1 = 1.7F;
//      }
//
//      float f2 = f * 4.3F * MathHelper.sin(f1 * 0.6F * p_225621_3_);
//      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(f2));
//      p_225621_2_.translate(0.0D, 0.0D, (double)-0.4F);
//      if (!p_225621_1_.isInLava()) {
//         p_225621_2_.translate((double)0.2F, (double)0.1F, 0.0D);
//         p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
//      }
//
//   }
}