package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColdChickenModel<T extends Entity> extends ChickenModel<T> {


   public ColdChickenModel() {
      this.body.texOffs(0, 9)
              .addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F)
              .texOffs(38, 9)
              .addBox(0.0F, 3.0F, -1.0F, 0.0f, 3.0f, 5.0f);
      this.head.texOffs(0, 0)
              .addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f)
              .texOffs(44, 0)
              .addBox(-3.0f, -7.0f, -2.015f, 6.0f, 3.0f, 4.0f);
   }

}