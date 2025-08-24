package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;
import java.util.List;

public class V2571 extends NamespacedSchema {
    public V2571(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    private static final List<String> MOB_NAMES = List.of(
            "minecraft:herobrine",
            "minecraft:crossbone_skeleton",
            "minecraft:shaman",
            "minecraft:custom_arrow",
            "minecraft:bone_arrow",
            "minecraft:queen_bee",
            "minecraft:woodpecker",
            "minecraft:pillager_captain",
            "minecraft:endstone_block",
            "minecraft:colette",
            "minecraft:bogged",
            "minecraft:great_hunger",
            "minecraft:creaking",
            "minecraft:happy_ghast",
            "minecraft:warden",
            "minecraft:crimson_mosquito",
            "minecraft:roadrunner",
            "minecraft:enderiophage",
            "minecraft:gumball",
            "minecraft:gumbeeper",
            "minecraft:desolate_dagger",

            "minecraft:mosquito_spit",
            "minecraft:goat",
            "minecraft:camel",
            "minecraft:tadpole",
            "minecraft:axolotl",
            "minecraft:frog",
            "minecraft:copper_golem",
            "minecraft:trickster",
            "minecraft:breeze",
            "minecraft:warm_egg",
            "minecraft:cold_egg",
            "minecraft:pokeball",
            "minecraft:wildfire",
            "minecraft:infernal_sovereign",
            "minecraft:infernal_fireball",
            "minecraft:rattata",
            "minecraft:star",
            "minecraft:cat_projectile",
            "minecraft:creaking_transient",
            "minecraft:allay",
            "minecraft:pale_garden_bat",
            "minecraft:blackhole",
            "minecraft:giant_worm",
            "minecraft:raccoon",
            "minecraft:gilded_ravager",
            "minecraft:frisbee",
            "minecraft:marauder",
            "minecraft:demon_eye",
            "minecraft:eye_of_cthulhu",
            "minecraft:retinazer",
            "minecraft:eye_of_cthulhu_second_form",

            "minecraft:rokfisk"
    );

    private static final List<String> BLOCK_ENTITY_NAMES = List.of(
            "minecraft:creaking_heart",
            "minecraft:sculk_sensor",
            "minecraft:brushable_block",
            "minecraft:decorated_pot"
    );

    protected static void registerEntities(Schema schema, Map<String, Supplier<TypeTemplate>> map, List<String> names) {
        for (String name : names) {
            schema.register(map, name, () -> V0100.equipment(schema));
        }
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> entityMap = super.registerEntities(schema);
        registerEntities(schema, entityMap, MOB_NAMES);
        return entityMap;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> blockEntityMap = super.registerBlockEntities(schema);
        registerEntities(schema, blockEntityMap, BLOCK_ENTITY_NAMES);
        return blockEntityMap;
    }
}
