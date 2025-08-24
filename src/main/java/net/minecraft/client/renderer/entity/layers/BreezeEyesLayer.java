package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BreezeModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.breeze.BreezeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreezeEyesLayer extends LayerRenderer<BreezeEntity, BreezeModel> {
    private static final RenderType BREEZE_EYES = RenderType.breezeEyes(new ResourceLocation("textures/entity/breeze/breeze_eyes.png"));

    public BreezeEyesLayer(IEntityRenderer<BreezeEntity, BreezeModel> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, BreezeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder iVertexBuilder = buffer.getBuffer(BREEZE_EYES);
        BreezeModel breezeModel = this.getParentModel();
        BreezeRenderer.enable(breezeModel, breezeModel.head(), breezeModel.eyes()).renderToBuffer(matrixStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
