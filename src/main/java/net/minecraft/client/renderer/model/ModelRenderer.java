package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;

import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
   private float xTexSize = 64.0F;
   private float yTexSize = 32.0F;
   private int xTexOffs;
   private int yTexOffs;
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   public boolean mirror;
   public boolean visible = true;
   private String name;
   private final ObjectList<ModelRenderer.ModelBox> cubes = new ObjectArrayList<>();
   private final ObjectList<ModelRenderer> children = new ObjectArrayList<>();
   private static float scaleX = 1.0F,scaleY = 1.0F, scaleZ = 1.0F;

   public ModelRenderer(Model p_i1173_1_) {
      p_i1173_1_.accept(this);
      this.setTexSize(p_i1173_1_.texWidth, p_i1173_1_.texHeight);
   }

   public ModelRenderer(Model p_i46358_1_, int p_i46358_2_, int p_i46358_3_) {
      this(p_i46358_1_.texWidth, p_i46358_1_.texHeight, p_i46358_2_, p_i46358_3_);
      p_i46358_1_.accept(this);
   }

   public ModelRenderer(int p_i225949_1_, int p_i225949_2_, int p_i225949_3_, int p_i225949_4_) {
      this.setTexSize(p_i225949_1_, p_i225949_2_);
      this.texOffs(p_i225949_3_, p_i225949_4_);
   }

   public ModelRenderer(HierarchicalModel<?> model, String name) {
      this.name = name;
      this.setTexSize(model.texWidth, model.texHeight);
   }

   public ModelRenderer(HierarchicalModel<?> model, String name,  int width, int height) {
      this(model.texWidth, model.texHeight, width, height);
      this.name = name;
      model.accept(this);
   }

   private ModelRenderer() {
   }

   public ModelRenderer createShallowCopy() {
      ModelRenderer modelrenderer = new ModelRenderer();
      modelrenderer.copyFrom(this);
      return modelrenderer;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void applyScale(Vector3f scale) {
      this.scaleX = scale.x();
      this.scaleY = scale.y();
      this.scaleZ = scale.z();
   }

   public void copyFrom(ModelRenderer p_217177_1_) {
      this.xRot = p_217177_1_.xRot;
      this.yRot = p_217177_1_.yRot;
      this.zRot = p_217177_1_.zRot;
      this.x = p_217177_1_.x;
      this.y = p_217177_1_.y;
      this.z = p_217177_1_.z;
   }

   public void addChild(ModelRenderer p_78792_1_) {
      this.children.add(p_78792_1_);
   }

   public ModelRenderer texOffs(int p_78784_1_, int p_78784_2_) {
      this.xTexOffs = p_78784_1_;
      this.yTexOffs = p_78784_2_;
      return this;
   }

   public ModelRenderer addBox(String name, float x, float y, float z, int width, int height, int depth, float delta, int textureOffsetX, int textureOffsetY) {
      this.texOffs(textureOffsetX, textureOffsetY);
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, (float) width, (float) height, (float) depth, delta, delta, delta, this.mirror, false);
      return this;
   }

   public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth) {
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
      return this;
   }

   public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth, boolean mirror) {
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirror, false);
      return this;
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float delta) {
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float delta1, float delta2, float delta3) {
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, width, height, depth, delta1, delta2, delta3, this.mirror, false);
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirror) {
      this.addBox(this.xTexOffs, this.yTexOffs, x, y, z, width, height, depth, delta, delta, delta, mirror, false);
   }

   private void addBox(int textureOffsetX, int textureOffsetY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirror, boolean skip) {
      this.cubes.add(new ModelRenderer.ModelBox(textureOffsetX, textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirror, this.xTexSize, this.yTexSize));
   }

   public void setPos(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int overlay) {
      this.render(matrixStack, vertexBuilder, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(MatrixStack p_228309_1_, IVertexBuilder p_228309_2_, int p_228309_3_, int p_228309_4_, float p_228309_5_, float p_228309_6_, float p_228309_7_, float p_228309_8_) {
      if (this.visible) {
         if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            p_228309_1_.pushPose();
            this.translateAndRotate(p_228309_1_);
            this.compile(p_228309_1_.last(), p_228309_2_, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);

            for(ModelRenderer modelrenderer : this.children) {
               modelrenderer.render(p_228309_1_, p_228309_2_, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
            }

            p_228309_1_.popPose();
         }
      }
   }

   public void translateAndRotate(MatrixStack p_228307_1_) {
      p_228307_1_.translate((double)(this.x / 16.0F), (double)(this.y / 16.0F), (double)(this.z / 16.0F));
      if (this.zRot != 0.0F) {
         p_228307_1_.mulPose(Vector3f.ZP.rotation(this.zRot));
      }

      if (this.yRot != 0.0F) {
         p_228307_1_.mulPose(Vector3f.YP.rotation(this.yRot));
      }

      if (this.xRot != 0.0F) {
         p_228307_1_.mulPose(Vector3f.XP.rotation(this.xRot));
      }

   }

   private void compile(MatrixStack.Entry p_228306_1_, IVertexBuilder p_228306_2_, int p_228306_3_, int p_228306_4_, float p_228306_5_, float p_228306_6_, float p_228306_7_, float p_228306_8_) {
      Matrix4f matrix4f = p_228306_1_.pose();
      Matrix3f matrix3f = p_228306_1_.normal();

      for(ModelRenderer.ModelBox modelrenderer$modelbox : this.cubes) {
         for(ModelRenderer.TexturedQuad modelrenderer$texturedquad : modelrenderer$modelbox.polygons) {
            Vector3f vector3f = modelrenderer$texturedquad.normal.copy();
            vector3f.transform(matrix3f);
            float f = vector3f.x();
            float f1 = vector3f.y();
            float f2 = vector3f.z();

            for(int i = 0; i < 4; ++i) {
               ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertices[i];
               float f3 = modelrenderer$positiontexturevertex.pos.x() / 16.0F;
               float f4 = modelrenderer$positiontexturevertex.pos.y() / 16.0F;
               float f5 = modelrenderer$positiontexturevertex.pos.z() / 16.0F;
               Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
               vector4f.transform(matrix4f);
               p_228306_2_.vertex(vector4f.x(), vector4f.y(), vector4f.z(), p_228306_5_, p_228306_6_, p_228306_7_, p_228306_8_, modelrenderer$positiontexturevertex.u, modelrenderer$positiontexturevertex.v, p_228306_4_, p_228306_3_, f, f1, f2);
            }
         }
      }

   }

   public ModelRenderer setTexSize(int p_78787_1_, int p_78787_2_) {
      this.xTexSize = (float)p_78787_1_;
      this.yTexSize = (float)p_78787_2_;
      return this;
   }

   public ModelRenderer.ModelBox getRandomCube(Random p_228310_1_) {
      return this.cubes.get(p_228310_1_.nextInt(this.cubes.size()));
   }

   @OnlyIn(Dist.CLIENT)
   public static class ModelBox {
      private final ModelRenderer.TexturedQuad[] polygons;
      public final float minX;
      public final float minY;
      public final float minZ;
      public final float maxX;
      public final float maxY;
      public final float maxZ;

      public ModelBox(int p_i225950_1_, int p_i225950_2_, float p_i225950_3_, float p_i225950_4_, float p_i225950_5_, float p_i225950_6_, float p_i225950_7_, float p_i225950_8_, float p_i225950_9_, float p_i225950_10_, float p_i225950_11_, boolean p_i225950_12_, float p_i225950_13_, float p_i225950_14_) {
         this.minX = p_i225950_3_;
         this.minY = p_i225950_4_;
         this.minZ = p_i225950_5_;
         this.maxX = p_i225950_3_ + p_i225950_6_;
         this.maxY = p_i225950_4_ + p_i225950_7_;
         this.maxZ = p_i225950_5_ + p_i225950_8_;
         this.polygons = new ModelRenderer.TexturedQuad[6];
         float f = p_i225950_3_ + p_i225950_6_;
         float f1 = p_i225950_4_ + p_i225950_7_;
         float f2 = p_i225950_5_ + p_i225950_8_;
         p_i225950_3_ = p_i225950_3_ - p_i225950_9_;
         p_i225950_4_ = p_i225950_4_ - p_i225950_10_;
         p_i225950_5_ = p_i225950_5_ - p_i225950_11_;
         f = f + p_i225950_9_;
         f1 = f1 + p_i225950_10_;
         f2 = f2 + p_i225950_11_;
         if (p_i225950_12_) {
            float f3 = f;
            f = p_i225950_3_;
            p_i225950_3_ = f3;
         }

         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex7 = new ModelRenderer.PositionTextureVertex(p_i225950_3_, p_i225950_4_, p_i225950_5_, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = new ModelRenderer.PositionTextureVertex(f, p_i225950_4_, p_i225950_5_, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex1 = new ModelRenderer.PositionTextureVertex(f, f1, p_i225950_5_, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex2 = new ModelRenderer.PositionTextureVertex(p_i225950_3_, f1, p_i225950_5_, 8.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex3 = new ModelRenderer.PositionTextureVertex(p_i225950_3_, p_i225950_4_, f2, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex4 = new ModelRenderer.PositionTextureVertex(f, p_i225950_4_, f2, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex5 = new ModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex6 = new ModelRenderer.PositionTextureVertex(p_i225950_3_, f1, f2, 8.0F, 0.0F);
         float f4 = (float)p_i225950_1_;
         float f5 = (float)p_i225950_1_ + p_i225950_8_;
         float f6 = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_;
         float f7 = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_6_;
         float f8 = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_;
         float f9 = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_ + p_i225950_6_;
         float f10 = (float)p_i225950_2_;
         float f11 = (float)p_i225950_2_ + p_i225950_8_;
         float f12 = (float)p_i225950_2_ + p_i225950_8_ + p_i225950_7_;
         this.polygons[2] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex}, f5, f10, f6, f11, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.DOWN);
         this.polygons[3] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex5}, f6, f11, f7, f10, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.UP);
         this.polygons[1] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex2}, f4, f11, f5, f12, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.WEST);
         this.polygons[4] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex1}, f5, f11, f6, f12, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.NORTH);
         this.polygons[0] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex5}, f6, f11, f8, f12, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.EAST);
         this.polygons[5] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex5, modelrenderer$positiontexturevertex6}, f8, f11, f9, f12, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.SOUTH);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PositionTextureVertex {
      public final Vector3f pos;
      public final float u;
      public final float v;

      public PositionTextureVertex(float p_i1158_1_, float p_i1158_2_, float p_i1158_3_, float p_i1158_4_, float p_i1158_5_) {
         this(new Vector3f(p_i1158_1_, p_i1158_2_, p_i1158_3_), p_i1158_4_, p_i1158_5_);
      }

      public ModelRenderer.PositionTextureVertex remap(float p_78240_1_, float p_78240_2_) {
         return new ModelRenderer.PositionTextureVertex(this.pos, p_78240_1_, p_78240_2_);
      }

      public PositionTextureVertex(Vector3f p_i225952_1_, float p_i225952_2_, float p_i225952_3_) {
         this.pos = p_i225952_1_;
         this.u = p_i225952_2_;
         this.v = p_i225952_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexturedQuad {
      public final ModelRenderer.PositionTextureVertex[] vertices;
      public final Vector3f normal;

      public TexturedQuad(ModelRenderer.PositionTextureVertex[] p_i225951_1_, float p_i225951_2_, float p_i225951_3_, float p_i225951_4_, float p_i225951_5_, float p_i225951_6_, float p_i225951_7_, boolean p_i225951_8_, Direction p_i225951_9_) {
         this.vertices = p_i225951_1_;
         float f = 0.0F / p_i225951_6_;
         float f1 = 0.0F / p_i225951_7_;
         p_i225951_1_[0] = p_i225951_1_[0].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_3_ / p_i225951_7_ + f1);
         p_i225951_1_[1] = p_i225951_1_[1].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_3_ / p_i225951_7_ + f1);
         p_i225951_1_[2] = p_i225951_1_[2].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_5_ / p_i225951_7_ - f1);
         p_i225951_1_[3] = p_i225951_1_[3].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_5_ / p_i225951_7_ - f1);
         if (p_i225951_8_) {
            int i = p_i225951_1_.length;

            for(int j = 0; j < i / 2; ++j) {
               ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = p_i225951_1_[j];
               p_i225951_1_[j] = p_i225951_1_[i - 1 - j];
               p_i225951_1_[i - 1 - j] = modelrenderer$positiontexturevertex;
            }
         }

         this.normal = p_i225951_9_.step();
         if (p_i225951_8_) {
            this.normal.mul(-1.0F, 1.0F, 1.0F);
         }

      }
   }
}