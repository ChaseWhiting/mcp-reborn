package net.minecraft.client.renderer.entity;

import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuSecondFormEntity;
import net.minecraft.entity.terraria.monster.demoneye.EyeOfCthulhuSecondFormModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CthulhuSecondRenderer extends MobRenderer<EyeOfCthulhuSecondFormEntity, EyeOfCthulhuSecondFormModel> {


   public CthulhuSecondRenderer(EntityRendererManager p_i47187_1_) {
      super(p_i47187_1_, new EyeOfCthulhuSecondFormModel(), 2.2F);
   }

   public ResourceLocation getTextureLocation(EyeOfCthulhuSecondFormEntity entity) {
      return new ResourceLocation("textures/entity/terraria/eye_of_cthulhu_phase2.png");
   }
}