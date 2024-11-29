package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryUtils;
import net.minecraft.util.text.StringTextComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RegistryCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("registry")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("registry", StringArgumentType.word())
                                .suggests(RegistryCommand::suggestRegistries)
                                .then(Commands.literal("entry")
                                        .then(Commands.argument("entry", StringArgumentType.greedyString())
                                                .suggests(RegistryCommand::suggestRegistryEntries)
                                                .executes(context -> {
                                                    String registryName = StringArgumentType.getString(context, "registry");
                                                    String entryName = StringArgumentType.getString(context, "entry");
                                                    return getRegistryEntry(context, registryName, entryName);
                                                })
                                        )
                                )
                                .then(Commands.literal("methods")
                                        .then(Commands.argument("methodName", StringArgumentType.greedyString())
                                                .suggests(RegistryCommand::suggestMethods)
                                                .executes(context -> {
                                                    String registryName = StringArgumentType.getString(context, "registry");
                                                    String methodName = StringArgumentType.getString(context, "methodName");
                                                    return viewRegistryMethod(context, registryName, methodName);
                                                })
                                        )
                                )
                                .then(Commands.literal("fields")
                                        .then(Commands.argument("fieldName", StringArgumentType.greedyString())
                                                .suggests(RegistryCommand::suggestFields)
                                                .executes(context -> {
                                                    String registryName = StringArgumentType.getString(context, "registry");
                                                    String fieldName = StringArgumentType.getString(context, "fieldName");
                                                    return viewRegistryField(context, registryName, fieldName);
                                                })
                                        )
                                )
                                .then(Commands.literal("constructors")
                                        .then(Commands.argument("constructorSignature", StringArgumentType.greedyString())
                                                .suggests(RegistryCommand::suggestConstructors)
                                                .executes(context -> {
                                                    String registryName = StringArgumentType.getString(context, "registry");
                                                    String constructorSignature = StringArgumentType.getString(context, "constructorSignature");
                                                    return viewRegistryConstructor(context, registryName, constructorSignature);
                                                })
                                        )
                                )
                                .then(Commands.literal("enums")
                                        .then(Commands.argument("enumName", StringArgumentType.greedyString())
                                                .suggests(RegistryCommand::suggestEnums)
                                                .executes(context -> {
                                                    String registryName = StringArgumentType.getString(context, "registry");
                                                    String enumName = StringArgumentType.getString(context, "enumName");
                                                    return viewRegistryEnum(context, registryName, enumName);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int lookupRegistry(CommandContext<CommandSource> context, String registryName, String entryName) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            ResourceLocation entryLocation = new ResourceLocation(entryName);
            if (registry.containsKey(entryLocation)) {
                context.getSource().sendSuccess(new StringTextComponent("Found entry: " + entryName + " in registry: " + registryName), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Entry not found: " + entryName + " in registry: " + registryName));
            }
        } else {
            context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        }
        return 1;
    }

    private static int getRegistryEntry(CommandContext<CommandSource> context, String registryName, String entryName) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            ResourceLocation entryLocation = new ResourceLocation(entryName);
            if (registry.containsKey(entryLocation)) {
                Object entry = registry.get(entryLocation);
                String entryContents = entry.toString();
                context.getSource().sendSuccess(new StringTextComponent("Entry contents: " + entryContents), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Entry not found: " + entryName + " in registry: " + registryName));
            }
        } else {
            context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        }
        return 1;
    }

    // Suggestion Methods
    private static CompletableFuture<Suggestions> suggestRegistries(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        List<String> registryNames = List.of(
                "sound_event", "fluid", "mob_effect", "block", "enchantment", "entity_type",
                "item", "crossbow_config", "potion", "particle_type", "block_entity_type",
                "motive", "custom_stat", "chunk_status", "rule_test", "pos_rule_test",
                "menu", "recipe_type", "recipe_serializer", "attribute", "stat_type",
                "villager_type", "villager_profession", "point_of_interest_type",
                "memory_module_type", "sensor_type", "schedule", "activity",
                "loot_pool_entry_type", "loot_function_type", "loot_condition_type", "surface_builder",
                "carver", "feature", "structure_feature", "structure_piece", "decorator",
                "block_state_provider_type", "block_placer_type", "foliage_placer_type",
                "trunk_placer_type", "tree_decorator_type", "feature_size_type",
                "configured_feature",
                "biome_source", "chunk_generator", "structure_processor", "structure_pool_element", "frisbee"
        );
        return ISuggestionProvider.suggest(registryNames.stream(), builder);
    }

    private static CompletableFuture<Suggestions> suggestRegistryEntries(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String registryName = StringArgumentType.getString(context, "registry");
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            List<String> entryNames = registry.keySet().stream()
                    .map(ResourceLocation::toString)
                    .collect(Collectors.toList());
            return ISuggestionProvider.suggest(entryNames.stream(), builder);
        }
        return Suggestions.empty();
    }

    private static CompletableFuture<Suggestions> suggestMethods(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String registryName = StringArgumentType.getString(context, "registry");
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                List<String> methodNames = Arrays.stream(clazz.getMethods())
                        .map(Method::getName)
                        .distinct()
                        .collect(Collectors.toList());
                return ISuggestionProvider.suggest(methodNames.stream(), builder);
            }
        }
        return Suggestions.empty();
    }

    private static CompletableFuture<Suggestions> suggestFields(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String registryName = StringArgumentType.getString(context, "registry");
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                List<String> fieldNames = Arrays.stream(clazz.getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.toList());
                return ISuggestionProvider.suggest(fieldNames.stream(), builder);
            }
        }
        return Suggestions.empty();
    }

    private static CompletableFuture<Suggestions> suggestConstructors(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String registryName = StringArgumentType.getString(context, "registry");
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                List<String> constructorSignatures = Arrays.stream(clazz.getDeclaredConstructors())
                        .map(constructor -> {
                            String params = Arrays.stream(constructor.getParameterTypes())
                                    .map(Class::getSimpleName)  // Get only the simple name of each parameter type
                                    .collect(Collectors.joining(", "));
                            return constructor.getName() + "(" + params + ")";
                        })
                        .collect(Collectors.toList());
                return ISuggestionProvider.suggest(constructorSignatures.stream(), builder);
            }
        }
        return Suggestions.empty();
    }

    private static CompletableFuture<Suggestions> suggestEnums(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String registryName = StringArgumentType.getString(context, "registry");
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);

        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                List<String> enumNames = Arrays.stream(clazz.getDeclaredClasses())
                        .filter(Class::isEnum)
                        .map(Class::getSimpleName)
                        .collect(Collectors.toList());
                return ISuggestionProvider.suggest(enumNames.stream(), builder);
            }
        }
        return Suggestions.empty();
    }

    // Execution Methods
    private static int viewRegistryMethod(CommandContext<CommandSource> context, String registryName, String methodName) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);
        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        String methodDetails = Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getSimpleName() + " " +
                                method.getName() + "(" + Arrays.stream(method.getParameterTypes())
                                .map(Class::getSimpleName)
                                .collect(Collectors.joining(", ")) + ")";
                        context.getSource().sendSuccess(new StringTextComponent("Method details: " + methodDetails), true);
                        return 1;
                    }
                }
                context.getSource().sendFailure(new StringTextComponent("Method not found: " + methodName));
            }
        }
        context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        return 0;
    }

    private static int viewRegistryField(CommandContext<CommandSource> context, String registryName, String fieldName) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);
        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    String fieldDetails = Modifier.toString(field.getModifiers()) + " " + field.getType().getSimpleName() + " " + field.getName();
                    context.getSource().sendSuccess(new StringTextComponent("Field details: " + fieldDetails), true);
                    return 1;
                } catch (NoSuchFieldException e) {
                    context.getSource().sendFailure(new StringTextComponent("Field not found: " + fieldName));
                }
            }
        }
        context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        return 0;
    }

    private static int viewRegistryConstructor(CommandContext<CommandSource> context, String registryName, String constructorSignature) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);
        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    String signature = constructor.getName() + Arrays.toString(constructor.getParameterTypes());
                    if (signature.equals(constructorSignature)) {
                        String constructorDetails = Modifier.toString(constructor.getModifiers()) + " " + constructor.getName() + "(" +
                                Arrays.stream(constructor.getParameterTypes())
                                        .map(Class::getSimpleName)
                                        .collect(Collectors.joining(", ")) + ")";
                        context.getSource().sendSuccess(new StringTextComponent("Constructor details: " + constructorDetails), true);
                        return 1;
                    }
                }
                context.getSource().sendFailure(new StringTextComponent("Constructor not found: " + constructorSignature));
            }
        }
        context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        return 0;
    }

    private static int viewRegistryEnum(CommandContext<CommandSource> context, String registryName, String enumName) {
        Registry<?> registry = RegistryUtils.getRegistryFromName(registryName);
        if (registry != null) {
            Class<?> clazz = getClassFromRegistry(registry);
            if (clazz != null) {
                Class<?>[] nestedClasses = clazz.getDeclaredClasses();
                for (Class<?> nestedClass : nestedClasses) {
                    if (nestedClass.isEnum() && nestedClass.getSimpleName().equals(enumName)) {
                        String enumValues = Arrays.stream(nestedClass.getEnumConstants())
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));
                        context.getSource().sendSuccess(new StringTextComponent("Enum values for " + enumName + ": " + enumValues), true);
                        return 1;
                    }
                }
                context.getSource().sendFailure(new StringTextComponent("Enum not found: " + enumName));
            }
        }
        context.getSource().sendFailure(new StringTextComponent("Registry not found: " + registryName));
        return 0;
    }

    // Utility method to get the class from the registry
    private static Class<?> getClassFromRegistry(Registry<?> registry) {
        return RegistryUtils.getClassFromRegistry(registry);
    }
}
