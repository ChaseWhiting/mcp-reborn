package net.minecraft.client.renderer.entity;

import net.minecraft.entity.terraria.monster.demoneye.DemonEyeEntity;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemonEyeRenderer extends MobRenderer<DemonEyeEntity, DemonEyeModel> {


   public DemonEyeRenderer(EntityRendererManager p_i47187_1_) {
      super(p_i47187_1_, new DemonEyeModel(), 0.3F);
   }

   public ResourceLocation getTextureLocation(DemonEyeEntity entity) {
      return new ResourceLocation("textures/entity/terraria/demon_eye.png");
   }
}