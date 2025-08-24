package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.WoodpeckerModel;
import net.minecraft.entity.passive.WoodpeckerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WoodpeckerRenderer extends MobRenderer<WoodpeckerEntity, WoodpeckerModel> {
public static final ResourceLocation location = new ResourceLocation("textures/entity/woodpecker.png");

   public WoodpeckerRenderer(EntityRendererManager p_i47375_1_) {
      super(p_i47375_1_, new WoodpeckerModel(), 0.3F);
   }

   public ResourceLocation getTextureLocation(WoodpeckerEntity p_110775_1_) {
      return location;
   }

   @Override
   protected void scale(WoodpeckerEntity woodpeckerEntity, MatrixStack matrixStack, float v) {
      if (woodpeckerEntity.isBaby()) {
         matrixStack.scale(0.6f);
      } else {
         matrixStack.scale(1F);
      }
   }
}