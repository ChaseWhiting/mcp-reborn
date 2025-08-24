package net.minecraft.item.dagger;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class DesolateDaggerRenderer extends EntityRenderer<DesolateDaggerEntity> {

    public DesolateDaggerRenderer(EntityRendererManager context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    public void render(DesolateDaggerEntity entity, float entityYaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer source, int lightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, lightIn);
        float ageInTicks = partialTicks + entity.tickCount;
        double stab = Math.max(entity.getStab(partialTicks), Math.sin(ageInTicks * 0.1F) * 0.2F);
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot) + 90.0F));
        poseStack.mulPose(Vector3f.ZN.rotationDegrees((float) (MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot) + 5F * Math.sin(ageInTicks * 0.2F))));
        poseStack.mulPose(Vector3f.ZN.rotationDegrees(45));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        float startAlpha = ageInTicks < 3 ? 0 : (ageInTicks - 3) / 6F;
        float alpha = (float) Math.min(0.6F + stab, Math.min(1F, startAlpha));

        poseStack.translate(stab, stab + Math.cos(ageInTicks * 0.1F) * 0.2F, 0);

        // Use ItemRenderer to render the item
        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.daggerRenderStack,
                ItemCameraTransforms.TransformType.FIXED,
                lightIn,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                source
        );

        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation(DesolateDaggerEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}