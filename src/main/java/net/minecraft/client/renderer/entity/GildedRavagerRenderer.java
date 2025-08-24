package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.GildedRavagerModel;
import net.minecraft.entity.monster.GildedRavagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GildedRavagerRenderer extends MobRenderer<GildedRavagerEntity, GildedRavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/gilded_ravager.png");

   public GildedRavagerRenderer(EntityRendererManager manager) {
      super(manager, new GildedRavagerModel(), 1.1F);
   }

   public ResourceLocation getTextureLocation(GildedRavagerEntity gildedRavager) {
      return TEXTURE_LOCATION;
   }


   protected void scale(GildedRavagerEntity ravager, MatrixStack matrixStack, float v) {
      matrixStack.scale(1.07F, 1.1F, 1.07F);
   }
}