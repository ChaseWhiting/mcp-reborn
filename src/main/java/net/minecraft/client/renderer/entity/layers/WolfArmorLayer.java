package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfArmorLayer<T extends WolfEntity> extends LayerRenderer<T, WolfModel<T>> {
    private static final ResourceLocation WOLF_OVERLAY_TEXTURE = new ResourceLocation("textures/entity/wolf/wolf_armor.png");






    private final WolfModel<T> layerModel = new WolfModel<>();

    public WolfArmorLayer(IEntityRenderer<T, WolfModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, T bogged, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, WOLF_OVERLAY_TEXTURE, matrixStack, renderTypeBuffer, packedLight, bogged, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
    }




    protected RenderType getRenderType(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
