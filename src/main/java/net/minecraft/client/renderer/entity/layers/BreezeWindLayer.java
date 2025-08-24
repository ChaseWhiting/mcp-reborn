package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BreezeModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.breeze.BreezeEntity;
import net.minecraft.util.ResourceLocation;

public class BreezeWindLayer extends LayerRenderer<BreezeEntity, BreezeModel> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze_wind.png");
    private final BreezeModel model;


    public BreezeWindLayer(EntityRendererManager manager, BreezeRenderer model) {
        super(model);
        this.model = new BreezeModel(manager.bakeLayer(ModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, BreezeEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        IVertexBuilder iVertexBuilder = p_225628_2_.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(p_225628_4_.tickCount) % 1.0F, 0.0F));
        this.model.setupAnim(p_225628_4_, 1f, 1f, p_225628_8_, 1f, 1f);
        BreezeRenderer.enable(this.model, this.model.wind()).renderToBuffer(
                p_225628_1_,
                iVertexBuilder,
                p_225628_3_,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }


    private float xOffset(float f) {
        return f * 0.02f;
    }
}
