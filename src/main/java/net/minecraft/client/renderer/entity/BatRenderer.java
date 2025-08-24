package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<BatEntity, BatModel<BatEntity>> {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");
   private static final ResourceLocation WARM_BAT_LOCATION = new ResourceLocation("textures/entity/warm_bat.png");
   private static final ResourceLocation COLD_BAT_LOCATION = new ResourceLocation("textures/entity/cold_bat.png");


   public BatRenderer(EntityRendererManager p_i46192_1_) {
      super(p_i46192_1_, new BatModel(), 0.25F);
   }

   public ResourceLocation getTextureLocation(BatEntity p_110775_1_) {
      return switch (p_110775_1_.getVariant()) {
          case TEMPERATE -> BAT_LOCATION;
          case WARM -> WARM_BAT_LOCATION;
          case COLD -> COLD_BAT_LOCATION;
      };
   }

    @Override
    protected void scale(BatEntity bat, MatrixStack p_225620_2_, float p_225620_3_) {
        if (bat.getVariant().isWarm()) {
            p_225620_2_.scale(1.25f);
        } else if (bat.getVariant().isCold()){
            p_225620_2_.scale(0.9f);
        } else {
            super.scale(bat, p_225620_2_, p_225620_3_);

        }
    }
}