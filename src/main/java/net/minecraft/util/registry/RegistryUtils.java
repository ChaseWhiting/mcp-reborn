package net.minecraft.util.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RegistryUtils {

    public static <T> Class<?> getClassFromRegistry(Registry<T> registry) {
        ResourceLocation location = getAnyResourceLocationFromRegistry(registry);
        T object = registry.get(location);

        if (object != null) {
            return object.getClass();
        } else {
            throw new IllegalArgumentException("No object found in the registry for the given ResourceLocation: " + location);
        }
    }

    public static ResourceLocation getAnyResourceLocationFromRegistry(Registry<?> registry) {
        ResourceLocation defaultKey = registry instanceof DefaultedRegistry<?> ? ((DefaultedRegistry<?>)registry).getDefaultKey() : null;
        return defaultKey != null ? defaultKey : registry.keySet().stream().findFirst().orElseThrow(() ->
            new IllegalArgumentException("Registry is empty or no valid ResourceLocation found"));
    }

    public static Registry<?> getRegistryFromName(String registryName) {
        return switch (registryName) {
            case "sound_event" -> Registry.SOUND_EVENT;
            case "fluid" -> Registry.FLUID;
            case "mob_effect" -> Registry.MOB_EFFECT;
            case "block" -> Registry.BLOCK;
            case "enchantment" -> Registry.ENCHANTMENT;
            case "entity_type" -> Registry.ENTITY_TYPE;
            case "item" -> Registry.ITEM;
            case "crossbow_config" -> Registry.CROSSBOW_CONFIG;
            case "potion" -> Registry.POTION;
            case "particle_type" -> Registry.PARTICLE_TYPE;
            case "block_entity_type" -> Registry.BLOCK_ENTITY_TYPE;
            case "motive" -> Registry.MOTIVE;
            case "custom_stat" -> Registry.CUSTOM_STAT;
            case "chunk_status" -> Registry.CHUNK_STATUS;
            case "rule_test" -> Registry.RULE_TEST;
            case "pos_rule_test" -> Registry.POS_RULE_TEST;
            case "menu" -> Registry.MENU;
            case "recipe_type" -> Registry.RECIPE_TYPE;
            case "recipe_serializer" -> Registry.RECIPE_SERIALIZER;
            case "attribute" -> Registry.ATTRIBUTE;
            case "stat_type" -> Registry.STAT_TYPE;
            case "villager_type" -> Registry.VILLAGER_TYPE;
            case "configured_feature" -> WorldGenRegistries.CONFIGURED_FEATURE;
            case "villager_profession" -> Registry.VILLAGER_PROFESSION;
            case "point_of_interest_type" -> Registry.POINT_OF_INTEREST_TYPE;
            case "memory_module_type" -> Registry.MEMORY_MODULE_TYPE;
            case "sensor_type" -> Registry.SENSOR_TYPE;
            case "schedule" -> Registry.SCHEDULE;
            case "activity" -> Registry.ACTIVITY;
            case "loot_pool_entry_type" -> Registry.LOOT_POOL_ENTRY_TYPE;
            case "loot_function_type" -> Registry.LOOT_FUNCTION_TYPE;
            case "loot_condition_type" -> Registry.LOOT_CONDITION_TYPE;
            case "surface_builder" -> Registry.SURFACE_BUILDER;
            case "carver" -> Registry.CARVER;
            case "feature" -> Registry.FEATURE;
            case "structure_feature" -> Registry.STRUCTURE_FEATURE;
            case "structure_piece" -> Registry.STRUCTURE_PIECE;
            case "decorator" -> Registry.DECORATOR;
            case "block_state_provider_type" -> Registry.BLOCKSTATE_PROVIDER_TYPES;
            case "block_placer_type" -> Registry.BLOCK_PLACER_TYPES;
            case "foliage_placer_type" -> Registry.FOLIAGE_PLACER_TYPES;
            case "trunk_placer_type" -> Registry.TRUNK_PLACER_TYPES;
            case "tree_decorator_type" -> Registry.TREE_DECORATOR_TYPES;
            case "feature_size_type" -> Registry.FEATURE_SIZE_TYPES;
            case "biome_source" -> Registry.BIOME_SOURCE;
            case "chunk_generator" -> Registry.CHUNK_GENERATOR;
            case "structure_processor" -> Registry.STRUCTURE_PROCESSOR;
            case "structure_pool_element" -> Registry.STRUCTURE_POOL_ELEMENT;
            case "frisbee" -> Registry.FRISBEE_DATA;
            default -> Registry.REGISTRY;
        };
    }
}
