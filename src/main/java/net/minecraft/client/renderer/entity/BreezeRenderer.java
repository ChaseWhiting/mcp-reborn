package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.client.renderer.entity.model.BreezeModel;
import net.minecraft.entity.monster.breeze.BreezeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreezeRenderer extends MobRenderer<BreezeEntity, BreezeModel> {
   private static final ResourceLocation BREEZE_TEXTURE = new ResourceLocation("textures/entity/breeze/breeze.png");

   public BreezeRenderer(EntityRendererManager renderManager) {
      super(renderManager, new BreezeModel(renderManager.bakeLayer(ModelLayers.BREEZE)), 0.5F);
      this.addLayer(new BreezeWindLayer(renderManager, this));
      this.addLayer(new BreezeEyesLayer(this));
   }

   @Override
   public ResourceLocation getTextureLocation(BreezeEntity entity) {
      return BREEZE_TEXTURE;
   }

   public static BreezeModel enable(BreezeModel breezeModel, ModelPart... modelPartArray) {
      breezeModel.head().visible = false;
      breezeModel.eyes().visible = false;
      breezeModel.rods().visible = false;
      breezeModel.wind().visible = false;
      for (ModelPart modelPart : modelPartArray) {
         modelPart.visible = true;
      }
      return breezeModel;
   }
}
