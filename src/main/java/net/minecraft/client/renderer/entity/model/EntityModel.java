package net.minecraft.client.renderer.entity.model;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
   public float attackTime;
   public boolean riding;
   public boolean young = true;

   protected EntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected EntityModel(Function<ResourceLocation, RenderType> p_i225945_1_) {
      super(p_i225945_1_);
   }

   public abstract void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
   }

   public void copyPropertiesTo(EntityModel<T> p_217111_1_) {
      p_217111_1_.attackTime = this.attackTime;
      p_217111_1_.riding = this.riding;
      p_217111_1_.young = this.young;
   }
}