package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.GoatModel;
import net.minecraft.entity.goat.GoatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoatRenderer extends MobRenderer<GoatEntity, GoatModel> {
   private static final ResourceLocation GOAT_LOCATION = new ResourceLocation("textures/entity/goat/goat.png");


   public GoatRenderer(EntityRendererManager p_i47210_1_) {
      super(p_i47210_1_, new GoatModel(), 0.7F);
   }



   public ResourceLocation getTextureLocation(GoatEntity p_110775_1_) {
      return GOAT_LOCATION;
   }
}