package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.blackhole.CircleRenderer;
import net.minecraft.entity.item.BlackholeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlackholeRenderer extends EntityRenderer<BlackholeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/blackhole/blackhole.png");

    public BlackholeRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(BlackholeEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);

        // Render the circle
        float radius = entity.getSize();
        int segments = 360;
        int color = 0x000000; // Black color

        CircleRenderer.drawCircle(matrixStack, buffer, 0, 0, radius, segments, color);

        // Additional rendering logic if needed
    }

    @Override
    public ResourceLocation getTextureLocation(BlackholeEntity entity) {
        return TEXTURE;
    }

    protected void scale(BlackholeEntity blackhole, MatrixStack matrixStack, float v) {
        float scale = blackhole.getSize();
        matrixStack.scale(scale, scale, scale);
    }
}
