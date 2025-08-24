package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

public class GuiFullBrightLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    public GuiFullBrightLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        // Render again, but forcibly full bright
        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.getParentModel().renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
