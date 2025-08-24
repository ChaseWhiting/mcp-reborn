package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.ColdPigFurLayer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PigRenderer extends MobRenderer<PigEntity, PigModel<PigEntity>> {
   private static final ResourceLocation PIG_LOCATION = new ResourceLocation("textures/entity/pig/pig.png");
    private static final ResourceLocation COLD_LOCATION = new ResourceLocation("textures/entity/pig/cold_pig.png");
    private static final ResourceLocation WARM_LOCATION = new ResourceLocation("textures/entity/pig/warm_pig.png");

   public PigRenderer(EntityRendererManager p_i47198_1_) {
      super(p_i47198_1_, new PigModel<>(), 0.7F);
      this.addLayer(new SaddleLayer<>(this, new PigModel<>(0.5F), new ResourceLocation("textures/entity/pig/pig_saddle.png")));
       this.addLayer(new ColdPigFurLayer<>(this, new PigModel<>(0.3F), new ResourceLocation("textures/entity/pig/cold_pig_fur.png")));
   }

   public ResourceLocation getTextureLocation(PigEntity p_110775_1_) {
      return switch (p_110775_1_.getVariant()) {
          case TEMPERATE -> PIG_LOCATION;
          case WARM -> WARM_LOCATION;
          case COLD -> COLD_LOCATION;
      };
   }
}