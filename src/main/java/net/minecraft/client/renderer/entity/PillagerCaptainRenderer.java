package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.PillagerCaptainEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PillagerCaptainRenderer extends IllagerRenderer<PillagerCaptainEntity> {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager_captain.png");
   private static final ResourceLocation PILLAGER_COLD = new ResourceLocation("textures/entity/illager/cold_pillager_captain.png");

   public PillagerCaptainRenderer(EntityRendererManager manager) {
      super(manager, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<>(this));
   }

   public ResourceLocation getTextureLocation(PillagerCaptainEntity pillager) {
      return switch (pillager.getVariant()) {
         case TEMPERATE -> PILLAGER;
         case WARM -> PILLAGER;
         case COLD -> PILLAGER_COLD;
      };
   }
}