package net.minecraft.client.renderer.entity.newrenderers.ghast;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;

public class RopesLayer<M extends HappyGhastModel<HappyGhastEntity>>
extends LayerRenderer<HappyGhastEntity, M> {


    private final RenderType ropes;
    private final HappyGhastModel<HappyGhastEntity> adultModel;
    private final HappyGhastModel<HappyGhastEntity> babyModel;

    public RopesLayer(IEntityRenderer<HappyGhastEntity, M> renderLayerParent, EntityModelSet entityModelSet, ResourceLocation resourceLocation) {
        super(renderLayerParent);
        this.ropes = RenderType.entityCutoutNoCull(resourceLocation);
        this.adultModel = new HappyGhastModel<>(entityModelSet.bakeLayer(ModelLayers.HAPPY_GHAST_ROPES));
        this.babyModel = new HappyGhastModel<>(entityModelSet.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_ROPES));
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225628_3_, HappyGhastEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {




        //EM.setupAnim(entity, );

        if (!entity.isLeashHolder() || !entity.getItemBySlot(EquipmentSlotType.CHEST).getItem().is(ItemTags.HARNESSES)) {
            return;
        }
        HappyGhastModel<HappyGhastEntity> happyGhastModel = entity.isBaby() ? this.babyModel : this.adultModel;

        getParentModel().copyPropertiesTo(happyGhastModel);
        happyGhastModel.prepareMobModel(entity, p_225628_5_, p_225628_6_, p_225628_7_);
        happyGhastModel.setupAnim(entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
        happyGhastModel.renderToBuffer(matrixStack, buffer.getBuffer(ropes), p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

    }
}
