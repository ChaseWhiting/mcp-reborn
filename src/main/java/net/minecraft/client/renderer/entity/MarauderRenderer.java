package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.MarauderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MarauderRenderer extends IllagerRenderer<MarauderEntity> {
    private static final ResourceLocation MARAUDER = new ResourceLocation("textures/entity/illager/marauder_broken_shield.png");

   public MarauderRenderer(EntityRendererManager renderManager) {
      super(renderManager, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<>(this) {
          @Override
          public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, MarauderEntity marauder, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
              super.render(matrixStack, buffer, packedLight, marauder, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
          }
      });
   }

   public ResourceLocation getTextureLocation(MarauderEntity marauder) {
      return MARAUDER;
   }

   protected void scale(MarauderEntity marauder, MatrixStack matrixStack, float v) {
      matrixStack.scale(1.01F, 1.01F, 1.01F);
   }
}