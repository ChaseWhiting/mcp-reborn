package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.monster.creaking.CreakingModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreakingEyesLayer<T extends CreakingEntity, M extends EntityModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType CREAKING_EYES = RenderType.eyes(new ResourceLocation("textures/entity/creaking_eyes.png"));

   public CreakingEyesLayer(IEntityRenderer<T, M> p_i50921_1_) {
      super(p_i50921_1_);
   }

   public RenderType renderType() {
      return CREAKING_EYES;
   }
}