package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractBipedModelMaker<T extends LivingEntity> extends BipedModel<T> {

   protected AbstractBipedModelMaker(float modelSize, float yOffset, int textureWidth, int textureHeight) {
      super(modelSize, yOffset, textureWidth, textureHeight);
   }

   @Override
   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
   }
}
