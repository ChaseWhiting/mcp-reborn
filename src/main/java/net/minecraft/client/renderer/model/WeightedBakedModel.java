package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements IBakedModel {
   private final int totalWeight;
   private final List<WeightedBakedModel.WeightedModel> list;
   private final IBakedModel wrapped;

   public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> p_i46073_1_) {
      this.list = p_i46073_1_;
      this.totalWeight = WeightedRandom.getTotalWeight(p_i46073_1_);
      this.wrapped = (p_i46073_1_.get(0)).model;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      return (WeightedRandom.getWeightedItem(this.list, Math.abs((int)p_200117_3_.nextLong()) % this.totalWeight)).model.getQuads(p_200117_1_, p_200117_2_, p_200117_3_);
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.wrapped.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public ItemCameraTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.wrapped.getOverrides();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedBakedModel.WeightedModel> list = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable IBakedModel p_177677_1_, int p_177677_2_) {
         if (p_177677_1_ != null) {
            this.list.add(new WeightedBakedModel.WeightedModel(p_177677_1_, p_177677_2_));
         }

         return this;
      }

      @Nullable
      public IBakedModel build() {
         if (this.list.isEmpty()) {
            return null;
         } else {
            return (IBakedModel)(this.list.size() == 1 ? (this.list.get(0)).model : new WeightedBakedModel(this.list));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WeightedModel extends WeightedRandom.Item {
      protected final IBakedModel model;

      public WeightedModel(IBakedModel p_i46763_1_, int p_i46763_2_) {
         super(p_i46763_2_);
         this.model = p_i46763_1_;
      }
   }
}