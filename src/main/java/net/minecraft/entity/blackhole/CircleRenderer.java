package net.minecraft.entity.blackhole;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

public class CircleRenderer {
    public static void drawCircle(MatrixStack matrixStack, IRenderTypeBuffer buffer, float centerX, float centerY, float radius, int segments, int color) {
        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityTranslucentCull(new ResourceLocation("textures/entity/blackhole/blackhole.png")));
        float angleStep = (float) (2 * Math.PI / segments);

        float red = (color >> 16 & 0xFF) / 255.0f;
        float green = (color >> 8 & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = 1.0f;

        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 0);

        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;

            float x1 = (float) (Math.cos(angle1) * radius);
            float y1 = (float) (Math.sin(angle1) * radius);
            float x2 = (float) (Math.cos(angle2) * radius);
            float y2 = (float) (Math.sin(angle2) * radius);

            vertexBuilder.vertex(matrixStack.last().pose(), x1, y1, 0.0f)
                    .color(red, green, blue, alpha)
                    .uv(0.0f, 0.0f) // Constant UV coordinates for a 1x1 texture
                    .endVertex();
            vertexBuilder.vertex(matrixStack.last().pose(), x2, y2, 0.0f)
                    .color(red, green, blue, alpha)
                    .uv(0.0f, 0.0f) // Constant UV coordinates for a 1x1 texture
                    .endVertex();
        }

        matrixStack.popPose();
    }
}
