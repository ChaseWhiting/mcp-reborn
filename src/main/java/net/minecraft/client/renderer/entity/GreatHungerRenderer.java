package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.GreatHungerModel;
import net.minecraft.entity.monster.GreatHungerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GreatHungerRenderer extends MobRenderer<GreatHungerEntity, GreatHungerModel<GreatHungerEntity>> {
   private static final ResourceLocation GREAT_HUNGER = new ResourceLocation("textures/entity/great_hunger.png");

   public GreatHungerRenderer(EntityRendererManager p_i47195_1_) {
      super(p_i47195_1_, new GreatHungerModel<>(), 0.35F);
   }

   public ResourceLocation getTextureLocation(GreatHungerEntity p_110775_1_) {
      return GREAT_HUNGER;
   }

   @Override
   protected void scale(GreatHungerEntity entity, MatrixStack matrixStack, float deltaTime) {
      // Calculate target scale based on whether the entity is digging or swallowing items
      float targetScale = entity.isDigging() ? 0.0F : (1.0F + 0.015F * entity.getSwallowedItemCount());

      // Lerp between current progress and target scale for smooth transition
      entity.setClientSideGrowProgress(MathHelper.lerp(0.05F, entity.getClientSideGrowProgress(), targetScale));

      // Apply the smooth scale transition to the MatrixStack
      float currentScale = entity.getClientSideGrowProgress();
      matrixStack.scale(currentScale, currentScale, currentScale);
   }
}