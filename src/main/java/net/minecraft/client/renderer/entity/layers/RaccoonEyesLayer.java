package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.RaccoonModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.RaccoonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaccoonEyesLayer<T extends RaccoonEntity, M extends RaccoonModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType RACCOON_EYES = RenderType.eyes(new ResourceLocation("textures/entity/fox/raccoon_eyes.png"));

   public RaccoonEyesLayer(IEntityRenderer<T, M> p_i50921_1_) {
      super(p_i50921_1_);
   }

   public RenderType renderType() {
      return RACCOON_EYES;
   }
}