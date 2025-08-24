package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreakingEyesLayer<T extends CreakingEntity, M extends NewHierarchicalModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType EYES = RenderType.eyes(new ResourceLocation("textures/entity/creaking/creaking_eyes.png"));

   public CreakingEyesLayer(IEntityRenderer<T, M> p_i50921_1_) {
      super(p_i50921_1_);
   }

   @Override
   public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entity.eyesGlowing() || (entity.isActive() && !entity.isDeadOrDying())) {
         super.render(matrixStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
      }
   }

   public RenderType renderType() {
      return EYES;
   }
}