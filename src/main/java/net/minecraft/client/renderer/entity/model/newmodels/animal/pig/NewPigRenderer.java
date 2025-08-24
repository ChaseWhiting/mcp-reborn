package net.minecraft.client.renderer.entity.model.newmodels.animal.pig;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.newmodels.EquipmentClientInfo;
import net.minecraft.client.renderer.entity.model.newmodels.animal.AdultAndBabyModelPair;
import net.minecraft.client.renderer.entity.model.newmodels.animal.layer.SimpleEquipmentLayer;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.Map;
import java.util.Optional;

public class NewPigRenderer extends MobRenderer<PigEntity, NewPigModel> {
    private final Map<WarmColdVariant.ModelType, AdultAndBabyModelPair<? extends NewPigModel>> models;
    private static final ResourceLocation PIG_LOCATION = new ResourceLocation("textures/entity/pig/temperate_pig.png");
    private static final ResourceLocation COLD_LOCATION = new ResourceLocation("textures/entity/pig/cold_pig.png");
    private static final ResourceLocation WARM_LOCATION = new ResourceLocation("textures/entity/pig/warm_pig.png");


    public NewPigRenderer(EntityRendererManager context) {
        super(context, new NewPigModel(context.bakeLayer(ModelLayers.PIG)), 0.7f);
        this.models = NewPigRenderer.bakeModels(context);
        this.addLayer(new SimpleEquipmentLayer<>(this,
                new ResourceLocation("textures/entity/pig/saddle.png"), pig -> pig.isSaddled() ? Optional.of(Unit.INSTANCE) : Optional.empty(),
                new NewPigModel(context.bakeLayer(ModelLayers.PIG_SADDLE)), new NewPigModel(context.bakeLayer(ModelLayers.PIG_BABY_SADDLE))));
    }

    private static Map<WarmColdVariant.ModelType, AdultAndBabyModelPair<? extends NewPigModel>> bakeModels(EntityRendererManager context) {
        return Maps.newEnumMap(Map.of(
                WarmColdVariant.ModelType.NORMAL,
                new AdultAndBabyModelPair<>(new NewPigModel(context.bakeLayer(ModelLayers.PIG)),
                        new NewPigModel(context.bakeLayer(ModelLayers.PIG_BABY))),
                WarmColdVariant.ModelType.COLD,
                new AdultAndBabyModelPair<>(new NewColdPigModel(context.bakeLayer(ModelLayers.COLD_PIG)),
                        new NewColdPigModel(context.bakeLayer(ModelLayers.COLD_PIG_BABY))),
                WarmColdVariant.ModelType.WARM,
                new AdultAndBabyModelPair<>(new NewPigModel(context.bakeLayer(ModelLayers.PIG)),
                        new NewPigModel(context.bakeLayer(ModelLayers.PIG_BABY)))));
    }

    @Override
    public void render(PigEntity pig, float f, float f2, MatrixStack matrix, IRenderTypeBuffer buffer, int i) {


        this.model = this.models.get(pig.getVariant().getModelType()).getModel(pig.isBaby());
        super.render(pig, f, f2, matrix, buffer, i);
    }

    @Override
    public ResourceLocation getTextureLocation(PigEntity pig) {
        return switch (pig.getVariant()) {
            case TEMPERATE -> PIG_LOCATION;
            case WARM -> WARM_LOCATION;
            case COLD -> COLD_LOCATION;
        };
    }
}
