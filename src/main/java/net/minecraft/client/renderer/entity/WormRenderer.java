package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.terraria.creature.WormEntity;
import net.minecraft.entity.terraria.creature.WormPartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;


public class WormRenderer extends EntityRenderer<WormEntity> {
    private static final ResourceLocation HEAD_TEXTURE = new ResourceLocation("textures/entity/terraria/worm/giant_worm_head.png");
    private static final ResourceLocation BODY_TEXTURE = new ResourceLocation("textures/entity/terraria/worm/giant_worm_body.png");
    private static final ResourceLocation TAIL_TEXTURE = new ResourceLocation("textures/entity/terraria/worm/giant_worm_tail.png");

    private final WormHeadModel headModel = new WormHeadModel();
    private final WormBodyModel bodyModel = new WormBodyModel();
    private final WormTailModel tailModel = new WormTailModel();

    public WormRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(WormEntity wormEntity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        WormPartEntity[] parts = wormEntity.getParts();
        
        for (int i = 0; i < parts.length; i++) {
            WormPartEntity part = parts[i];
            matrixStack.pushPose();

            // Get position and rotation offsets for each part
            applyTransformations(matrixStack, part, entityYaw, i);

            // Choose model and texture based on part type
            EntityModel<WormEntity> model;
            ResourceLocation texture;
            if (i == 0) {
                model = headModel;
                texture = HEAD_TEXTURE;
            } else if (i == parts.length - 1) {
                model = tailModel;
                texture = TAIL_TEXTURE;
            } else {
                model = bodyModel;
                texture = BODY_TEXTURE;
            }

            renderPart(matrixStack, buffer, model, texture, part, packedLight);

            matrixStack.popPose();
        }

        super.render(wormEntity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    private void renderPart(MatrixStack matrixStack, IRenderTypeBuffer buffer, EntityModel<WormEntity> model, ResourceLocation texture, WormPartEntity part, int packedLight) {
        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void applyTransformations(MatrixStack matrixStack, WormPartEntity part, float entityYaw, int partIndex) {
        // Translate the part to its position relative to the main entity
        matrixStack.translate(part.getX() - part.getParentWorm().getX(),
                part.getY() - part.getParentWorm().getY(),
                part.getZ() - part.getParentWorm().getZ());

        // Apply rotation based on the yaw of the main worm entity to align horizontally
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-entityYaw));

        // Apply the pitch (xRot) of the part for vertical alignment if needed
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(part.xRot));
    }

    @Override
    public ResourceLocation getTextureLocation(WormEntity entity) {
        // Not directly used, as each part has its own texture
        return HEAD_TEXTURE;
    }
}
