package net.minecraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.Map;

public class DecoratedPotPatterns {
    private static final String BASE_NAME = "decorated_pot_base";
    public static final RegistryKey<String> BASE = DecoratedPotPatterns.create("decorated_pot_base");
    private static final String BRICK_NAME = "decorated_pot_side";
    private static final String ANGLER_NAME = "angler_pottery_pattern";
    private static final String ARCHER_NAME = "archer_pottery_pattern";
    private static final String ARMS_UP_NAME = "arms_up_pottery_pattern";
    private static final String BLADE_NAME = "blade_pottery_pattern";
    private static final String BREWER_NAME = "brewer_pottery_pattern";
    private static final String BURN_NAME = "burn_pottery_pattern";
    private static final String DANGER_NAME = "danger_pottery_pattern";
    private static final String EXPLORER_NAME = "explorer_pottery_pattern";
    private static final String FRIEND_NAME = "friend_pottery_pattern";
    private static final String HEART_NAME = "heart_pottery_pattern";
    private static final String HEARTBREAK_NAME = "heartbreak_pottery_pattern";
    private static final String HOWL_NAME = "howl_pottery_pattern";
    private static final String MINER_NAME = "miner_pottery_pattern";
    private static final String MOURNER_NAME = "mourner_pottery_pattern";
    private static final String PLENTY_NAME = "plenty_pottery_pattern";
    private static final String PRIZE_NAME = "prize_pottery_pattern";
    private static final String SHEAF_NAME = "sheaf_pottery_pattern";
    private static final String SHELTER_NAME = "shelter_pottery_pattern";
    private static final String SKULL_NAME = "skull_pottery_pattern";
    private static final String SNORT_NAME = "snort_pottery_pattern";
    private static final String POT_NAME = "pot_pottery_pattern";
    private static final String DUST_NAME = "dust_pottery_pattern";

    private static final RegistryKey<String> BRICK = DecoratedPotPatterns.create("decorated_pot_side");
    private static final RegistryKey<String> ANGLER = DecoratedPotPatterns.create("angler_pottery_pattern");
    private static final RegistryKey<String> ARCHER = DecoratedPotPatterns.create("archer_pottery_pattern");
    private static final RegistryKey<String> ARMS_UP = DecoratedPotPatterns.create("arms_up_pottery_pattern");
    private static final RegistryKey<String> BLADE = DecoratedPotPatterns.create("blade_pottery_pattern");
    private static final RegistryKey<String> BREWER = DecoratedPotPatterns.create("brewer_pottery_pattern");
    private static final RegistryKey<String> BURN = DecoratedPotPatterns.create("burn_pottery_pattern");
    private static final RegistryKey<String> DANGER = DecoratedPotPatterns.create("danger_pottery_pattern");
    private static final RegistryKey<String> EXPLORER = DecoratedPotPatterns.create("explorer_pottery_pattern");
    private static final RegistryKey<String> FRIEND = DecoratedPotPatterns.create("friend_pottery_pattern");
    private static final RegistryKey<String> HEART = DecoratedPotPatterns.create("heart_pottery_pattern");
    private static final RegistryKey<String> HEARTBREAK = DecoratedPotPatterns.create("heartbreak_pottery_pattern");
    private static final RegistryKey<String> HOWL = DecoratedPotPatterns.create("howl_pottery_pattern");
    private static final RegistryKey<String> MINER = DecoratedPotPatterns.create("miner_pottery_pattern");
    private static final RegistryKey<String> MOURNER = DecoratedPotPatterns.create("mourner_pottery_pattern");
    private static final RegistryKey<String> PLENTY = DecoratedPotPatterns.create("plenty_pottery_pattern");
    private static final RegistryKey<String> PRIZE = DecoratedPotPatterns.create("prize_pottery_pattern");
    private static final RegistryKey<String> SHEAF = DecoratedPotPatterns.create("sheaf_pottery_pattern");
    private static final RegistryKey<String> SHELTER = DecoratedPotPatterns.create("shelter_pottery_pattern");
    private static final RegistryKey<String> SKULL = DecoratedPotPatterns.create("skull_pottery_pattern");
    private static final RegistryKey<String> SNORT = DecoratedPotPatterns.create("snort_pottery_pattern");
    private static final RegistryKey<String> POT = DecoratedPotPatterns.create("pot_pottery_pattern");
    private static final RegistryKey<String> DUST = DecoratedPotPatterns.create("dust_pottery_pattern");


