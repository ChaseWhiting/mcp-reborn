package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.position.PositionSourceType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootEntryManager;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.DecoratedPotPatterns;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.component.DataComponentType;
import net.minecraft.util.component.DataComponents;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.DensityFunction;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.DensityFunctions;
import net.minecraft.world.biome.newBiome.noise.NormalNoise;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSizeType;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements Codec<T>, Keyable, IObjectIntIterable<T> {


   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
   protected static final MutableRegistry<MutableRegistry<?>> WRITABLE_REGISTRY = new SimpleRegistry<>(createRegistryKey("root"), Lifecycle.experimental());
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;

   public static final RegistryKey<Registry<GameEvent>> GAME_EVENT_REGISTRY = createRegistryKey("game_event");
   public static final RegistryKey<Registry<TrimPattern>> TRIM_PATTERN_REGISTRY = createRegistryKey("trim_pattern");
   public static final RegistryKey<Registry<ToolTrimPattern>> TOOL_TRIM_PATTERN_REGISTRY = createRegistryKey("tool_trim_pattern");

   public static final RegistryKey<Registry<TrimMaterial>> TRIM_MATERIAL_REGISTRY = createRegistryKey("trim_material");
   public static final RegistryKey<Registry<DataComponentType<?>>> DATA_COMPONENT_TYPE_REGISTRY = createRegistryKey("data_component");
   public static final RegistryKey<Registry<String>> DECORATED_POT_PATTERNS_REGISTRY = createRegistryKey("decorated_pot_patterns");
   public static final RegistryKey<Registry<RuleBlockEntityModifierType<?>>> RULE_BLOCK_ENTITY_MODIFIER_REGISTRY = createRegistryKey("rule_block_entity_modifier");
   public static final RegistryKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_REGISTRY = createRegistryKey("int_provider_type");
   //public static final RegistryKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT_REGISTRY = createRegistryKey("structure_placement_type");

   public static final RegistryKey<Registry<Codec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE_REGISTRY = createRegistryKey("density_function_type");

   public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENT_REGISTRY = createRegistryKey("sound_event");
   public static final RegistryKey<Registry<Instrument>> INSTRUMENT_REGISTRY = createRegistryKey("instrument");
   public static final RegistryKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_REGISTRY = createRegistryKey("position_source_type");
   public static final RegistryKey<Registry<Fluid>> FLUID_REGISTRY = createRegistryKey("fluid");
   public static final RegistryKey<Registry<Effect>> MOB_EFFECT_REGISTRY = createRegistryKey("mob_effect");
   public static final RegistryKey<Registry<Block>> BLOCK_REGISTRY = createRegistryKey("block");
   public static final RegistryKey<Registry<Enchantment>> ENCHANTMENT_REGISTRY = createRegistryKey("enchantment");
   public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPE_REGISTRY = createRegistryKey("entity_type");
   public static final RegistryKey<Registry<Item>> ITEM_REGISTRY = createRegistryKey("item");
   public static final RegistryKey<Registry<CrossbowConfig>> CROSSBOW_CONFIG_REGISTRY = createRegistryKey("crossbow_config");
   public static final RegistryKey<Registry<FrisbeeData>> FRISBEE_REGISTRY = createRegistryKey("frisbee");
   public static final RegistryKey<Registry<Potion>> POTION_REGISTRY = createRegistryKey("potion");
   public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPE_REGISTRY = createRegistryKey("particle_type");
   public static final RegistryKey<Registry<TileEntityType<?>>> BLOCK_ENTITY_TYPE_REGISTRY = createRegistryKey("block_entity_type");
   public static final RegistryKey<Registry<PaintingType>> MOTIVE_REGISTRY = createRegistryKey("motive");
   public static final RegistryKey<Registry<ResourceLocation>> CUSTOM_STAT_REGISTRY = createRegistryKey("custom_stat");
   public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS_REGISTRY = createRegistryKey("chunk_status");
   public static final RegistryKey<Registry<IRuleTestType<?>>> RULE_TEST_REGISTRY = createRegistryKey("rule_test");
   public static final RegistryKey<Registry<IPosRuleTests<?>>> POS_RULE_TEST_REGISTRY = createRegistryKey("pos_rule_test");
   public static final RegistryKey<Registry<ContainerType<?>>> MENU_REGISTRY = createRegistryKey("menu");
   public static final RegistryKey<Registry<IRecipeType<?>>> RECIPE_TYPE_REGISTRY = createRegistryKey("recipe_type");
   public static final RegistryKey<Registry<IRecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRY = createRegistryKey("recipe_serializer");
   public static final RegistryKey<Registry<Attribute>> ATTRIBUTE_REGISTRY = createRegistryKey("attribute");
   public static final RegistryKey<Registry<StatType<?>>> STAT_TYPE_REGISTRY = createRegistryKey("stat_type");
   public static final RegistryKey<Registry<VillagerType>> VILLAGER_TYPE_REGISTRY = createRegistryKey("villager_type");
   public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_REGISTRY = createRegistryKey("villager_profession");
   public static final RegistryKey<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE_REGISTRY = createRegistryKey("point_of_interest_type");
   public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_REGISTRY = createRegistryKey("memory_module_type");
   public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPE_REGISTRY = createRegistryKey("sensor_type");
   public static final RegistryKey<Registry<Schedule>> SCHEDULE_REGISTRY = createRegistryKey("schedule");
   public static final RegistryKey<Registry<Activity>> ACTIVITY_REGISTRY = createRegistryKey("activity");
   public static final RegistryKey<Registry<LootPoolEntryType>> LOOT_ENTRY_REGISTRY = createRegistryKey("loot_pool_entry_type");
   public static final RegistryKey<Registry<LootFunctionType>> LOOT_FUNCTION_REGISTRY = createRegistryKey("loot_function_type");
   public static final RegistryKey<Registry<LootConditionType>> LOOT_ITEM_REGISTRY = createRegistryKey("loot_condition_type");
   public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_REGISTRY = createRegistryKey("dimension_type");
   public static final RegistryKey<Registry<World>> DIMENSION_REGISTRY = createRegistryKey("dimension");
   public static final RegistryKey<Registry<Dimension>> LEVEL_STEM_REGISTRY = createRegistryKey("dimension");




   public static final Registry<SoundEvent> SOUND_EVENT = registerSimple(SOUND_EVENT_REGISTRY, () -> {
      return SoundEvents.ITEM_PICKUP;
   });

   public static final Registry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = registerSimple(DENSITY_FUNCTION_TYPE_REGISTRY, DensityFunctions::bootstrap);



   public static final Registry<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = registerSimple(RULE_BLOCK_ENTITY_MODIFIER_REGISTRY, () -> {
      return RuleBlockEntityModifierType.CLEAR;
   });

//   public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = registerSimple(STRUCTURE_PLACEMENT_REGISTRY, () -> {
//      return StructurePlacementType.RANDOM_SPREAD;
//   });

   public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = registerSimple(INT_PROVIDER_TYPE_REGISTRY, () -> {
      return IntProviderType.CONSTANT;
   });

   public static final Registry<String> DECORATED_POT_PATTERNS = registerSimple(DECORATED_POT_PATTERNS_REGISTRY, () -> {
      return DecoratedPotPatterns.bootstrap();
   });

   public static final Registry<TrimMaterial> TRIM_MATERIAL = registerSimple(TRIM_MATERIAL_REGISTRY, () -> {
      return TrimMaterials.DUMMY_TRIM_MATERIAL;
   });

   public static final Registry<TrimPattern> TRIM_PATTERN = registerSimple(TRIM_PATTERN_REGISTRY, () -> {
      return TrimPatterns.DUMMY_TRIM_PATTERN;
   });

   public static final Registry<ToolTrimPattern> TOOL_TRIM_PATTERN = registerSimple(TOOL_TRIM_PATTERN_REGISTRY, () -> {
      return ToolTrimPatterns.DUMMY_TOOL_TRIM_PATTERN;
   });

   public static final Registry<DataComponentType<?>> DATA_COMPONENT_TYPE = registerSimple(DATA_COMPONENT_TYPE_REGISTRY, () -> {
      return DataComponents.MAX_STACK_SIZE;
   });

   public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerSimple(POSITION_SOURCE_TYPE_REGISTRY, () -> {
      return PositionSourceType.BLOCK;
   });

   public static final DefaultedRegistry<GameEvent> GAME_EVENT = registerDefaulted(GAME_EVENT_REGISTRY, "step", () -> {
      return GameEvent.STEP;
   });

   public static final Registry<Instrument> INSTRUMENT = registerSimple(INSTRUMENT_REGISTRY, () -> {
      return Instruments.PONDER_GOAT_HORN;
   });
   public static final DefaultedRegistry<Fluid> FLUID = registerDefaulted(FLUID_REGISTRY, "empty", () -> {
      return Fluids.EMPTY;
   });
   public static final Registry<Effect> MOB_EFFECT = registerSimple(MOB_EFFECT_REGISTRY, () -> {
      return Effects.LUCK;
   });
   public static final DefaultedRegistry<Block> BLOCK = registerDefaulted(BLOCK_REGISTRY, "air", () -> {
      return Blocks.AIR;
   });
   public static final Registry<Enchantment> ENCHANTMENT = registerSimple(ENCHANTMENT_REGISTRY, () -> {
      return Enchantments.BLOCK_FORTUNE;
   });
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = registerDefaulted(ENTITY_TYPE_REGISTRY, "pig", () -> {
      return EntityType.PIG;
   });
   public static final DefaultedRegistry<Item> ITEM = registerDefaulted(ITEM_REGISTRY, "air", () -> {
      return Items.AIR;
   });

   public static final Registry<CrossbowConfig> CROSSBOW_CONFIG = registerSimple(CROSSBOW_CONFIG_REGISTRY, () -> {
      return new CrossbowConfig(new float[]{0,0}, new int[]{0,0}, false, 0, 0, (EffectInstance[]) null);
   });


   public static final DefaultedRegistry<FrisbeeData> FRISBEE_DATA = registerDefaulted(FRISBEE_REGISTRY, "default", () -> {
      return FrisbeeData.DEFAULT;
   });


   public static final DefaultedRegistry<Potion> POTION = registerDefaulted(POTION_REGISTRY, "empty", () -> {
      return Potions.EMPTY;
   });
   public static final Registry<ParticleType<?>> PARTICLE_TYPE = registerSimple(PARTICLE_TYPE_REGISTRY, () -> {
      return ParticleTypes.BLOCK;
   });
   public static final Registry<TileEntityType<?>> BLOCK_ENTITY_TYPE = registerSimple(BLOCK_ENTITY_TYPE_REGISTRY, () -> {
      return TileEntityType.FURNACE;
   });
   public static final DefaultedRegistry<PaintingType> MOTIVE = registerDefaulted(MOTIVE_REGISTRY, "kebab", () -> {
      return PaintingType.KEBAB;
   });
   public static final Registry<ResourceLocation> CUSTOM_STAT = registerSimple(CUSTOM_STAT_REGISTRY, () -> {
      return Stats.JUMP;
   });



   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = registerDefaulted(CHUNK_STATUS_REGISTRY, "empty", () -> {
      return ChunkStatus.EMPTY;
   });
   public static final Registry<IRuleTestType<?>> RULE_TEST = registerSimple(RULE_TEST_REGISTRY, () -> {
      return IRuleTestType.ALWAYS_TRUE_TEST;
   });
   public static final Registry<IPosRuleTests<?>> POS_RULE_TEST = registerSimple(POS_RULE_TEST_REGISTRY, () -> {
      return IPosRuleTests.ALWAYS_TRUE_TEST;
   });
   public static final Registry<ContainerType<?>> MENU = registerSimple(MENU_REGISTRY, () -> {
      return ContainerType.ANVIL;
   });
   public static final Registry<IRecipeType<?>> RECIPE_TYPE = registerSimple(RECIPE_TYPE_REGISTRY, () -> {
      return IRecipeType.CRAFTING;
   });
   public static final Registry<IRecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple(RECIPE_SERIALIZER_REGISTRY, () -> {
      return IRecipeSerializer.SHAPELESS_RECIPE;
   });
   public static final Registry<Attribute> ATTRIBUTE = registerSimple(ATTRIBUTE_REGISTRY, () -> {
      return Attributes.LUCK;
   });
   public static final Registry<StatType<?>> STAT_TYPE = registerSimple(STAT_TYPE_REGISTRY, () -> {
      return Stats.ITEM_USED;
   });
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = registerDefaulted(VILLAGER_TYPE_REGISTRY, "plains", () -> {
      return VillagerType.PLAINS;
   });
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(VILLAGER_PROFESSION_REGISTRY, "none", () -> {
      return VillagerProfession.NONE;
   });
   public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = registerDefaulted(POINT_OF_INTEREST_TYPE_REGISTRY, "unemployed", () -> {
      return PointOfInterestType.UNEMPLOYED;
   });
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted(MEMORY_MODULE_TYPE_REGISTRY, "dummy", () -> {
      return MemoryModuleType.DUMMY;
   });
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = registerDefaulted(SENSOR_TYPE_REGISTRY, "dummy", () -> {
      return SensorType.DUMMY;
   });
   public static final Registry<Schedule> SCHEDULE = registerSimple(SCHEDULE_REGISTRY, () -> {
      return Schedule.EMPTY;
   });
   public static final Registry<Activity> ACTIVITY = registerSimple(ACTIVITY_REGISTRY, () -> {
      return Activity.IDLE;
   });
   public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = registerSimple(LOOT_ENTRY_REGISTRY, () -> {
      return LootEntryManager.EMPTY;
   });
   public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = registerSimple(LOOT_FUNCTION_REGISTRY, () -> {
      return LootFunctionManager.SET_COUNT;
   });
   public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = registerSimple(LOOT_ITEM_REGISTRY, () -> {
      return LootConditionManager.INVERTED;
   });
   public static final RegistryKey<Registry<DimensionSettings>> NOISE_GENERATOR_SETTINGS_REGISTRY = createRegistryKey("worldgen/noise_settings");
   public static final RegistryKey<Registry<ConfiguredSurfaceBuilder<?>>> CONFIGURED_SURFACE_BUILDER_REGISTRY = createRegistryKey("worldgen/configured_surface_builder");
   public static final RegistryKey<Registry<ConfiguredCarver<?>>> CONFIGURED_CARVER_REGISTRY = createRegistryKey("worldgen/configured_carver");
   public static final RegistryKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_feature");
   public static final RegistryKey<Registry<StructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_structure_feature");
   public static final RegistryKey<Registry<StructureProcessorList>> PROCESSOR_LIST_REGISTRY = createRegistryKey("worldgen/processor_list");
   public static final RegistryKey<Registry<JigsawPattern>> TEMPLATE_POOL_REGISTRY = createRegistryKey("worldgen/template_pool");
   public static final RegistryKey<Registry<Biome>> BIOME_REGISTRY = createRegistryKey("worldgen/biome");
   public static final RegistryKey<Registry<SurfaceBuilder<?>>> SURFACE_BUILDER_REGISTRY = createRegistryKey("worldgen/surface_builder");
   public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = registerSimple(SURFACE_BUILDER_REGISTRY, () -> {
      return SurfaceBuilder.DEFAULT;
   });
   public static final RegistryKey<Registry<WorldCarver<?>>> CARVER_REGISTRY = createRegistryKey("worldgen/carver");
   public static final Registry<WorldCarver<?>> CARVER = registerSimple(CARVER_REGISTRY, () -> {
      return WorldCarver.CAVE;
   });
   public static final RegistryKey<Registry<Feature<?>>> FEATURE_REGISTRY = createRegistryKey("worldgen/feature");
   public static final Registry<Feature<?>> FEATURE = registerSimple(FEATURE_REGISTRY, () -> {
      return Feature.ORE;
   });
   public static final RegistryKey<Registry<Structure<?>>> STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/structure_feature");
   public static final Registry<Structure<?>> STRUCTURE_FEATURE = registerSimple(STRUCTURE_FEATURE_REGISTRY, () -> {
      return Structure.MINESHAFT;
   });
   public static final RegistryKey<Registry<IStructurePieceType>> STRUCTURE_PIECE_REGISTRY = createRegistryKey("worldgen/structure_piece");
   public static final Registry<IStructurePieceType> STRUCTURE_PIECE = registerSimple(STRUCTURE_PIECE_REGISTRY, () -> {
      return IStructurePieceType.MINE_SHAFT_ROOM;
   });
   public static final RegistryKey<Registry<Placement<?>>> DECORATOR_REGISTRY = createRegistryKey("worldgen/decorator");
   public static final Registry<Placement<?>> DECORATOR = registerSimple(DECORATOR_REGISTRY, () -> {
      return Placement.NOPE;
   });
   public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_REGISTRY = createRegistryKey("worldgen/block_state_provider_type");
   public static final RegistryKey<Registry<BlockPlacerType<?>>> BLOCK_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/block_placer_type");
   //public static final RegistryKey<Registry<DensityFunction>> DENSITY_FUNCTION_REGISTRY = createRegistryKey("worldgen/density_function");
   public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/foliage_placer_type");
   public static final RegistryKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/trunk_placer_type");
   public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_REGISTRY = createRegistryKey("worldgen/tree_decorator_type");
   public static final RegistryKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_REGISTRY = createRegistryKey("worldgen/feature_size_type");
   public static final RegistryKey<Registry<Codec<? extends BiomeProvider>>> BIOME_SOURCE_REGISTRY = createRegistryKey("worldgen/biome_source");
   public static final RegistryKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_REGISTRY = createRegistryKey("worldgen/chunk_generator");
   public static final RegistryKey<Registry<IStructureProcessorType<?>>> STRUCTURE_PROCESSOR_REGISTRY = createRegistryKey("worldgen/structure_processor");
   public static final RegistryKey<Registry<IJigsawDeserializer<?>>> STRUCTURE_POOL_ELEMENT_REGISTRY = createRegistryKey("worldgen/structure_pool_element");


   public static final RegistryKey<Registry<NormalNoise.NoiseParameters>> NOISE_REGISTRY = createRegistryKey("worldgen/noise");

//   public static final Registry<NormalNoise.NoiseParameters> NOISE = registerSimple(NOISE_REGISTRY, () -> {
//      return Noises.instantiate();
//   });

   public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPES = registerSimple(BLOCK_STATE_PROVIDER_TYPE_REGISTRY, () -> {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   });
   public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPES = registerSimple(BLOCK_PLACER_TYPE_REGISTRY, () -> {
      return BlockPlacerType.SIMPLE_BLOCK_PLACER;
   });
//   public static final Registry<DensityFunction> DENSITY_FUNCTION = registerSimple(DENSITY_FUNCTION_REGISTRY, () -> {
//      return DensityFunctions.zero();
//   });
   public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = registerSimple(FOLIAGE_PLACER_TYPE_REGISTRY, () -> {
      return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
   });
   public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = registerSimple(TRUNK_PLACER_TYPE_REGISTRY, () -> {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   });
   public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = registerSimple(TREE_DECORATOR_TYPE_REGISTRY, () -> {
      return TreeDecoratorType.LEAVE_VINE;
   });
   public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPES = registerSimple(FEATURE_SIZE_TYPE_REGISTRY, () -> {
      return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
   });
   public static final Registry<Codec<? extends BiomeProvider>> BIOME_SOURCE = registerSimple(BIOME_SOURCE_REGISTRY, Lifecycle.stable(), () -> {
      return BiomeProvider.CODEC;
   });
   public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerSimple(CHUNK_GENERATOR_REGISTRY, Lifecycle.stable(), () -> {
      return ChunkGenerator.CODEC;
   });
   public static final Registry<IStructureProcessorType<?>> STRUCTURE_PROCESSOR = registerSimple(STRUCTURE_PROCESSOR_REGISTRY, () -> {
      return IStructureProcessorType.BLOCK_IGNORE;
   });
   public static final Registry<IJigsawDeserializer<?>> STRUCTURE_POOL_ELEMENT = registerSimple(STRUCTURE_POOL_ELEMENT_REGISTRY, () -> {
      return IJigsawDeserializer.EMPTY;
   });
   private final RegistryKey<? extends Registry<T>> key;
   private final Lifecycle lifecycle;

   private static <T> RegistryKey<Registry<T>> createRegistryKey(String p_239741_0_) {
      return RegistryKey.createRegistryKey(new ResourceLocation(p_239741_0_));
   }



   public static <T extends MutableRegistry<?>> void checkRegistry(MutableRegistry<T> p_239738_0_) {
      p_239738_0_.forEach((p_239739_1_) -> {
         if (p_239739_1_.keySet().isEmpty()) {
            LOGGER.error("Registry '{}' was empty after loading", (Object)p_239738_0_.getKey(p_239739_1_));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               throw new IllegalStateException("Registry: '" + p_239738_0_.getKey(p_239739_1_) + "' is empty, not allowed, fix me!");
            }
         }

         if (p_239739_1_ instanceof DefaultedRegistry) {
            ResourceLocation resourcelocation = ((DefaultedRegistry)p_239739_1_).getDefaultKey();
            Validate.notNull(p_239739_1_.get(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
         }

      });
   }

   private static <T> Registry<T> registerSimple(RegistryKey<? extends Registry<T>> p_239746_0_, Supplier<T> p_239746_1_) {
      return registerSimple(p_239746_0_, Lifecycle.experimental(), p_239746_1_);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(RegistryKey<? extends Registry<T>> p_239745_0_, String p_239745_1_, Supplier<T> p_239745_2_) {
      return registerDefaulted(p_239745_0_, p_239745_1_, Lifecycle.experimental(), p_239745_2_);
   }

   private static <T> Registry<T> registerSimple(RegistryKey<? extends Registry<T>> p_239742_0_, Lifecycle p_239742_1_, Supplier<T> p_239742_2_) {
      return internalRegister(p_239742_0_, new SimpleRegistry<>(p_239742_0_, p_239742_1_), p_239742_2_, p_239742_1_);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(RegistryKey<? extends Registry<T>> p_239744_0_, String p_239744_1_, Lifecycle p_239744_2_, Supplier<T> p_239744_3_) {
      return internalRegister(p_239744_0_, new DefaultedRegistry<>(p_239744_1_, p_239744_0_, p_239744_2_), p_239744_3_, p_239744_2_);
   }

   private static <T, R extends MutableRegistry<T>> R internalRegister(RegistryKey<? extends Registry<T>> p_239743_0_, R p_239743_1_, Supplier<T> p_239743_2_, Lifecycle p_239743_3_) {
      ResourceLocation resourcelocation = p_239743_0_.location();
      LOADERS.put(resourcelocation, p_239743_2_);
      MutableRegistry<R> mutableregistry = (MutableRegistry<R>)WRITABLE_REGISTRY;
      return (R)mutableregistry.register((RegistryKey)p_239743_0_, p_239743_1_, p_239743_3_);
   }

   protected Registry(RegistryKey<? extends Registry<T>> p_i232510_1_, Lifecycle p_i232510_2_) {
      this.key = p_i232510_1_;
      this.lifecycle = p_i232510_2_;
   }


   public RegistryKey<? extends Registry<T>> key() {
      return this.key;
   }

   public String toString() {
      return "Registry[" + this.key + " (" + this.lifecycle + ")]";
   }

   public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> p_decode_1_, U p_decode_2_) {
      return p_decode_1_.compressMaps() ? p_decode_1_.getNumberValue(p_decode_2_).flatMap((p_239740_1_) -> {
         T t = this.byId(p_239740_1_.intValue());
         return t == null ? DataResult.error("Unknown registry id: " + p_239740_1_) : DataResult.success(t, this.lifecycle(t));
      }).map((p_239736_1_) -> {
         return Pair.of((T)p_239736_1_, p_decode_1_.empty());
      }) : ResourceLocation.CODEC.decode(p_decode_1_, p_decode_2_).flatMap((p_239735_1_) -> {
         T t = this.get(p_239735_1_.getFirst());
         return t == null ? DataResult.error("Unknown registry key: " + p_239735_1_.getFirst()) : DataResult.success(Pair.of(t, p_239735_1_.getSecond()), this.lifecycle(t));
      });
   }

   public <U> DataResult<U> encode(T p_encode_1_, DynamicOps<U> p_encode_2_, U p_encode_3_) {
      ResourceLocation resourcelocation = this.getKey(p_encode_1_);
      if (resourcelocation == null) {
         return DataResult.error("Unknown registry element " + p_encode_1_);
      } else {
         return p_encode_2_.compressMaps() ? p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createInt(this.getId(p_encode_1_))).setLifecycle(this.lifecycle) : p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createString(resourcelocation.toString())).setLifecycle(this.lifecycle);
      }
   }

   public <U> Stream<U> keys(DynamicOps<U> p_keys_1_) {
      return this.keySet().stream().map((p_243574_1_) -> {
         return p_keys_1_.createString(p_243574_1_.toString());
      });
   }

   @Nullable
   public abstract ResourceLocation getKey(T p_177774_1_);

   public abstract Optional<RegistryKey<T>> getResourceKey(T p_230519_1_);

   public abstract int getId(@Nullable T p_148757_1_);

   @Nullable
   public abstract T get(@Nullable RegistryKey<T> p_230516_1_);

   @Nullable
   public abstract T get(@Nullable ResourceLocation p_82594_1_);

   protected abstract Lifecycle lifecycle(T p_241876_1_);

   public abstract Lifecycle elementsLifecycle();

   public Optional<T> getOptional(@Nullable ResourceLocation p_241873_1_) {
      return Optional.ofNullable(this.get(p_241873_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<T> getOptional(@Nullable RegistryKey<T> p_243575_1_) {
      return Optional.ofNullable(this.get(p_243575_1_));
   }

   public T getOrThrow(RegistryKey<T> p_243576_1_) {
      T t = this.get(p_243576_1_);
      if (t == null) {
         throw new IllegalStateException("Missing: " + p_243576_1_);
      } else {
         return t;
      }
   }


   public abstract Set<ResourceLocation> keySet();

   public abstract Set<Entry<RegistryKey<T>, T>> entrySet();

   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @OnlyIn(Dist.CLIENT)
   public abstract boolean containsKey(ResourceLocation p_212607_1_);

   public static <T> T register(Registry<? super T> p_218325_0_, String p_218325_1_, T p_218325_2_) {
      return register(p_218325_0_, new ResourceLocation(p_218325_1_), p_218325_2_);
   }

   public static <V, T extends V> T register(Registry<V> p_218322_0_, ResourceLocation p_218322_1_, T p_218322_2_) {
      return ((MutableRegistry<V>)p_218322_0_).register(RegistryKey.create(p_218322_0_.key, p_218322_1_), p_218322_2_, Lifecycle.stable());
   }

   public static <V, T extends V> T register(Registry<V> p_218322_0_, RegistryKey<V> v, T p_218322_2_) {
      return ((MutableRegistry<V>)p_218322_0_).register(RegistryKey.create(p_218322_0_.key, v.location()), p_218322_2_, Lifecycle.stable());
   }

   public static <V, T extends V> T registerMapping(Registry<V> p_218343_0_, int p_218343_1_, String p_218343_2_, T p_218343_3_) {
      return ((MutableRegistry<V>)p_218343_0_).registerMapping(p_218343_1_, RegistryKey.create(p_218343_0_.key, new ResourceLocation(p_218343_2_)), p_218343_3_, Lifecycle.stable());
   }

   static {
      WorldGenRegistries.bootstrap();
      LOADERS.forEach((p_239747_0_, p_239747_1_) -> {
         if (p_239747_1_.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", (Object)p_239747_0_);
         }

      });
      checkRegistry(WRITABLE_REGISTRY);
   }

   public Codec<T> byNameCodec() {
      return ResourceLocation.CODEC.flatXmap(
              name -> {
                 T value = this.get(name);
                 return value == null
                         ? DataResult.error("Unknown registry key: " + name)
                         : DataResult.success(value);
              },
              value -> {
                 ResourceLocation key = this.getKey(value);
                 return key == null
                         ? DataResult.error("Unknown registry element: " + value)
                         : DataResult.success(key);
              }
      );
   }

   public IObjectIntIterable<T> asHolderIdMap() {
      return new IObjectIntIterable<T>(){

         @Override
         public int getId(T holder) {
            return Registry.this.getId(holder);
         }

         @Override
         @Nullable
         public T byId(int n) {
            return Registry.this.byId(n);
         }


         public int size() {
            return Registry.this.stream().toList().size();
         }

         @Override
         public Iterator<T> iterator() {
            return Registry.this.stream().iterator();
         }
      };
   }

}
