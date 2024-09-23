package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.BoggedOuterLayer;
import net.minecraft.client.renderer.entity.model.BoggedModel;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoggedRenderer extends BipedRenderer<BoggedEntity, BoggedModel<BoggedEntity>> {
   private static final ResourceLocation BOGGED_TEXTURE = new ResourceLocation("textures/entity/skeleton/bogged.png");
   private static final ResourceLocation BLOSSOMED_TEXTURE = new ResourceLocation("textures/entity/skeleton/blossomed.png");
   private static final ResourceLocation WITHERED_TEXTURE = new ResourceLocation("textures/entity/skeleton/withered.png");
   private static final ResourceLocation PARCHED_TEXTURE = new ResourceLocation("textures/entity/skeleton/parched.png");
   private static final ResourceLocation FROSTED_TEXTURE = new ResourceLocation("textures/entity/skeleton/frosted.png");
   private static final ResourceLocation FESTERED_TEXTURE = new ResourceLocation("textures/entity/skeleton/festered.png");
   private static final ResourceLocation FESTERED_BROWN_TEXTURE = new ResourceLocation("textures/entity/skeleton/festered_brown.png");




   public BoggedRenderer(EntityRendererManager renderManager) {
      super(renderManager, new BoggedModel<>(), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new BoggedModel<>(0.5F,true), new BoggedModel<>(1.0F, true)));
      this.addLayer(new BoggedOuterLayer<>(this));
   }


   public ResourceLocation getTextureLocation(BoggedEntity entity) {
      return switch (entity.getBoggedType()) {
          case BLOSSOMED -> BLOSSOMED_TEXTURE;
          case WITHERED -> WITHERED_TEXTURE;
          case PARCHED -> PARCHED_TEXTURE;
         case FESTERED -> FESTERED_TEXTURE;
         case FESTERED_BROWN -> FESTERED_BROWN_TEXTURE;
         case FROSTED -> FROSTED_TEXTURE;
         default -> BOGGED_TEXTURE;
      };
   }

   protected boolean isShaking(BoggedEntity p_230495_1_) {
      return p_230495_1_.isUnderWaterConverting();
   }

   @Override
   public float getShakeAmount() {
      return 0.9F;
   }
}
