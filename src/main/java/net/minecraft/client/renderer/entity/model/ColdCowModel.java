package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColdCowModel<T extends Entity> extends CowModel<T> {

   public ColdCowModel() {
      super();
      this.texHeight = 64;
      this.texWidth = 64;
      this.right_horn = new ModelRenderer(this);
      this.right_horn.texOffs(0, 32).addBox(-1.5f, -4.5f, -0.5f, 2.0f, 6.0f, 2.0f);
      this.left_horn = new ModelRenderer(this);
      this.left_horn.mirror = true;
      this.left_horn.texOffs(0, 32).addBox(-1.5f, -3.0f, -0.5f, 2.0f, 6.0f, 2.0f);
      this.body.texOffs(20, 32)
              .addBox("fur", -6.0f, -10f, -7f, 12f, 18f, 10f, 0.5f);

      this.nose = new ModelRenderer(this);
      this.nose.texOffs(8, 32).addBox(-3.0f, 0.99f, -7.0f, 6.0f, 3.0f, 2.0f);

      this.left_horn.setPos(5.5f, -2.5f, -5f);
      this.left_horn.setRotationAngle(1.5708f, 0.0f, 0.0f);

      this.right_horn.setPos(-4.5f, -2.3f, -3.5f);
      this.right_horn.setRotationAngle(1.5708f, 0.0f, 0.0f);

      this.head.addChildren(left_horn, right_horn, nose);
   }
}