    private static final Map<Item, RegistryKey<String>> ITEM_TO_POT_TEXTURE = Map.ofEntries(
            Map.entry(Items.BRICK, BRICK), Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER),
            Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER),
            Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP),
            Map.entry(Items.BLADE_POTTERY_SHERD, BLADE),
            Map.entry(Items.BREWER_POTTERY_SHERD, BREWER),
            Map.entry(Items.BURN_POTTERY_SHERD, BURN),
            Map.entry(Items.DANGER_POTTERY_SHERD, DANGER),
            Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER),
            Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND),
            Map.entry(Items.HEART_POTTERY_SHERD, HEART),
            Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK),
            Map.entry(Items.HOWL_POTTERY_SHERD, HOWL),
            Map.entry(Items.MINER_POTTERY_SHERD, MINER),
            Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER),
            Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY),
            Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE),
            Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF),
            Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER),
            Map.entry(Items.SKULL_POTTERY_SHERD, SKULL),
            Map.entry(Items.SNORT_POTTERY_SHERD, SNORT),
            Map.entry(Items.POT_POTTERY_SHERD, POT),
            Map.entry(Items.DUST_POTTERY_SHERD, DUST));

    private static RegistryKey<String> create(String string) {
        return RegistryKey.create(Registry.DECORATED_POT_PATTERNS_REGISTRY, new ResourceLocation(string));
    }

    public static ResourceLocation location(RegistryKey<String> resourceKey) {
        String pathWithPng = resourceKey.location().getPath() + ".png";
        ResourceLocation newLocation = new ResourceLocation(resourceKey.location().getNamespace(), pathWithPng);
        return newLocation.withPrefix("textures/entity/decorated_pot/");
    }


    @Nullable
    public static RegistryKey<String> getResourceKey(Item item) {
        return ITEM_TO_POT_TEXTURE.get(item);
    }

    public static String bootstrap() {
        Registry<String> registry = Registry.DECORATED_POT_PATTERNS;
        Registry.register(registry, BRICK, BRICK_NAME);
        Registry.register(registry, ANGLER, ANGLER_NAME);
        Registry.register(registry, ARCHER, ARCHER_NAME);
        Registry.register(registry, ARMS_UP, ARMS_UP_NAME);
        Registry.register(registry, BLADE, BLADE_NAME);
        Registry.register(registry, BREWER, BREWER_NAME);
        Registry.register(registry, BURN, BURN_NAME);
        Registry.register(registry, DANGER, DANGER_NAME);
        Registry.register(registry, EXPLORER, EXPLORER_NAME);
        Registry.register(registry, FRIEND, FRIEND_NAME);
        Registry.register(registry, HEART, HEART_NAME);
        Registry.register(registry, HEARTBREAK, HEARTBREAK_NAME);
        Registry.register(registry, HOWL, HOWL_NAME);
        Registry.register(registry, MINER, MINER_NAME);
        Registry.register(registry, MOURNER, MOURNER_NAME);
        Registry.register(registry, PLENTY, PLENTY_NAME);
        Registry.register(registry, PRIZE, PRIZE_NAME);
        Registry.register(registry, SHEAF, SHEAF_NAME);
        Registry.register(registry, SHELTER, SHELTER_NAME);
        Registry.register(registry, SKULL, SKULL_NAME);
        Registry.register(registry, SNORT, SNORT_NAME);
        Registry.register(registry, POT, POT_NAME);
        Registry.register(registry, DUST, DUST_NAME);
        return Registry.register(registry, BASE, BASE_NAME);
    }
}