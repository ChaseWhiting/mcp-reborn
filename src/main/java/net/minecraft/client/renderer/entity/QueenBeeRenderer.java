package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.BeeModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.QueenBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QueenBeeRenderer extends MobRenderer<QueenBeeEntity, BeeModel<QueenBeeEntity>> {
   private static final ResourceLocation ANGRY_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee.png");
   private static final ResourceLocation NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_nectar.png");

   public QueenBeeRenderer(EntityRendererManager p_i226033_1_) {
      super(p_i226033_1_, new BeeModel<>(), 0.4F);
   }

   @Override
   protected void scale(QueenBeeEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      p_225620_2_.scale(1.4F);
   }

   public ResourceLocation getTextureLocation(QueenBeeEntity p_110775_1_) {
      if (p_110775_1_.isAngry()) {
         return p_110775_1_.hasNectar() ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
      } else {
         return p_110775_1_.hasNectar() ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
      }
   }
}