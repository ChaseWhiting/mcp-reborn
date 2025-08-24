package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TNTRenderer extends EntityRenderer<TNTEntity> {
   public TNTRenderer(EntityRendererManager renderManager) {
      super(renderManager);
      this.shadowRadius = 0.5F;
   }

   public void render(TNTEntity tntEntity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
      matrixStack.pushPose();
      matrixStack.translate(0.0D, 0.5D, 0.0D);

      if ((float)tntEntity.getLife() - partialTicks + 1.0F < 10.0F) {
         float scaleFactor = 1.0F - ((float)tntEntity.getLife() - partialTicks + 1.0F) / 10.0F;
         scaleFactor = MathHelper.clamp(scaleFactor, 0.0F, 1.0F);
         scaleFactor = scaleFactor * scaleFactor;
         scaleFactor = scaleFactor * scaleFactor;
         float scale = 1.0F + scaleFactor * 0.3F;
         matrixStack.scale(scale, scale, scale);
      }

      matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
      matrixStack.translate(-0.5D, -0.5D, 0.5D);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));

      BlockState blockState = tntEntity.getBlockState().isPresent() ? tntEntity.getBlockState().get() : Blocks.TNT.defaultBlockState();

      renderWhiteSolidBlock(blockState, matrixStack, buffer, packedLight, tntEntity.getLife() / 5 % 2 == 0);

      matrixStack.popPose();
      super.render(tntEntity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
   }

   public ResourceLocation getTextureLocation(TNTEntity tntEntity) {
      return AtlasTexture.LOCATION_BLOCKS;
   }

   public static void renderWhiteSolidBlock(BlockState blockState, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, boolean isFlashing) {
      int overlay = isFlashing ? OverlayTexture.pack(OverlayTexture.u(1.0F), 10) : OverlayTexture.NO_OVERLAY;
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, matrixStack, buffer, packedLight, overlay);
   }
}