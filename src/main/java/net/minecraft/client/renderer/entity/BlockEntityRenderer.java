package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.AbstractFakeBlockEntity;
import net.minecraft.util.ResourceLocation;

public class BlockEntityRenderer extends MobRenderer<AbstractFakeBlockEntity, BlockRenderer> {
    private final ResourceLocation TEXTURE;

    public BlockEntityRenderer(EntityRendererManager context, ResourceLocation texture) {
        super(context, new BlockRenderer(), 0F); // Call the superclass constructor first
        this.TEXTURE = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractFakeBlockEntity entity) {
        return TEXTURE;
    }

}

class BlockRenderer extends EntityModel<AbstractFakeBlockEntity> {
    private final ModelRenderer bb_main;

    public BlockRenderer() {
        this.texWidth = 64;
        this.texHeight = 64;

        this.bb_main = new ModelRenderer(this);
        this.bb_main.setPos(0.0F, 24.0F, 0.0F);
        this.bb_main.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }


    @Override
    public void setupAnim(AbstractFakeBlockEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

    }

}
