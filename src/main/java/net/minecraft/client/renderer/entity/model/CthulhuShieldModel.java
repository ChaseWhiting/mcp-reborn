package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CthulhuShieldModel extends Model {
   private final ModelRenderer plate;
   private final ModelRenderer handle;

   public CthulhuShieldModel() {
      super(RenderType::entitySolid);
      this.texWidth = 64;
      this.texHeight = 64;
      plate = new ModelRenderer(this);
      plate.setPos(0.0F, 13.0F, -1.0F);
      plate.texOffs(0, 0).addBox(-9.0F, -11.0F, -3.0F, 18.0F, 18.0F, 2.0F, 0.0F, false);
      plate.texOffs(0, 40).addBox(-11.0F, -15.0F, -3.0F, 2.0F, 22.0F, 2.0F, 0.0F, false);
      plate.texOffs(56, 40).addBox(9.0F, -15.0F, -3.0F, 2.0F, 22.0F, 2.0F, 0.0F, false);
      plate.texOffs(0, 28).addBox(-13.0F, -7.0F, -3.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
      plate.texOffs(12, 28).addBox(-15.0F, -3.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(0, 28).addBox(11.0F, -7.0F, -3.0F, 2.0F, 10.0F, 2.0F, 0.0F, true);
      plate.texOffs(12, 28).addBox(13.0F, -3.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);
      plate.texOffs(12, 28).addBox(-9.0F, 7.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(12, 28).addBox(7.0F, 7.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(11, 60).addBox(-7.0F, 9.0F, -3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(11, 60).addBox(3.0F, 9.0F, -3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(11, 60).addBox(-3.0F, 11.0F, -3.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(11, 56).addBox(-3.0F, 9.0F, -3.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(8, 52).addBox(-7.0F, 7.0F, -3.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(12, 32).addBox(-9.0F, -15.0F, -3.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
      plate.texOffs(32, 36).addBox(-7.0F, -13.0F, -3.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(36, 32).addBox(-5.0F, -15.0F, -3.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(40, 28).addBox(-3.0F, -17.0F, -3.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(56, 40).addBox(-1.0F, -19.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      plate.texOffs(12, 32).addBox(7.0F, -15.0F, -3.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

      handle = new ModelRenderer(this);
      handle.setPos(0.0F, 13.0F, -1.0F);
      handle.texOffs(9, 42).addBox(-5.0F, -1.0F, -1.0F, 10.0F, 2.0F, 8.0F, 0.0F, false);
   }

   public ModelRenderer plate() {
      return this.plate;
   }

   public ModelRenderer handle() {
      return this.handle;
   }

   public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
      this.plate.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
      this.handle.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
   }
}