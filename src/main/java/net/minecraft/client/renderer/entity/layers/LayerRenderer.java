package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LayerRenderer<T extends Entity, M extends EntityModel<T>> {
   private final IEntityRenderer<T, M> renderer;

   public LayerRenderer(IEntityRenderer<T, M> p_i50926_1_) {
      this.renderer = p_i50926_1_;
   }

   protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> baseModel, EntityModel<T> overlayModel, ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
      if (!entity.isInvisible()) {
         baseModel.copyPropertiesTo(overlayModel);
         overlayModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
         overlayModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         renderColoredCutoutModel(overlayModel, texture, matrixStack, renderTypeBuffer, packedLight, entity, red, green, blue);
      }
   }

   protected static <A extends BoggedEntity> void createBogged(EntityModel<A> baseModel, EntityModel<A> overlayModel, ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, A entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
         baseModel.copyPropertiesTo(overlayModel);
         overlayModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
         overlayModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         renderColoredCutoutModel(overlayModel, texture, matrixStack, renderTypeBuffer, packedLight, entity, red, green, blue);
   }

   protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRenderNoInvisible(EntityModel<T> baseModel, EntityModel<T> overlayModel, ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
         baseModel.copyPropertiesTo(overlayModel);
         overlayModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
         overlayModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         renderColoredCutoutModel(overlayModel, texture, matrixStack, renderTypeBuffer, packedLight, entity, red, green, blue);
   }


   protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> p_229141_0_, ResourceLocation p_229141_1_, MatrixStack p_229141_2_, IRenderTypeBuffer p_229141_3_, int p_229141_4_, T p_229141_5_, float p_229141_6_, float p_229141_7_, float p_229141_8_) {
      IVertexBuilder ivertexbuilder = p_229141_3_.getBuffer(RenderType.entityCutoutNoCull(p_229141_1_));
      p_229141_0_.renderToBuffer(p_229141_2_, ivertexbuilder, p_229141_4_, LivingRenderer.getOverlayCoords(p_229141_5_, 0.0F), p_229141_6_, p_229141_7_, p_229141_8_, 1.0F);
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   protected ResourceLocation getTextureLocation(T p_229139_1_) {
      return this.renderer.getTextureLocation(p_229139_1_);
   }

   public abstract void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_);
}