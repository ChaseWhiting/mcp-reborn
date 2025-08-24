package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.model.newmodels.HumanoidArmorModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.chicken.NewChickenModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.chicken.NewColdChickenModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.pig.NewColdPigModel;
import net.minecraft.client.renderer.entity.model.newmodels.animal.pig.NewPigModel;
import net.minecraft.client.renderer.entity.model.newmodels.monster.NewCreakingModel;
import net.minecraft.client.renderer.entity.model.newmodels.player.NewPlayerModel;
import net.minecraft.client.renderer.entity.newrenderers.ghast.HappyGhastHarnessModel;
import net.minecraft.client.renderer.entity.newrenderers.ghast.HappyGhastModel;
import net.minecraft.client.renderer.entity.newrenderers.ghast.NewGhastModel;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.tileentity.DecoratedPotRenderer;
import net.minecraft.entity.allay.AllayModel;
import net.minecraft.entity.frog.TadpoleModel;
import net.minecraft.entity.monster.creaking.CreakingModel;
import net.minecraft.entity.warden.WardenModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LayerDefinitions {
    private static final CubeDeformation FISH_PATTERN_DEFORMATION = new CubeDeformation(0.008f);
    private static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0f);
    private static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5f);

    public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();

        LayerDefinition layerDefinition2 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
        LayerDefinition layerDefinition4 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32);


        builder.put(ModelLayers.WARDEN, WardenModel.createBodyLayer());
        builder.put(ModelLayers.CREAKING, CreakingModel.createBodyLayer());
        builder.put(ModelLayers.CREAKING_2, NewCreakingModel.createBodyLayer());

        builder.put(ModelLayers.GHAST, NewGhastModel.createBodyLayer());
        builder.put(ModelLayers.ALLAY, AllayModel.createBodyLayer());
        builder.put(ModelLayers.CAMEL, CamelModel.createBodyLayer(new CubeDeformation(0.1F)));

        builder.put(ModelLayers.CAMEL_CARPET, CamelModel.createBodyLayer(new CubeDeformation(0.11F)));
        builder.put(ModelLayers.CAMEL_BABY_CARPET, CamelModel.createBodyLayer(new CubeDeformation(0.05F)));

        builder.put(ModelLayers.CAMEL_LEASH, CamelModel.createBodyLayer(new CubeDeformation(0.1778F)));
        builder.put(ModelLayers.CAMEL_BABY_LEASH, CamelModel.createBodyLayer(new CubeDeformation(0.02F)));

        builder.put(ModelLayers.HAPPY_GHAST, HappyGhastModel.createBodyLayer(false, CubeDeformation.NONE));
        builder.put(ModelLayers.HAPPY_GHAST_BABY, HappyGhastModel.createBodyLayer(true, CubeDeformation.NONE).apply(HappyGhastModel.BABY_TRANSFORMER));
        builder.put(ModelLayers.HAPPY_GHAST_HARNESS, HappyGhastHarnessModel.createHarnessLayer(false));
        builder.put(ModelLayers.HAPPY_GHAST_BABY_HARNESS, HappyGhastHarnessModel.createHarnessLayer(true).apply(HappyGhastModel.BABY_TRANSFORMER));
        builder.put(ModelLayers.HAPPY_GHAST_ROPES, HappyGhastModel.createBodyLayer(false, new CubeDeformation(0.2f)));
        builder.put(ModelLayers.HAPPY_GHAST_BABY_ROPES, HappyGhastModel.createBodyLayer(true, new CubeDeformation(0.2f)).apply(HappyGhastModel.BABY_TRANSFORMER));



        builder.put(ModelLayers.FROG, FrogModel.createBodyLayer());
        builder.put(ModelLayers.TADPOLE, TadpoleModel.createBodyLayer());
        builder.put(ModelLayers.BREEZE, BreezeModel.createBodyLayer(32, 32));
        builder.put(ModelLayers.BREEZE_WIND, BreezeModel.createBodyLayer(128, 128));
        builder.put(ModelLayers.ROKFISK, RokfiskModel.createBodyLayer());
        builder.put(ModelLayers.DECORATED_POT_BASE, DecoratedPotRenderer.createBaseLayer());
        builder.put(ModelLayers.DECORATED_POT_SIDES,DecoratedPotRenderer.createSidesLayer());
        builder.put(ModelLayers.PIGLIN_HEAD, LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64));

        builder.put(ModelLayers.PIG, NewPigModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.PIG_BABY, NewPigModel.createBodyLayer(CubeDeformation.NONE).apply(NewPigModel.BABY_TRANSFORMER));

        builder.put(ModelLayers.PIG_SADDLE, NewPigModel.createBodyLayer(new CubeDeformation(0.5F)));
        builder.put(ModelLayers.PIG_BABY_SADDLE, NewPigModel.createBodyLayer(new CubeDeformation(0.5F)).apply(NewPigModel.BABY_TRANSFORMER));



        builder.put(ModelLayers.COLD_PIG, NewColdPigModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.COLD_PIG_BABY, NewPigModel.createBodyLayer(CubeDeformation.NONE).apply(NewPigModel.BABY_TRANSFORMER));

        LayerDefinition normalChicken = NewChickenModel.createBodyLayer();
        LayerDefinition coldChicken = NewColdChickenModel.createBodyLayer();

        builder.put(ModelLayers.CHICKEN, normalChicken);
        builder.put(ModelLayers.CHICKEN_BABY, normalChicken.apply(NewChickenModel.BABY_TRANSFORMER));
        builder.put(ModelLayers.COLD_CHICKEN, coldChicken);
        builder.put(ModelLayers.COLD_CHICKEN_BABY, coldChicken.apply(NewChickenModel.BABY_TRANSFORMER));

        builder.put(ModelLayers.PLAYER, LayerDefinition.create(NewPlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
        builder.put(ModelLayers.PLAYER_INNER_ARMOR, layerDefinition4);
        builder.put(ModelLayers.PLAYER_OUTER_ARMOR, layerDefinition2);
        builder.put(ModelLayers.PLAYER_SLIM, LayerDefinition.create(NewPlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
        builder.put(ModelLayers.PLAYER_SLIM_INNER_ARMOR, layerDefinition4);
        builder.put(ModelLayers.PLAYER_SLIM_OUTER_ARMOR, layerDefinition2);

        ImmutableMap<ModelLayerLocation, LayerDefinition> immutableMap = builder.build();
        List<ModelLayerLocation> list = ModelLayers.getKnownLocations().filter(modelLayerLocation -> !immutableMap.containsKey(modelLayerLocation)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing layer definitions: " + list);
        }
        return immutableMap;
    }
}