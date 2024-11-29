package net.minecraft.client.renderer.entity;

import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuEntity;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeEntity;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeModel;
import net.minecraft.entity.terraria.monster.demoneye.EyeOfCthulhuModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CthulhuRenderer extends MobRenderer<EyeOfCthulhuEntity, EyeOfCthulhuModel<EyeOfCthulhuEntity>> {


   public CthulhuRenderer(EntityRendererManager p_i47187_1_) {
      super(p_i47187_1_, new EyeOfCthulhuModel<>(), 1.7F);
   }

   public ResourceLocation getTextureLocation(EyeOfCthulhuEntity entity) {
      return new ResourceLocation("textures/entity/terraria/eye_of_cthulhu.png");
   }
}