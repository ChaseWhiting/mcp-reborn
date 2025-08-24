package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Mob;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractBipedModelRenderer<T extends Mob, M extends BipedModel<T>> extends BipedRenderer<T, M> {
   private static final ResourceLocation LOCATION_DEFAULT = new ResourceLocation("textures/entity/colette.png");

   protected AbstractBipedModelRenderer(EntityRendererManager p_i50974_1_, M p_i50974_2_, M p_i50974_3_, M p_i50974_4_) {
      super(p_i50974_1_, p_i50974_2_, 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, p_i50974_3_, p_i50974_4_));
   }

   public ResourceLocation getTextureLocation(Mob entity) {
      return LOCATION_DEFAULT;
   }
}