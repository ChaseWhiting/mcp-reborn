package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.AbstractBipedModelMaker;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Mob;
import net.minecraft.entity.monster.ColetteEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColetteRenderer extends AbstractBipedModelRenderer<ColetteEntity, AbstractBipedModelMaker<ColetteEntity>> {
   public ColetteRenderer(EntityRendererManager p_i46127_1_) {
      super(p_i46127_1_,
              new AbstractBipedModelMaker<ColetteEntity>(0.0F, 0.0F, 64, 32) {
              },
              new AbstractBipedModelMaker<ColetteEntity>(0.5F, 0.0F, 64, 32) {
              },
              new AbstractBipedModelMaker<ColetteEntity>(1.0F, 0.0F, 64, 32) {
              });
   }

}
