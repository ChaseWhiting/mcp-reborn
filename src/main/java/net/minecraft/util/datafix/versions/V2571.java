package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V2571 extends NamespacedSchema {
    public V2571(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));
    }

    protected static void registerBlockEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));  // You can define a different template for block entities if needed
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> entityMap = super.registerEntities(schema);
        registerMob(schema, entityMap, "minecraft:herobrine");
        registerMob(schema, entityMap, "minecraft:crossbone_skeleton");
        registerMob(schema, entityMap, "minecraft:shaman");
        registerMob(schema, entityMap, "minecraft:custom_arrow");
        registerMob(schema, entityMap, "minecraft:bone_arrow");
        registerMob(schema, entityMap, "minecraft:queen_bee");
        registerMob(schema, entityMap, "minecraft:pillager_captain");
        registerMob(schema, entityMap, "minecraft:endstone_block");
        registerMob(schema, entityMap, "minecraft:colette");
        registerMob(schema, entityMap, "minecraft:bogged");
        registerMob(schema, entityMap, "minecraft:great_hunger");
        registerMob(schema, entityMap, "minecraft:creaking");
        registerMob(schema, entityMap, "minecraft:star");
        registerMob(schema, entityMap, "minecraft:cat_projectile");
        registerMob(schema, entityMap, "minecraft:creaking_transient");

        registerMob(schema, entityMap, "minecraft:allay");
        registerMob(schema, entityMap, "minecraft:pale_garden_bat");

        registerMob(schema, entityMap, "minecraft:blackhole");
        registerMob(schema, entityMap, "minecraft:giant_worm");
        registerMob(schema, entityMap, "minecraft:raccoon");

        registerMob(schema, entityMap, "minecraft:gilded_ravager");
        registerMob(schema, entityMap, "minecraft:frisbee");
        registerMob(schema, entityMap, "minecraft:marauder");

        registerMob(schema, entityMap, "minecraft:demon_eye");
        registerMob(schema, entityMap, "minecraft:eye_of_cthulhu");
        registerMob(schema, entityMap, "minecraft:retinazer");

        registerMob(schema, entityMap, "minecraft:eye_of_cthulhu_second_form");


        return entityMap;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> entityMap = super.registerBlockEntities(schema);
        registerBlockEntity(schema, entityMap, "minecraft:creaking_heart");

        return entityMap;
    }
}
