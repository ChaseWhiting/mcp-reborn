package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.GrapplingHookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/grappling_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

    public GrapplingHookRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(GrapplingHookEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        PlayerEntity playerEntity = (PlayerEntity) entity.getOwner();
        if (playerEntity != null) {
            matrixStack.pushPose();
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            MatrixStack.Entry matrixStackEntry = matrixStack.last();
            Matrix4f matrix4f = matrixStackEntry.pose();
            Matrix3f matrix3f = matrixStackEntry.normal();
            IVertexBuilder vertexBuilder = buffer.getBuffer(RENDER_TYPE);
            vertex(vertexBuilder, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
            vertex(vertexBuilder, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
            vertex(vertexBuilder, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
            vertex(vertexBuilder, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
            matrixStack.popPose();
            int handSideMultiplier = playerEntity.getMainArm() == HandSide.RIGHT ? 1 : -1;
            ItemStack itemStack = playerEntity.getMainHandItem();
            if (itemStack.getItem() != Items.GRAPPLING_HOOK) {
                handSideMultiplier = -handSideMultiplier;
            }

            float attackAnimationProgress = playerEntity.getAttackAnim(partialTicks);
            float swingProgress = MathHelper.sin(MathHelper.sqrt(attackAnimationProgress) * (float)Math.PI);
            float bodyYaw = MathHelper.lerp(partialTicks, playerEntity.yBodyRotO, playerEntity.yBodyRot) * ((float)Math.PI / 180F);
            double xDirection = (double)MathHelper.sin(bodyYaw);
            double zDirection = (double)MathHelper.cos(bodyYaw);
            double offsetX = (double)handSideMultiplier * 0.35D;
            double offsetY = 0.8D;
            double xPos, yPos, zPos;
            float eyeHeight;
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && playerEntity == Minecraft.getInstance().player) {
                double fov = this.entityRenderDispatcher.options.fov;
                fov = fov / 100.0D;
                Vector3d vector3d = new Vector3d((double)handSideMultiplier * -0.36D * fov, -0.045D * fov, 0.4D);
                vector3d = vector3d.xRot(-MathHelper.lerp(partialTicks, playerEntity.xRotO, playerEntity.xRot) * ((float)Math.PI / 180F));
                vector3d = vector3d.yRot(-MathHelper.lerp(partialTicks, playerEntity.yRotO, playerEntity.yRot) * ((float)Math.PI / 180F));
                vector3d = vector3d.yRot(swingProgress * 0.5F);
                vector3d = vector3d.xRot(-swingProgress * 0.7F);
                xPos = MathHelper.lerp((double)partialTicks, playerEntity.xo, playerEntity.getX()) + vector3d.x;
                yPos = MathHelper.lerp((double)partialTicks, playerEntity.yo, playerEntity.getY()) + vector3d.y;
                zPos = MathHelper.lerp((double)partialTicks, playerEntity.zo, playerEntity.getZ()) + vector3d.z;
                eyeHeight = playerEntity.getEyeHeight();
            } else {
                xPos = MathHelper.lerp((double)partialTicks, playerEntity.xo, playerEntity.getX()) - zDirection * offsetX - xDirection * 0.8D;
                yPos = playerEntity.yo + (double)playerEntity.getEyeHeight() + (playerEntity.getY() - playerEntity.yo) * (double)partialTicks - 0.45D;
                zPos = MathHelper.lerp((double)partialTicks, playerEntity.zo, playerEntity.getZ()) - xDirection * offsetX + zDirection * 0.8D;
                eyeHeight = playerEntity.isCrouching() ? -0.1875F : 0.0F;
            }

            double hookX = MathHelper.lerp((double)partialTicks, entity.xo, entity.getX());
            double hookY = MathHelper.lerp((double)partialTicks, entity.yo, entity.getY()) + 0.25D;
            double hookZ = MathHelper.lerp((double)partialTicks, entity.zo, entity.getZ());
            float deltaX = (float)(xPos - hookX);
            float deltaY = (float)(yPos - hookY) + eyeHeight;
            float deltaZ = (float)(zPos - hookZ);
            IVertexBuilder lineBuilder = buffer.getBuffer(RenderType.lines());
            Matrix4f matrix4f1 = matrixStack.last().pose();
            int segments = 16;

            for(int i = 0; i < segments; ++i) {
                stringVertex(deltaX, deltaY, deltaZ, lineBuilder, matrix4f1, fraction(i, segments));
                stringVertex(deltaX, deltaY, deltaZ, lineBuilder, matrix4f1, fraction(i + 1, segments));
            }

            matrixStack.popPose();
            super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    private static float fraction(int numerator, int denominator) {
        return (float)numerator / (float)denominator;
    }

    private static void vertex(IVertexBuilder vertexBuilder, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float x, int y, int u, int v) {
        vertexBuilder.vertex(matrix4f, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)u, (float)v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void stringVertex(float x, float y, float z, IVertexBuilder vertexBuilder, Matrix4f matrix4f, float fraction) {
        vertexBuilder.vertex(matrix4f, x * fraction, y * (fraction * fraction + fraction) * 0.5F + 0.25F, z * fraction).color(0, 0, 0, 255).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(GrapplingHookEntity entity) {
        return TEXTURE_LOCATION;
    }
}
