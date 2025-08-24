package net.minecraft.entity.warden;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public class LivingEntityEmissiveLayer<T extends LivingEntity, M extends NewHierarchicalModel<T>> extends LayerRenderer<T, M> {
    private final ResourceLocation texture;
    private final AlphaFunction<T> alphaFunction;
    private final DrawSelector<T, M> drawSelector;
    private final Function<ResourceLocation, RenderType> bufferProvider;
    private final boolean alwaysVisible;

    public LivingEntityEmissiveLayer(IEntityRenderer<T, M> p_i50926_1_, ResourceLocation location, AlphaFunction<T> alphaFunction, DrawSelector<T, M> drawSelector, Function<ResourceLocation, RenderType> bufferProvider, boolean alwaysVisible ) {
        super(p_i50926_1_);
        this.texture = location;
        this.alphaFunction = alphaFunction;
        this.drawSelector = drawSelector;
        this.bufferProvider = bufferProvider;
        this.alwaysVisible = alwaysVisible;

    }


    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (t.isInvisible() && !alwaysVisible) {
            return;
        }


            this.getParentModel().renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(t))), n, LivingRenderer.getOverlayCoords(t, 0.0f), 1.0F, 1.0F, 1.0F, 1.0F);


        this.onlyDrawSelectedParts(t);
        IVertexBuilder vertexBuffer = multiBufferSource.getBuffer(bufferProvider.apply(texture));
        this.getParentModel().renderToBuffer(poseStack, vertexBuffer, n, LivingRenderer.getOverlayCoords(t, 0.0f), 1.0F, 1.0f, 1.0f, this.alphaFunction.apply(t, f3, f4));
        this.resetDrawForAllParts();
    }


    private void onlyDrawSelectedParts(T t) {
        List<ModelPart> list = this.drawSelector.getPartsToDraw(this.getParentModel(), t);
        this.getParentModel().root().getAllParts().forEach(mr -> {
            mr.skipDraw = true;
        });
        list.forEach(mr -> {
            mr.skipDraw = false;
        });
    }

    private void resetDrawForAllParts() {
        (this.getParentModel()).root().getAllParts().forEach(modelPart -> {
            modelPart.skipDraw = false;
        });
    }


    public static interface AlphaFunction<T extends LivingEntity> {
        public float apply(T entity, float var, float var2);
    }

    public static interface  DrawSelector<T extends LivingEntity, M extends EntityModel<T>> {
        public List<ModelPart> getPartsToDraw(M var1, T var2);
    }
}
