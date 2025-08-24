package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CowModel<T extends Entity> extends QuadrupedModel<T> {
   ModelRenderer left_horn;
   ModelRenderer right_horn;
   ModelRenderer nose;

   public CowModel() {
      // Updated to 64x64 texture size
      super(12, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
      this.texHeight = 64;
      this.texWidth = 64;
      // Head
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, 0.0F);
      this.head.setPos(0.0F, 4.0F, -8.0F);

      this.head.texOffs(0, 32)
              .addBox(-3.0f, 0.99f, -7.0f, 6.0f, 3.0f, 2.0f);
      this.left_horn = new ModelRenderer(this);
      this.right_horn = new ModelRenderer(this);
      this.left_horn.texOffs(22, 0).addBox("left_horn", 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F);
      this.right_horn.texOffs(22, 0).addBox("right_horn", -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F);
      this.nose = new ModelRenderer(this);
      this.nose.visible = false;
      this.head.addChildren(this.left_horn, this.right_horn);
      // Body
      this.body = new ModelRenderer(this, 18, 4);
      this.body.addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, 0.0F);
      this.body.setPos(0.0F, 5.0F, 2.0F);
      this.body.texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F);

      // Legs (mirrored)
      this.leg0 = new ModelRenderer(this, 0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.leg0.setPos(-4.0F, 12.0F, 7.0F);

      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.mirror = true;
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.leg1.setPos(4.0F, 12.0F, 7.0F);

      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.leg2.setPos(-4.0F, 12.0F, -6.0F);

      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.mirror = true;
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.leg3.setPos(4.0F, 12.0F, -6.0F);
   }

   public ModelRenderer getHead() {
      return this.head;
   }
}
