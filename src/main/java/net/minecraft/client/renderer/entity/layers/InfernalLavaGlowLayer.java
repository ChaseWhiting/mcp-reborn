package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.entity.boss.sovereign.InfernalSovereignEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfernalLavaGlowLayer<T extends InfernalSovereignEntity, M extends HierarchicalModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType EYES = RenderType.eyes(new ResourceLocation("textures/entity/infernal_glow.png"));

   public InfernalLavaGlowLayer(IEntityRenderer<T, M> p_i50921_1_) {
      super(p_i50921_1_);
   }


   public RenderType renderType() {
      return EYES;
   }
}