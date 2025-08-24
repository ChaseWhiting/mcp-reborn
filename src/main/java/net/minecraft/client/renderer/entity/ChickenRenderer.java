package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.client.renderer.entity.model.ColdChickenModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.AdultAndBabyModelPair;
import net.minecraft.client.renderer.entity.model.newmodels.animal.chicken.NewChickenModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.chicken.NewColdChickenModel;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ChickenRenderer extends MobRenderer<ChickenEntity, NewChickenModel> {
   private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");
   private static final ResourceLocation WARM = new ResourceLocation("textures/entity/warm_chicken.png");
   private static final ResourceLocation COLD = new ResourceLocation("textures/entity/cold_chicken.png");

   private final Map<WarmColdVariant.ModelType, AdultAndBabyModelPair<? extends NewChickenModel>> models;


   public ChickenRenderer(EntityRendererManager manager) {
      super(manager, new NewChickenModel(manager.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
      this.models = bakeModels(manager);
   }

   private static Map<WarmColdVariant.ModelType, AdultAndBabyModelPair<? extends NewChickenModel>> bakeModels(EntityRendererManager context) {
      return Maps.newEnumMap(Map.of(
              WarmColdVariant.ModelType.NORMAL, new AdultAndBabyModelPair<>(
                      new NewChickenModel(context.bakeLayer(ModelLayers.CHICKEN)),
                      new NewChickenModel(context.bakeLayer(ModelLayers.CHICKEN_BABY))),
              WarmColdVariant.ModelType.WARM, new AdultAndBabyModelPair<>(
                      new NewChickenModel(context.bakeLayer(ModelLayers.CHICKEN)),
                      new NewChickenModel(context.bakeLayer(ModelLayers.CHICKEN_BABY))),
              WarmColdVariant.ModelType.COLD, new AdultAndBabyModelPair<>(
                              new NewColdChickenModel(context.bakeLayer(ModelLayers.COLD_CHICKEN)),
                      new NewColdChickenModel(context.bakeLayer(ModelLayers.COLD_CHICKEN_BABY)))));
   }


   public ResourceLocation getTextureLocation(ChickenEntity p_110775_1_) {
      return switch (p_110775_1_.getVariant()) {
          case TEMPERATE -> CHICKEN_LOCATION;
          case WARM -> WARM;
          case COLD -> COLD;
      };
   }

   protected float getBob(ChickenEntity p_77044_1_, float p_77044_2_) {
      float f = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlap, p_77044_1_.flap);
      float f1 = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlapSpeed, p_77044_1_.flapSpeed);
      return (MathHelper.sin(f) + 1.0F) * f1;
   }

   @Override
   public void render(ChickenEntity chicken, float f, float f2, MatrixStack matrix, IRenderTypeBuffer buffer, int i) {


      this.model = this.models.get(chicken.getVariant().getModelType()).getModel(chicken.isBaby());
      super.render(chicken, f, f2, matrix, buffer, i);
   }

}