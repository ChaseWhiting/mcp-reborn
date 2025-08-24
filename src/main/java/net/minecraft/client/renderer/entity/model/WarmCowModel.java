package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WarmCowModel<T extends Entity> extends CowModel<T> {

   public WarmCowModel() {
      super();
      this.texHeight = 64;
      this.texWidth = 64;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.setPos(0.0F, 4.0F, -8.0F);

      this.head.texOffs(0, 0)
              .addBox(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f)
              .texOffs(0, 32)
              .addBox(-3.0f, 0.99f, -7.0f, 6.0f, 3.0f, 2.0f)
              .texOffs(27, 0)
              .addBox(-8.0f, -3.0f, -5.0f, 4.0f, 2.0f, 2.0f)
              .texOffs(39, 0)
              .addBox(-8.0f, -5.0f, -5.0f, 2.0f, 2.0f, 2.0f)
              .texOffs(27, 0)
              .mirror().addBox(4.0f, -3.0f, -5.0f, 4.0f, 2.0f, 2.0f)
              .mirror(false)
              .texOffs(39, 0)
              .mirror()
              .addBox(6.0f, -5.0f, -5.0f, 2.0f, 2.0f, 2.0f)
              .mirror(false);

      this.right_horn = new ModelRenderer(this);
      this.left_horn = new ModelRenderer(this);
      this.right_horn.visible = false;
      this.left_horn.visible = false;

      this.head.addChildren(left_horn, right_horn, nose);
   }
}
