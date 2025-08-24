package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.stream.Stream;

public class PlaceCommand {

    private static final SimpleCommandExceptionType ERROR_FEATURE_FAILED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.place.feature.failed"));
    private static final SimpleCommandExceptionType ERROR_STRUCTURE_FAILED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.place.structure.failed"));
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType(
            (template) -> new TranslationTextComponent("commands.place.template.invalid", template));
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.place.template.failed"));

    private static final SuggestionProvider<CommandSource> SUGGEST_TEMPLATES = (context, builder) -> {
        TemplateManager templateManager = context.getSource().getServer().getStructureManager();
        return net.minecraft.command.ISuggestionProvider.suggest(
                templateManager.getKeys().stream().map(ResourceLocation::toString), builder);
    };

    private static final SuggestionProvider<CommandSource> SUGGEST_STRUCTURES = (context, builder) -> {
        DynamicRegistries registries = context.getSource().getLevel().getServer().registryAccess();
        return net.minecraft.command.ISuggestionProvider.suggest(
                registries.registryOrThrow(Registry.STRUCTURE_FEATURE_REGISTRY).keySet().stream()
                        .map(ResourceLocation::toString), builder);
    };

    private static final SuggestionProvider<CommandSource> SUGGEST_FEATURE = (context, builder) -> {
        DynamicRegistries registries = context.getSource().getLevel().getServer().registryAccess();

        // Get keys from CONFIGURED_FEATURE
        Stream<String> configuredFeatureKeys = registries.registryOrThrow(WorldGenRegistries.CONFIGURED_FEATURE.key())
                .keySet().stream()
                .map(ResourceLocation::getPath);

        // Suggest combined keys
        return net.minecraft.command.ISuggestionProvider.suggest(configuredFeatureKeys, builder);
    };


    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("place")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("template")
                        .then(Commands.argument("template", ResourceLocationArgument.id()).suggests(SUGGEST_TEMPLATES)
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> placeTemplate(context.getSource(),
                                                ResourceLocationArgument.getId(context, "template"),
                                                BlockPosArgument.getOrLoadBlockPos(context, "pos"),
                                                Rotation.NONE, Mirror.NONE, 1.0F, 0)))))
                .then(Commands.literal("feature")
                        .then(Commands.argument("feature", ResourceLocationArgument.id()).suggests(SUGGEST_FEATURE)
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())

                                        .executes(context -> placeStructure(context.getSource(),
                                                ResourceLocationArgument.getId(context, "feature"),
                                                BlockPosArgument.getOrLoadBlockPos(context, "pos")))))));
    }

    private static int placeTemplate(CommandSource source, ResourceLocation template, BlockPos pos,
                                     Rotation rotation, Mirror mirror, float integrity, int seed) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        TemplateManager templateManager = world.getStructureManager();
        Optional<Template> optional = Optional.ofNullable(templateManager.get(template));
        if (optional.isEmpty()) {
            throw ERROR_TEMPLATE_INVALID.create(template);
        }

        Template structureTemplate = optional.get();
        PlacementSettings settings = new PlacementSettings().setRotation(rotation).setMirror(mirror);
        boolean success = structureTemplate.placeInWorld(world, pos, pos, settings,
                StructureBlockTileEntity.createRandom(seed), 2);

        if (!success) {
            throw ERROR_TEMPLATE_FAILED.create();
        }

        source.sendSuccess(new TranslationTextComponent("commands.place.template.success", template, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    private static int placeStructure(CommandSource source, ResourceLocation structure, BlockPos pos) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        DynamicRegistries registries = world.getServer().registryAccess();
        ChunkGenerator generator = world.getChunkSource().getGenerator();
        SharedSeedRandom random = new SharedSeedRandom(world.getSeed());

        ConfiguredFeature<?, ?> feature = registries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).get(structure);

        if (feature == null) {
            throw ERROR_FEATURE_FAILED.create();
        }

        for (int tries = 0; tries < 10; tries++) {
            int offset = tries > 0? 2 + 2 * tries : 0;
            int yOffset = tries > 4 ? tries : 0;
            if (feature.place(world, generator, random, pos.offset(getOffset(random.nextBoolean(), offset), getOffset(true, yOffset), getOffset(random.nextBoolean(), offset)))) {
                source.sendSuccess(new TranslationTextComponent("commands.place.structure.success", structure, pos.getX() + getOffset(random.nextBoolean(), offset) , pos.getY() + getOffset(true, yOffset), pos.getZ() + getOffset(random.nextBoolean(), offset)), true);
                return 1;
            }
        }

        throw ERROR_FEATURE_FAILED.create();
    }

    public static int getOffset(boolean negative, int offset) {
        return negative ? -offset : offset;
    }
}
