package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.item.BlackholeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteRenderer<T extends Entity & IRendersAsItem> extends EntityRenderer<T> {
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public SpriteRenderer(EntityRendererManager renderManager, ItemRenderer itemRenderer, float scale, boolean fullBright) {
      super(renderManager);
      this.itemRenderer = itemRenderer;
      this.scale = scale;
      this.fullBright = fullBright;
   }

   public SpriteRenderer(EntityRendererManager renderManager, ItemRenderer itemRenderer) {
      this(renderManager, itemRenderer, 1.0F, false);
   }

   @Override
   protected int getBlockLightLevel(T entity, BlockPos blockPos) {
      return this.fullBright ? 15 : super.getBlockLightLevel(entity, blockPos);
   }

   @Override
   public void render(T entity, float yaw, float ticks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      boolean isBlackholeEntity = entity instanceof BlackholeEntity;

      if (entity.tickCount >= 2 || isBlackholeEntity || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
         matrixStack.pushPose();

         if (!isBlackholeEntity) {
            matrixStack.scale(this.scale, this.scale, this.scale);
            matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         } else {
            float scale = ((BlackholeEntity) entity).getSize();
            matrixStack.scale(scale, scale, scale);
         }

         this.itemRenderer.renderStatic(entity.getItem(), ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
         matrixStack.popPose();
         super.render(entity, yaw, ticks, matrixStack, buffer, packedLight);
      }
   }

   @Override
   public ResourceLocation getTextureLocation(T entity) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
