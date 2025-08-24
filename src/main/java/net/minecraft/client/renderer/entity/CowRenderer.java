package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.ColdCowModel;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.entity.model.WarmCowModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CowRenderer extends MobRenderer<CowEntity, CowModel<CowEntity>> {
   private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/temperate_cow.png");
   private static final ResourceLocation COLD_COW_LOCATION = new ResourceLocation("textures/entity/cow/cold_cow.png");
   private static final ResourceLocation WARM_COW_LOCATION = new ResourceLocation("textures/entity/cow/warm_cow.png");

   private static final CowModel<CowEntity> cowModel = new CowModel<>();
   private static final ColdCowModel<CowEntity> coldCowModel = new ColdCowModel<>();
   private static final WarmCowModel<CowEntity> warmCowModel = new WarmCowModel<>();


   public CowRenderer(EntityRendererManager p_i47210_1_) {
      super(p_i47210_1_, new CowModel<>(), 0.7F);
   }

   @Override
   public void render(CowEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (p_225623_1_.getVariant().isCold() && this.model != coldCowModel) {
         this.model = coldCowModel;
      }
      if (p_225623_1_.getVariant().isWarm() && this.model != warmCowModel) {
         this.model = warmCowModel;
      }
      if (!p_225623_1_.getVariant().isCold() && !p_225623_1_.getVariant().isWarm() && this.model != cowModel) {
         this.model = cowModel;
      }

      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(CowEntity p_110775_1_) {
      return switch (p_110775_1_.getVariant()) {
          case TEMPERATE -> COW_LOCATION;
          case WARM -> WARM_COW_LOCATION;
          case COLD -> COLD_COW_LOCATION;
      };
   }
}