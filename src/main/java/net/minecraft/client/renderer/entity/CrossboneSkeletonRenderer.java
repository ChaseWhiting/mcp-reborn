package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CrossboneSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrossboneSkeletonRenderer extends BipedRenderer<CrossboneSkeletonEntity, SkeletonModel<CrossboneSkeletonEntity>> {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public CrossboneSkeletonRenderer(EntityRendererManager p_i46143_1_) {
      super(p_i46143_1_, new SkeletonModel<>(), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new SkeletonModel(0.5F, true), new SkeletonModel(1.0F, true)));
   }

   public ResourceLocation getTextureLocation(CrossboneSkeletonEntity p_110775_1_) {
      return SKELETON_LOCATION;
   }
}