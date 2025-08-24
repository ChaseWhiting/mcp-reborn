package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColdPigFurLayer<T extends PigEntity & IEquipable, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   private final ResourceLocation textureLocation;
   private final M model;

   public ColdPigFurLayer(IEntityRenderer<T, M> p_i232478_1_, M p_i232478_2_, ResourceLocation p_i232478_3_) {
      super(p_i232478_1_);
      this.model = p_i232478_2_;
      this.textureLocation = p_i232478_3_;
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.getVariant().isCold()) {
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, textureLocation, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_, p_225628_7_, 1.0F, 1.0F, 1.0F);
      }
   }
}