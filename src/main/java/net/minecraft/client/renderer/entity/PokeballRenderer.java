package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.PokeballEntity;
import net.minecraft.pokemon.item.pokeball.PokeballModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PokeballRenderer extends EntityRenderer<PokeballEntity> {
    public static final ResourceLocation POKEBALL_TEXTURE = new ResourceLocation("textures/entity/pokeball.png"); // Replace "modid" with your mod's ID
    private final PokeballModel model = new PokeballModel();

    public PokeballRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(PokeballEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();

        matrixStack.translate(0, 1.5, 0);
        // Rotate the model to fix upside-down and backwards orientation
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180F)); // Flip upside-down
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(0.0F)); // Face the correct direction

        // Retrieve the vertex builder for rendering
        IVertexBuilder vertexBuilder = net.minecraft.client.renderer.ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(this.getTextureLocation(entity)), false, false);

        // Render the model
        this.model.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PokeballEntity entity) {
        return POKEBALL_TEXTURE;
    }
}
