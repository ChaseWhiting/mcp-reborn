package net.minecraft.client.model.geom;

import com.google.common.collect.Sets;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

import java.util.Set;
import java.util.stream.Stream;

public class ModelLayers {
    private static final String DEFAULT_LAYER = "main";
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation ALLAY = ModelLayers.register("allay");

    public static final ModelLayerLocation WARDEN = ModelLayers.register("warden");
    public static final ModelLayerLocation CREAKING = ModelLayers.register("creaking");
    public static final ModelLayerLocation CREAKING_2 = ModelLayers.register("creaking_2");

    public static final ModelLayerLocation CAMEL = ModelLayers.register("camel");
    public static final ModelLayerLocation CAMEL_CARPET = ModelLayers.register("camel_carpet");
    public static final ModelLayerLocation CAMEL_BABY_CARPET = ModelLayers.register("camel_baby_carpet");

    public static final ModelLayerLocation CAMEL_LEASH = ModelLayers.register("camel_leash");
    public static final ModelLayerLocation CAMEL_BABY_LEASH = ModelLayers.register("camel_baby_leash");

    public static final ModelLayerLocation CHICKEN = ModelLayers.register("chicken");
    public static final ModelLayerLocation CHICKEN_BABY = ModelLayers.register("chicken_baby");
    public static final ModelLayerLocation COLD_CHICKEN = ModelLayers.register("cold_chicken");
    public static final ModelLayerLocation COLD_CHICKEN_BABY = ModelLayers.register("cold_chicken_baby");


    public static final ModelLayerLocation PLAYER = ModelLayers.register("player");
    public static final ModelLayerLocation PLAYER_INNER_ARMOR = ModelLayers.registerInnerArmor("player");
    public static final ModelLayerLocation PLAYER_OUTER_ARMOR = ModelLayers.registerOuterArmor("player");
    public static final ModelLayerLocation PLAYER_SLIM = ModelLayers.register("player_slim");
    public static final ModelLayerLocation PLAYER_SLIM_INNER_ARMOR = ModelLayers.registerInnerArmor("player_slim");
    public static final ModelLayerLocation PLAYER_SLIM_OUTER_ARMOR = ModelLayers.registerOuterArmor("player_slim");

    public static final ModelLayerLocation GHAST = ModelLayers.register("ghast");


    public static final ModelLayerLocation HAPPY_GHAST = ModelLayers.register("happy_ghast");
    public static final ModelLayerLocation HAPPY_GHAST_BABY = ModelLayers.register("happy_ghast_baby");
    public static final ModelLayerLocation HAPPY_GHAST_HARNESS = ModelLayers.register("happy_ghast_harness");
    public static final ModelLayerLocation HAPPY_GHAST_BABY_HARNESS = ModelLayers.register("happy_ghast_baby_harness");
    public static final ModelLayerLocation HAPPY_GHAST_ROPES = ModelLayers.register("happy_ghast_ropes");
    public static final ModelLayerLocation HAPPY_GHAST_BABY_ROPES = ModelLayers.register("happy_ghast_baby_ropes");


    public static final ModelLayerLocation PIG_SADDLE = ModelLayers.register("pig_saddle");
    public static final ModelLayerLocation PIG_BABY_SADDLE = ModelLayers.register("pig_baby_saddle");


    public static final ModelLayerLocation PIG = ModelLayers.register("pig");
    public static final ModelLayerLocation PIG_BABY = ModelLayers.register("pig_baby");
    public static final ModelLayerLocation COLD_PIG = ModelLayers.register("cold_pig");
    public static final ModelLayerLocation COLD_PIG_BABY = ModelLayers.register("cold_pig_baby");


    public static final ModelLayerLocation FROG = ModelLayers.register("frog");
    public static final ModelLayerLocation BREEZE_WIND = ModelLayers.register("breeze_wind");
    public static final ModelLayerLocation BREEZE = ModelLayers.register("breeze");
    public static final ModelLayerLocation ROKFISK = ModelLayers.register("rokfisk");
    public static final ModelLayerLocation PIGLIN_HEAD = ModelLayers.register("piglin_head");
    public static final ModelLayerLocation DECORATED_POT_BASE = ModelLayers.register("decorated_pot_base");
    public static final ModelLayerLocation DECORATED_POT_SIDES = ModelLayers.register("decorated_pot_sides");


    public static final ModelLayerLocation TADPOLE = ModelLayers.register("tadpole");



    private static ModelLayerLocation register(String string) {
        return ModelLayers.register(string, DEFAULT_LAYER);
    }

    private static ModelLayerLocation register(String string, String string2) {
        ModelLayerLocation modelLayerLocation = ModelLayers.createLocation(string, string2);
        if (!ALL_MODELS.add(modelLayerLocation)) {
            throw new IllegalStateException("Duplicate registration for " + modelLayerLocation);
        }
        return modelLayerLocation;
    }

    private static ModelLayerLocation createLocation(String string, String string2) {
        return new ModelLayerLocation(new ResourceLocation("minecraft", string), string2);
    }

    private static ModelLayerLocation registerInnerArmor(String string) {
        return ModelLayers.register(string, "inner_armor");
    }

    private static ModelLayerLocation registerOuterArmor(String string) {
        return ModelLayers.register(string, "outer_armor");
    }

    public static Stream<ModelLayerLocation> getKnownLocations() {
        return ALL_MODELS.stream();
    }
}