package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import jdk.jfr.StackTrace;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Optional;

public class PlaceCommand {
    private static final SimpleCommandExceptionType ERROR_FEATURE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.place.feature.failed"));
    private static final SimpleCommandExceptionType ERROR_JIGSAW_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.place.jigsaw.failed"));
    private static final SimpleCommandExceptionType ERROR_STRUCTURE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.place.structure.failed"));
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType((p_214582_) -> {
        return new TranslationTextComponent("commands.place.template.invalid", p_214582_);
    });
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.place.template.failed"));
    private static final SuggestionProvider<CommandSource> SUGGEST_TEMPLATES = (context, builder) -> {
        // Get the template manager from the server world
        TemplateManager templateManager = context.getSource().getServer().getLevel(ServerWorld.OVERWORLD).getStructureManager();

        // Suggest template names using the new getKeys() method
        return net.minecraft.command.ISuggestionProvider.suggest(templateManager.getKeys().stream().map(ResourceLocation::toString), builder);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("place")
                .requires((p_214560_) -> p_214560_.hasPermission(2))
                .then(Commands.literal("feature")
                        .then(Commands.argument("feature", ResourceLocationArgument.id())
                                .executes((p_274824_) -> placeFeature(p_274824_.getSource(), ResourceLocationArgument.getId(p_274824_, "feature"), BlockPosArgument.getLoadedBlockPos(p_274824_, "pos")))
                        )
                )
                .then(Commands.literal("structure")
                        .then(Commands.argument("structure", ResourceLocationArgument.id())
                                .executes((p_274826_) -> placeStructure(p_274826_, p_274826_.getSource(), ResourceLocationArgument.getId(p_274826_, "structure"), BlockPosArgument.getLoadedBlockPos(p_274826_, "pos")))
                        )
                )
                .then(Commands.literal("template")
                        .then(Commands.argument("template", ResourceLocationArgument.id())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos()))
                                .suggests(SUGGEST_TEMPLATES)
                                .executes((p_274827_) -> placeTemplate(p_274827_.getSource(), ResourceLocationArgument.getId(p_274827_, "template"), BlockPosArgument.getLoadedBlockPos(p_274827_, "pos"), Rotation.NONE, Mirror.NONE, 1.0F, 0))
                        )
                )
        );
    }

    private static int placeFeature(CommandSource source, ResourceLocation feature, BlockPos pos) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        Template configuredFeature = world.getStructureManager().get(feature);
        configuredFeature.placeInWorld(world, pos, new PlacementSettings(), world.random);
        source.sendSuccess(new TranslationTextComponent("commands.place.feature.success", feature, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    private static int placeStructure(CommandContext context, CommandSource source, ResourceLocation structure, BlockPos pos) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        Structure<?> structureTemplate = ResourceLocationArgument.getStructure(context, structure);

        DynamicRegistries dynamicRegistries = world.getServer().registryAccess();
        ChunkGenerator chunkGenerator = world.getChunkSource().getGenerator();
        BiomeProvider biomeProvider = chunkGenerator.getBiomeSource();
        TemplateManager templateManager = world.getStructureManager();
        long seed = world.getSeed();
        ChunkPos chunkPos = new ChunkPos(pos);
        Biome biome = world.getBiome(pos);
        SharedSeedRandom sharedSeedRandom = new SharedSeedRandom(seed);
        sharedSeedRandom.setLargeFeatureSeed(seed, chunkPos.x, chunkPos.z);
        StructureSeparationSettings structureSeparationSettings = chunkGenerator.getSettings().getConfig(structureTemplate);

        StructureStart<?> structureStart = structureTemplate.generate(
                dynamicRegistries,
                chunkGenerator,
                biomeProvider,
                templateManager,
                seed,
                chunkPos,
                biome,
                0, // Reference
                sharedSeedRandom,
                structureSeparationSettings,
                null // Additional context or settings specific to the structure (could be null)
        );

        if (!structureStart.isValid()) {
            throw ERROR_STRUCTURE_FAILED.create();
        }

        structureStart.placeInChunk(world, world.structureFeatureManager(), chunkGenerator, world.random, structureStart.getBoundingBox(), chunkPos);

        source.sendSuccess(new TranslationTextComponent("commands.place.structure.success", structure, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    private static int placeTemplate(CommandSource source, ResourceLocation template, BlockPos pos, Rotation rotation, Mirror mirror, float integrity, int seed) throws CommandSyntaxException {
        try {
            ServerWorld world = source.getLevel();
            TemplateManager templateManager = world.getStructureManager();
            Optional<Template> optional = Optional.ofNullable(templateManager.getTemplate(template));

            if (optional.isEmpty()) {
                throw ERROR_TEMPLATE_INVALID.create(template);
            }

            Template structureTemplate = optional.get();
            PlacementSettings placementSettings = new PlacementSettings().setRotation(rotation).setMirror(mirror);
            boolean success = structureTemplate.placeInWorld(world, pos, pos, placementSettings, StructureBlockTileEntity.createRandom(seed), 2);

            if (!success) {
                throw ERROR_TEMPLATE_FAILED.create();
            }

            source.sendSuccess(new TranslationTextComponent("commands.place.template.success", template, pos.getX(), pos.getY(), pos.getZ()), true);
            return 1;

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception after logging it
        } catch (Exception e) {
            // Catch any other exceptions that might occur
            e.printStackTrace();
            source.sendFailure(new TranslationTextComponent("commands.place.template.failed"));
        }
        return 0;
    }
}
