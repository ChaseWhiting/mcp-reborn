package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuEntity;
import net.minecraft.entity.terraria.boss.twins.RetinazerEntity;
import net.minecraft.entity.terraria.boss.twins.RetinazerLaserModel;
import net.minecraft.entity.terraria.boss.twins.RetinazerModel;
import net.minecraft.entity.terraria.monster.demoneye.EyeOfCthulhuModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RetinazerRenderer extends MobRenderer<RetinazerEntity, RetinazerModel> {
   private static final RetinazerModel defaultModel = new RetinazerModel();

   public RetinazerRenderer(EntityRendererManager manager) {
      super(manager, defaultModel, 1.7F);
   }

   public ResourceLocation getTextureLocation(RetinazerEntity entity) {
      return entity.isInPhase2() ? new ResourceLocation("textures/entity/terraria/retinazer_true_form.png") : new ResourceLocation("textures/entity/terraria/retinazer.png");
   }

   @Override
   public void render(RetinazerEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.render(entity, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);

      if (entity.isInPhase2() && this.model != defaultModel.getOtherModel()) {
         this.model = defaultModel.getOtherModel();
      }
   }
}