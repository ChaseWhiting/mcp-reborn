package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShamanRenderer<T extends SpellcastingIllagerEntity> extends IllagerRenderer<T> {
   private static final ResourceLocation SHAMAN_ILLAGER = new ResourceLocation("textures/entity/illager/shaman.png");

   public ShamanRenderer(EntityRendererManager manager) {
      super(manager, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<T, IllagerModel<T>>(this) {
         public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, T illager, float v, float v1, float v2, float v3, float v4, float v5) {
            if (illager.isCastingSpell()) {
               super.render(matrixStack, iRenderTypeBuffer, i, illager, v, v1, v2, v3, v4, v5);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(T entity) {
      return SHAMAN_ILLAGER;
   }
}