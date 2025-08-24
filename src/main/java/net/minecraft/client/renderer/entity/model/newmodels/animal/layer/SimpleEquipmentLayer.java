package net.minecraft.client.renderer.entity.model.newmodels.animal.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.newmodels.EquipmentClientInfo;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;

public class SimpleEquipmentLayer<T extends LivingEntity, RM extends EntityModel<T>, EM extends EntityModel<T>>
extends LayerRenderer<T, RM> {



    private final Function<T, Optional<Unit>> shouldRender;
    private final Function<T, ResourceLocation> textureGetter;

    private final EM adultModel;
    private final EM babyModel;

    public SimpleEquipmentLayer(IEntityRenderer<T, RM> renderLayerParent, ResourceLocation texture, Function<T, Optional<Unit>> function, EM EM, EM EM2) {
        super(renderLayerParent);
        this.shouldRender = function;
        this.adultModel = EM;
        this.babyModel = EM2;
        this.textureGetter = mob -> texture;
    }

    public SimpleEquipmentLayer(IEntityRenderer<T, RM> renderLayerParent, Function<T, ResourceLocation> texture, Function<T, Optional<Unit>> function, EM EM, EM EM2) {
        super(renderLayerParent);
        this.shouldRender = function;
        this.adultModel = EM;
        this.babyModel = EM2;
        this.textureGetter = texture;
    }

    public SimpleEquipmentLayer(IEntityRenderer<T, RM> renderLayerParent, ResourceLocation texture, EM EM, Function<T, Optional<Unit>> function) {
        this(renderLayerParent, texture, function, EM, EM);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {

        if (shouldRender.apply(entity).isEmpty()) return;

        EM EM = entity.isBaby() ? this.babyModel : this.adultModel;

        //EM.setupAnim(entity, );

        getParentModel().copyPropertiesTo(EM);
        EM.prepareMobModel(entity, p_225628_5_, p_225628_6_, p_225628_7_);
        EM.setupAnim(entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(this.textureGetter.apply(entity)));
        EM.renderToBuffer(matrixStack, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

    }
}
