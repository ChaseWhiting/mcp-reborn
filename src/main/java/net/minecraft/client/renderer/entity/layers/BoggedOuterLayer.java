package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BoggedModel;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoggedOuterLayer<T extends BoggedEntity> extends LayerRenderer<T, BoggedModel<T>> {
    private static final ResourceLocation BOGGED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/bogged_overlay.png");
    private static final ResourceLocation BLOSSOMED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/blossomed_outer.png");
    private static final ResourceLocation WITHERED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/withered_outer.png");
    private static final ResourceLocation PARCHED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/parched_outer.png");
    private static final ResourceLocation FROSTED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/frosted_outer.png");
    private static final ResourceLocation FESTERED_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/skeleton/festered_outer.png");





    private final BoggedModel<T> layerModel = new BoggedModel<>(0.25F, true);

    public BoggedOuterLayer(IEntityRenderer<T, BoggedModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, T bogged, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRenderNoInvisible(this.getParentModel(), this.layerModel, getOverlay(bogged), matrixStack, renderTypeBuffer, packedLight, bogged, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
    }

    public ResourceLocation getOverlay(BoggedEntity entity) {
        return switch (entity.getBoggedType()) {
            case BLOSSOMED -> BLOSSOMED_OVERLAY_TEXTURE;
            case WITHERED -> WITHERED_OVERLAY_TEXTURE;
            case PARCHED -> PARCHED_OVERLAY_TEXTURE;
            case FESTERED, FESTERED_BROWN -> FESTERED_OVERLAY_TEXTURE;
            case FROSTED -> FROSTED_OVERLAY_TEXTURE;
            default -> BOGGED_OVERLAY_TEXTURE;
        };
    }


    protected RenderType getRenderType(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
