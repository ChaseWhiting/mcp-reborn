package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.HoveringInfernoModel;
import net.minecraft.entity.monster.WildfireEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WildfireRenderer extends MobRenderer<WildfireEntity, HoveringInfernoModel<WildfireEntity>> {
   private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/hovering_inferno.png");

   public WildfireRenderer(EntityRendererManager p_i46191_1_) {
      super(p_i46191_1_, new HoveringInfernoModel<>(), 0.5F);
   }

   protected int getBlockLightLevel(WildfireEntity p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public float getFlipDegrees(WildfireEntity blaze) {
      return 0f;
   }

   public ResourceLocation getTextureLocation(WildfireEntity p_110775_1_) {
      return BLAZE_LOCATION;
   }
}