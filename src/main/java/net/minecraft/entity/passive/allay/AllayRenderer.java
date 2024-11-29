package net.minecraft.entity.passive.allay;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.AllayModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllayRenderer extends MobRenderer<AllayEntity, AllayModel<AllayEntity>> {
   private static final ResourceLocation ALLAY_TEXTURE = new ResourceLocation("textures/entity/allay/allay.png");

   public AllayRenderer(EntityRendererManager p_234551_) {
      super(p_234551_, new AllayModel<>(), 0.4F);
      this.addLayer(new HeldItemLayer<>(this));
   }

   public ResourceLocation getTextureLocation(AllayEntity p_234558_) {
      return ALLAY_TEXTURE;
   }

   protected int getBlockLightLevel(AllayEntity p_234560_, BlockPos p_234561_) {
      return 15;
   }
}