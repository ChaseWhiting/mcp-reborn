package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.CopperGolemModel;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CopperGolemRenderer extends MobRenderer<CopperGolemEntity, CopperGolemModel> {
   private static final ResourceLocation COPPER_GOLEM_UNAFFECTED = new ResourceLocation("textures/entity/copper_golem.png");
   private static final ResourceLocation COPPER_GOLEM_EXPOSED = new ResourceLocation("textures/entity/exposed_copper_golem.png");
   private static final ResourceLocation COPPER_GOLEM_WEATHERED = new ResourceLocation("textures/entity/weathered_copper_golem.png");
   private static final ResourceLocation COPPER_GOLEM_OXIDIZED = new ResourceLocation("textures/entity/oxidized_copper_golem.png");


   public CopperGolemRenderer(EntityRendererManager p_i47210_1_) {
      super(p_i47210_1_, new CopperGolemModel(), 0.4F);
   }

   public ResourceLocation getTextureLocation(CopperGolemEntity p_110775_1_) {
      return switch(p_110775_1_.getWeatherState()) {
          case UNAFFECTED -> COPPER_GOLEM_UNAFFECTED;
          case EXPOSED -> COPPER_GOLEM_EXPOSED;
          case WEATHERED -> COPPER_GOLEM_WEATHERED;
          case OXIDIZED -> COPPER_GOLEM_OXIDIZED;
      };
   }
}