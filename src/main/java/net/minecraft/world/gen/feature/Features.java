package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockplacer.ColumnBlockPlacer;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.AxisRotatingBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.PlainFlowerBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.foliageplacer.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.treedecorator.*;
import net.minecraft.world.gen.trunkplacer.*;
import net.minecraft.world.server.ServerWorld;

public class Features {
   public static final ConfiguredFeature<?, ?> END_SPIKE = register("end_spike", Feature.END_SPIKE.configured(new EndSpikeFeatureConfig(false, ImmutableList.of(), (BlockPos)null)));
   public static final ConfiguredFeature<?, ?> END_GATEWAY = register("end_gateway", Feature.END_GATEWAY.configured(EndGatewayConfig.knownExit(ServerWorld.END_SPAWN_POINT, true)).decorated(Placement.END_GATEWAY.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED = register("end_gateway_delayed", Feature.END_GATEWAY.configured(EndGatewayConfig.delayedExitSearch()));
   public static final ConfiguredFeature<?, ?> CHORUS_PLANT = register("chorus_plant", Feature.CHORUS_PLANT.configured(IFeatureConfig.NONE).decorated(Features.Placements.HEIGHTMAP_SQUARE).countRandom(4));
   public static final ConfiguredFeature<?, ?> END_ISLAND = register("end_island", Feature.END_ISLAND.configured(IFeatureConfig.NONE));
   public static final ConfiguredFeature<?, ?> END_ISLAND_DECORATED = register("end_island_decorated", END_ISLAND.decorated(Placement.END_ISLAND.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> DELTA = register("delta", Feature.DELTA_FEATURE.configured(new BasaltDeltasFeature(Features.States.LAVA, Features.States.MAGMA_BLOCK, FeatureSpread.of(3, 4), FeatureSpread.of(0, 2))).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(40))));
   public static final ConfiguredFeature<?, ?> SMALL_BASALT_COLUMNS = register("small_basalt_columns", Feature.BASALT_COLUMNS.configured(new ColumnConfig(FeatureSpread.fixed(1), FeatureSpread.of(1, 3))).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4))));
   public static final ConfiguredFeature<?, ?> LARGE_BASALT_COLUMNS = register("large_basalt_columns", Feature.BASALT_COLUMNS.configured(new ColumnConfig(FeatureSpread.of(2, 1), FeatureSpread.of(5, 5))).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))));
   public static final ConfiguredFeature<?, ?> BASALT_BLOBS = register("basalt_blobs", Feature.REPLACE_BLOBS.configured(new BlobReplacementConfig(Features.States.NETHERRACK, Features.States.BASALT, FeatureSpread.of(3, 4))).range(128).squared().count(75));
   public static final ConfiguredFeature<?, ?> BLACKSTONE_BLOBS = register("blackstone_blobs", Feature.REPLACE_BLOBS.configured(new BlobReplacementConfig(Features.States.NETHERRACK, Features.States.BLACKSTONE, FeatureSpread.of(3, 4))).range(128).squared().count(25));
   public static final ConfiguredFeature<?, ?> GLOWSTONE_EXTRA = register("glowstone_extra", Feature.GLOWSTONE_BLOB.configured(IFeatureConfig.NONE).decorated(Placement.GLOWSTONE.configured(new FeatureSpreadConfig(10))));
   public static final ConfiguredFeature<?, ?> GLOWSTONE = register("glowstone", Feature.GLOWSTONE_BLOB.configured(IFeatureConfig.NONE).range(128).squared().count(10));
   public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_VEGETATION = register("crimson_forest_vegetation", Feature.NETHER_FOREST_VEGETATION.configured(Features.Configs.CRIMSON_FOREST_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(6))));
   public static final ConfiguredFeature<?, ?> WARPED_FOREST_VEGETATION = register("warped_forest_vegetation", Feature.NETHER_FOREST_VEGETATION.configured(Features.Configs.WARPED_FOREST_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(5))));
   public static final ConfiguredFeature<?, ?> NETHER_SPROUTS = register("nether_sprouts", Feature.NETHER_FOREST_VEGETATION.configured(Features.Configs.NETHER_SPROUTS_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4))));
   public static final ConfiguredFeature<?, ?> TWISTING_VINES = register("twisting_vines", Feature.TWISTING_VINES.configured(IFeatureConfig.NONE).range(128).squared().count(10));
   public static final ConfiguredFeature<?, ?> WEEPING_VINES = register("weeping_vines", Feature.WEEPING_VINES.configured(IFeatureConfig.NONE).range(128).squared().count(10));
   public static final ConfiguredFeature<?, ?> BASALT_PILLAR = register("basalt_pillar", Feature.BASALT_PILLAR.configured(IFeatureConfig.NONE).range(128).squared().count(10));
   public static final ConfiguredFeature<?, ?> SEAGRASS_COLD = register("seagrass_cold", Feature.SEAGRASS.configured(new ProbabilityConfig(0.3F)).count(32).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_COLD = register("seagrass_deep_cold", Feature.SEAGRASS.configured(new ProbabilityConfig(0.8F)).count(40).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_NORMAL = register("seagrass_normal", Feature.SEAGRASS.configured(new ProbabilityConfig(0.3F)).count(48).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_RIVER = register("seagrass_river", Feature.SEAGRASS.configured(new ProbabilityConfig(0.4F)).count(48).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP = register("seagrass_deep", Feature.SEAGRASS.configured(new ProbabilityConfig(0.8F)).count(48).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_SWAMP = register("seagrass_swamp", Feature.SEAGRASS.configured(new ProbabilityConfig(0.6F)).count(64).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_WARM = register("seagrass_warm", Feature.SEAGRASS.configured(new ProbabilityConfig(0.3F)).count(80).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_WARM = register("seagrass_deep_warm", Feature.SEAGRASS.configured(new ProbabilityConfig(0.8F)).count(80).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> SEA_PICKLE = register("sea_pickle", Feature.SEA_PICKLE.configured(new FeatureSpreadConfig(20)).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE).chance(16));
   public static final ConfiguredFeature<?, ?> ICE_SPIKE = register("ice_spike", Feature.ICE_SPIKE.configured(IFeatureConfig.NONE).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(3));
   public static final ConfiguredFeature<?, ?> ICE_PATCH = register("ice_patch", Feature.ICE_PATCH.configured(new SphereReplaceConfig(Features.States.PACKED_ICE, FeatureSpread.of(2, 1), 1, ImmutableList.of(Features.States.DIRT, Features.States.GRASS_BLOCK, Features.States.PODZOL, Features.States.COARSE_DIRT, Features.States.MYCELIUM, Features.States.SNOW_BLOCK, Features.States.ICE))).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(2));
   public static final ConfiguredFeature<?, ?> FOREST_ROCK = register("forest_rock", Feature.FOREST_ROCK.configured(new BlockStateFeatureConfig(Features.States.MOSSY_COBBLESTONE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).countRandom(2));
   public static final ConfiguredFeature<?, ?> SEAGRASS_SIMPLE = register("seagrass_simple", Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(Features.States.SEAGRASS, ImmutableList.of(Features.States.STONE), ImmutableList.of(Features.States.WATER), ImmutableList.of(Features.States.WATER))).decorated(Placement.CARVING_MASK.configured(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.1F))));
   public static final ConfiguredFeature<?, ?> ICEBERG_PACKED = register("iceberg_packed", Feature.ICEBERG.configured(new BlockStateFeatureConfig(Features.States.PACKED_ICE)).decorated(Placement.ICEBERG.configured(NoPlacementConfig.INSTANCE)).chance(16));
   public static final ConfiguredFeature<?, ?> ICEBERG_BLUE = register("iceberg_blue", Feature.ICEBERG.configured(new BlockStateFeatureConfig(Features.States.BLUE_ICE)).decorated(Placement.ICEBERG.configured(NoPlacementConfig.INSTANCE)).chance(200));
   public static final ConfiguredFeature<?, ?> KELP_COLD = register("kelp_cold", Feature.KELP.configured(IFeatureConfig.NONE).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP).squared().decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(120, 80.0D, 0.0D))));
   public static final ConfiguredFeature<?, ?> KELP_WARM = register("kelp_warm", Feature.KELP.configured(IFeatureConfig.NONE).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP).squared().decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(80, 80.0D, 0.0D))));
   public static final ConfiguredFeature<?, ?> BLUE_ICE = register("blue_ice", Feature.BLUE_ICE.configured(IFeatureConfig.NONE).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(30, 32, 64))).squared().countRandom(19));
   public static final ConfiguredFeature<?, ?> BAMBOO_LIGHT = register("bamboo_light", Feature.BAMBOO.configured(new ProbabilityConfig(0.0F)).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(16));
   public static final ConfiguredFeature<?, ?> BAMBOO = register("bamboo", Feature.BAMBOO.configured(new ProbabilityConfig(0.2F)).decorated(Features.Placements.HEIGHTMAP_WORLD_SURFACE).squared().decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(160, 80.0D, 0.3D))));
   public static final ConfiguredFeature<?, ?> VINES = register("vines", Feature.VINES.configured(IFeatureConfig.NONE).squared().count(50));
   public static final ConfiguredFeature<?, ?> LAKE_WATER = register("lake_water", Feature.LAKE.configured(new BlockStateFeatureConfig(Features.States.WATER)).decorated(Placement.WATER_LAKE.configured(new ChanceConfig(4))));
   public static final ConfiguredFeature<?, ?> LAKE_LAVA = register("lake_lava", Feature.LAKE.configured(new BlockStateFeatureConfig(Features.States.LAVA)).decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(80))));
   public static final ConfiguredFeature<?, ?> DISK_CLAY = register("disk_clay", Feature.DISK.configured(new SphereReplaceConfig(Features.States.CLAY, FeatureSpread.of(2, 1), 1, ImmutableList.of(Features.States.DIRT, Features.States.CLAY))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> DISK_GRAVEL = register("disk_gravel", Feature.DISK.configured(new SphereReplaceConfig(Features.States.GRAVEL, FeatureSpread.of(2, 3), 2, ImmutableList.of(Features.States.DIRT, Features.States.GRASS_BLOCK))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> DISK_SAND = register("disk_sand", Feature.DISK.configured(new SphereReplaceConfig(Features.States.SAND, FeatureSpread.of(2, 4), 2, ImmutableList.of(Features.States.DIRT, Features.States.GRASS_BLOCK))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE).count(3));
   public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER = register("freeze_top_layer", Feature.FREEZE_TOP_LAYER.configured(IFeatureConfig.NONE));
   public static final ConfiguredFeature<?, ?> BONUS_CHEST = register("bonus_chest", Feature.BONUS_CHEST.configured(IFeatureConfig.NONE));
   public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM = register("void_start_platform", Feature.VOID_START_PLATFORM.configured(IFeatureConfig.NONE));
   public static final ConfiguredFeature<?, ?> MONSTER_ROOM = register("monster_room", Feature.MONSTER_ROOM.configured(IFeatureConfig.NONE).range(256).squared().count(8));
   public static final ConfiguredFeature<?, ?> WELL = register("desert_well", Feature.DESERT_WELL.configured(IFeatureConfig.NONE).decorated(Features.Placements.HEIGHTMAP_SQUARE).chance(1000));
   public static final ConfiguredFeature<?, ?> FOSSIL = register("fossil", Feature.FOSSIL.configured(IFeatureConfig.NONE).chance(64));
   public static final ConfiguredFeature<?, ?> SPRING_LAVA_DOUBLE = register("spring_lava_double", Feature.SPRING.configured(Features.Configs.LAVA_SPRING_CONFIG).decorated(Placement.RANGE_VERY_BIASED.configured(new TopSolidRangeConfig(8, 16, 256))).squared().count(40));
   public static final ConfiguredFeature<?, ?> SPRING_LAVA = register("spring_lava", Feature.SPRING.configured(Features.Configs.LAVA_SPRING_CONFIG).decorated(Placement.RANGE_VERY_BIASED.configured(new TopSolidRangeConfig(8, 16, 256))).squared().count(20));
   public static final ConfiguredFeature<?, ?> SPRING_DELTA = register("spring_delta", Feature.SPRING.configured(new LiquidsConfig(Features.States.LAVA_STATE, true, 4, 1, ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE))).decorated(Features.Placements.RANGE_4_8_ROOFED).squared().count(16));
   public static final ConfiguredFeature<?, ?> SPRING_CLOSED = register("spring_closed", Feature.SPRING.configured(Features.Configs.CLOSED_NETHER_SPRING_CONFIG).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(16));
   public static final ConfiguredFeature<?, ?> SPRING_CLOSED_DOUBLE = register("spring_closed_double", Feature.SPRING.configured(Features.Configs.CLOSED_NETHER_SPRING_CONFIG).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(32));
   public static final ConfiguredFeature<?, ?> SPRING_OPEN = register("spring_open", Feature.SPRING.configured(new LiquidsConfig(Features.States.LAVA_STATE, false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK))).decorated(Features.Placements.RANGE_4_8_ROOFED).squared().count(8));
   public static final ConfiguredFeature<?, ?> SPRING_WATER = register("spring_water", Feature.SPRING.configured(new LiquidsConfig(Features.States.WATER_STATE, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE))).decorated(Placement.RANGE_BIASED.configured(new TopSolidRangeConfig(8, 8, 256))).squared().count(50));
   public static final ConfiguredFeature<?, ?> PILE_HAY = register("pile_hay", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig(new AxisRotatingBlockStateProvider(Blocks.HAY_BLOCK))));
   public static final ConfiguredFeature<?, ?> PILE_MELON = register("pile_melon", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig(new SimpleBlockStateProvider(Features.States.MELON))));
   public static final ConfiguredFeature<?, ?> PILE_SNOW = register("pile_snow", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig(new SimpleBlockStateProvider(Features.States.SNOW))));
   public static final ConfiguredFeature<?, ?> PILE_ICE = register("pile_ice", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(Features.States.BLUE_ICE, 1).add(Features.States.PACKED_ICE, 5))));
   public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = register("pile_pumpkin", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(Features.States.PUMPKIN, 19).add(Features.States.JACK_O_LANTERN, 1))));
   public static final ConfiguredFeature<?, ?> PILE_WHITE_PUMPKIN = register("pile_white_pumpkin", Feature.BLOCK_PILE.configured(new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(States.WHITE_PUMPKIN, 19).add(Blocks.WHITE_JACK_O_LANTERN.defaultBlockState(), 1))));
   public static final ConfiguredFeature<?, ?> PATCH_FIRE = register("patch_fire", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.FIRE), SimpleBlockPlacer.INSTANCE)).tries(64).whitelist(ImmutableSet.of(Features.States.NETHERRACK.getBlock())).noProjection().build()).decorated(Features.Placements.FIRE));
   public static final ConfiguredFeature<?, ?> PATCH_SOUL_FIRE = register("patch_soul_fire", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SOUL_FIRE), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(Features.States.SOUL_SOIL.getBlock())).noProjection().build()).decorated(Features.Placements.FIRE));
   public static final ConfiguredFeature<?, ?> PATCH_BROWN_MUSHROOM = register("patch_brown_mushroom", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.BROWN_MUSHROOM), SimpleBlockPlacer.INSTANCE)).tries(64).noProjection().build()));
   public static final ConfiguredFeature<?, ?> PATCH_RED_MUSHROOM = register("patch_red_mushroom", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.RED_MUSHROOM), SimpleBlockPlacer.INSTANCE)).tries(64).noProjection().build()));
   public static final ConfiguredFeature<?, ?> PATCH_CRIMSON_ROOTS = register("patch_crimson_roots", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.CRIMSON_ROOTS), new SimpleBlockPlacer())).tries(64).noProjection().build()).range(128));
   public static final ConfiguredFeature<?, ?> PATCH_SUNFLOWER = register("patch_sunflower", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SUNFLOWER), new DoublePlantBlockPlacer())).tries(64).noProjection().build()).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(10));
   public static final ConfiguredFeature<?, ?> PATCH_PUMPKIN = register("patch_pumpkin", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.PUMPKIN), SimpleBlockPlacer.INSTANCE)).tries(64).whitelist(ImmutableSet.of(Features.States.GRASS_BLOCK.getBlock())).noProjection().build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(32));
   public static final ConfiguredFeature<?, ?> PATCH_WHITE_PUMPKIN = register("patch_white_pumpkin", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(States.WHITE_PUMPKIN), SimpleBlockPlacer.INSTANCE)).tries(128).whitelist(ImmutableSet.of(Features.States.GRASS_BLOCK.getBlock(), Blocks.PALE_MOSS_BLOCK.defaultBlockState().getBlock())).noProjection().build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(1));

   public static final ConfiguredFeature<?, ?> PATCH_TAIGA_GRASS = register("patch_taiga_grass", Feature.RANDOM_PATCH.configured(Features.Configs.TAIGA_GRASS_CONFIG));
   public static final ConfiguredFeature<?, ?> PATCH_BERRY_BUSH = register("patch_berry_bush", Feature.RANDOM_PATCH.configured(Features.Configs.SWEET_BERRY_BUSH_CONFIG));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_PLAIN = register("patch_grass_plain", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).decorated(Placement.COUNT_NOISE.configured(new NoiseDependant(-0.8D, 5, 10))));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_FOREST = register("patch_grass_forest", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(2));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_BADLANDS = register("patch_grass_badlands", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_SAVANNA = register("patch_grass_savanna", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(20));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_NORMAL = register("patch_grass_normal", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(5));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA_2 = register("patch_grass_taiga_2", Feature.RANDOM_PATCH.configured(Features.Configs.TAIGA_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA = register("patch_grass_taiga", Feature.RANDOM_PATCH.configured(Features.Configs.TAIGA_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(7));
   public static final ConfiguredFeature<?, ?> PATCH_GRASS_JUNGLE = register("patch_grass_jungle", Feature.RANDOM_PATCH.configured(Features.Configs.JUNGLE_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(25));
   public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_2 = register("patch_dead_bush_2", Feature.RANDOM_PATCH.configured(Features.Configs.DEAD_BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(2));
   public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH = register("patch_dead_bush", Feature.RANDOM_PATCH.configured(Features.Configs.DEAD_BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_BADLANDS = register("patch_dead_bush_badlands", Feature.RANDOM_PATCH.configured(Features.Configs.DEAD_BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(20));
   public static final ConfiguredFeature<?, ?> PATCH_MELON = register("patch_melon", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.MELON), SimpleBlockPlacer.INSTANCE)).tries(64).whitelist(ImmutableSet.of(Features.States.GRASS_BLOCK.getBlock())).canReplace().noProjection().build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> PATCH_BERRY_SPARSE = register("patch_berry_sparse", PATCH_BERRY_BUSH.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> PATCH_BERRY_DECORATED = register("patch_berry_decorated", PATCH_BERRY_BUSH.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));
   public static final ConfiguredFeature<?, ?> PATCH_WATERLILLY = register("patch_waterlilly", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.LILY_PAD), SimpleBlockPlacer.INSTANCE)).tries(10).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(4));
   public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS_2 = register("patch_tall_grass_2", Feature.RANDOM_PATCH.configured(Features.Configs.TALL_GRASS_CONFIG).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP).squared().decorated(Placement.COUNT_NOISE.configured(new NoiseDependant(-0.8D, 0, 7))));
   public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS = register("patch_tall_grass", Feature.RANDOM_PATCH.configured(Features.Configs.TALL_GRASS_CONFIG).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(7));
   public static final ConfiguredFeature<?, ?> PATCH_LARGE_FERN = register("patch_large_fern", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.LARGE_FERN), new DoublePlantBlockPlacer())).tries(64).noProjection().build()).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(7));
   public static final ConfiguredFeature<?, ?> PATCH_CACTUS = register("patch_cactus", Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.CACTUS), new ColumnBlockPlacer(1, 2))).tries(10).noProjection().build()));
   public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DESERT = register("patch_cactus_desert", PATCH_CACTUS.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(10));
   public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DECORATED = register("patch_cactus_decorated", PATCH_CACTUS.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(5));
   public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_SWAMP = register("patch_sugar_cane_swamp", Feature.RANDOM_PATCH.configured(Features.Configs.SUGAR_CANE_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(20));
   public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_DESERT = register("patch_sugar_cane_desert", Feature.RANDOM_PATCH.configured(Features.Configs.SUGAR_CANE_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(60));
   public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_BADLANDS = register("patch_sugar_cane_badlands", Feature.RANDOM_PATCH.configured(Features.Configs.SUGAR_CANE_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(13));
   public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE = register("patch_sugar_cane", Feature.RANDOM_PATCH.configured(Features.Configs.SUGAR_CANE_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(10));
   public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NETHER = register("brown_mushroom_nether", PATCH_BROWN_MUSHROOM.range(128).chance(2));
   public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NETHER = register("red_mushroom_nether", PATCH_RED_MUSHROOM.range(128).chance(2));
   public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NORMAL = register("brown_mushroom_normal", PATCH_BROWN_MUSHROOM.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(4));
   public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NORMAL = register("red_mushroom_normal", PATCH_RED_MUSHROOM.decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(8));
   public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_TAIGA = register("brown_mushroom_taiga", PATCH_BROWN_MUSHROOM.chance(4).decorated(Features.Placements.HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> RED_MUSHROOM_TAIGA = register("red_mushroom_taiga", PATCH_RED_MUSHROOM.chance(8).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
   public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_GIANT = register("brown_mushroom_giant", BROWN_MUSHROOM_TAIGA.count(3));
   public static final ConfiguredFeature<?, ?> RED_MUSHROOM_GIANT = register("red_mushroom_giant", RED_MUSHROOM_TAIGA.count(3));
   public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_SWAMP = register("brown_mushroom_swamp", BROWN_MUSHROOM_TAIGA.count(8));
   public static final ConfiguredFeature<?, ?> RED_MUSHROOM_SWAMP = register("red_mushroom_swamp", RED_MUSHROOM_TAIGA.count(8));
   public static final ConfiguredFeature<?, ?> ORE_MAGMA = register("ore_magma", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.MAGMA_BLOCK, 33)).decorated(Placement.MAGMA.configured(NoPlacementConfig.INSTANCE)).squared().count(4));
   public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = register("ore_soul_sand", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.SOUL_SAND, 12)).range(32).squared().count(12));
   public static final ConfiguredFeature<?, ?> ORE_GOLD_DELTAS = register("ore_gold_deltas", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.NETHER_GOLD_ORE, 10)).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(20));
   public static final ConfiguredFeature<?, ?> ORE_QUARTZ_DELTAS = register("ore_quartz_deltas", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.NETHER_QUARTZ_ORE, 14)).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(32));
   public static final ConfiguredFeature<?, ?> ORE_GOLD_NETHER = register("ore_gold_nether", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.NETHER_GOLD_ORE, 10)).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(10));
   public static final ConfiguredFeature<?, ?> ORE_QUARTZ_NETHER = register("ore_quartz_nether", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.NETHER_QUARTZ_ORE, 14)).decorated(Features.Placements.RANGE_10_20_ROOFED).squared().count(16));
   public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = register("ore_gravel_nether", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.GRAVEL, 33)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(5, 0, 37))).squared().count(2));
   public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = register("ore_blackstone", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Features.States.BLACKSTONE, 33)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(5, 10, 37))).squared().count(2));
   public static final ConfiguredFeature<?, ?> ORE_DIRT = register("ore_dirt", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.DIRT, 33)).range(256).squared().count(10));
   public static final ConfiguredFeature<?, ?> ORE_GRAVEL = register("ore_gravel", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.GRAVEL, 33)).range(256).squared().count(8));
   public static final ConfiguredFeature<?, ?> ORE_GRANITE = register("ore_granite", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.GRANITE, 33)).range(80).squared().count(10));
   public static final ConfiguredFeature<?, ?> ORE_DIORITE = register("ore_diorite", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.DIORITE, 33)).range(80).squared().count(10));
   public static final ConfiguredFeature<?, ?> ORE_ANDESITE = register("ore_andesite", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.ANDESITE, 33)).range(80).squared().count(10));
   public static final ConfiguredFeature<?, ?> ORE_COAL = register("ore_coal", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.COAL_ORE, 17)).range(128).squared().count(20));
   public static final ConfiguredFeature<?, ?> ORE_IRON = register("ore_iron", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.IRON_ORE, 9)).range(64).squared().count(20));
   public static final ConfiguredFeature<?, ?> ORE_GOLD_EXTRA = register("ore_gold_extra", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.GOLD_ORE, 9)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(32, 32, 80))).squared().count(20));
   public static final ConfiguredFeature<?, ?> ORE_GOLD = register("ore_gold", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.GOLD_ORE, 9)).range(32).squared().count(2));
   public static final ConfiguredFeature<?, ?> ORE_REDSTONE = register("ore_redstone", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.REDSTONE_ORE, 8)).range(16).squared().count(8));
   public static final ConfiguredFeature<?, ?> ORE_DIAMOND = register("ore_diamond", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.DIAMOND_ORE, 8)).range(16).squared());
   public static final ConfiguredFeature<?, ?> ORE_LAPIS = register("ore_lapis", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.LAPIS_ORE, 7)).decorated(Placement.DEPTH_AVERAGE.configured(new DepthAverageConfig(16, 16))).squared());
   public static final ConfiguredFeature<?, ?> ORE_INFESTED = register("ore_infested", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.States.INFESTED_STONE, 9)).range(64).squared().count(7));
   public static final ConfiguredFeature<?, ?> ORE_EMERALD = register("ore_emerald", Feature.EMERALD_ORE.configured(new ReplaceBlockConfig(Features.States.STONE, Features.States.EMERALD_ORE)).decorated(Placement.EMERALD_ORE.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> ORE_DEBRIS_LARGE = register("ore_debris_large", Feature.NO_SURFACE_ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, Features.States.ANCIENT_DEBRIS, 3)).decorated(Placement.DEPTH_AVERAGE.configured(new DepthAverageConfig(16, 8))).squared());
   public static final ConfiguredFeature<?, ?> ORE_DEBRIS_SMALL = register("ore_debris_small", Feature.NO_SURFACE_ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, Features.States.ANCIENT_DEBRIS, 2)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(8, 16, 128))).squared());
   public static final ConfiguredFeature<?, ?> CRIMSON_FUNGI = register("crimson_fungi", Feature.HUGE_FUNGUS.configured(HugeFungusConfig.HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(8))));
   public static final ConfiguredFeature<HugeFungusConfig, ?> CRIMSON_FUNGI_PLANTED = register("crimson_fungi_planted", Feature.HUGE_FUNGUS.configured(HugeFungusConfig.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG));
   public static final ConfiguredFeature<?, ?> WARPED_FUNGI = register("warped_fungi", Feature.HUGE_FUNGUS.configured(HugeFungusConfig.HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(8))));
   public static final ConfiguredFeature<HugeFungusConfig, ?> WARPED_FUNGI_PLANTED = register("warped_fungi_planted", Feature.HUGE_FUNGUS.configured(HugeFungusConfig.HUGE_WARPED_FUNGI_PLANTED_CONFIG));
   public static final ConfiguredFeature<?, ?> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", Feature.HUGE_BROWN_MUSHROOM.configured(new BigMushroomFeatureConfig(new SimpleBlockStateProvider(Features.States.HUGE_BROWN_MUSHROOM), new SimpleBlockStateProvider(Features.States.HUGE_MUSHROOM_STEM), 3)));
   public static final ConfiguredFeature<?, ?> HUGE_RED_MUSHROOM = register("huge_red_mushroom", Feature.HUGE_RED_MUSHROOM.configured(new BigMushroomFeatureConfig(new SimpleBlockStateProvider(Features.States.HUGE_RED_MUSHROOM), new SimpleBlockStateProvider(Features.States.HUGE_MUSHROOM_STEM), 2)));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK = register("oak", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.OAK_LOG), new SimpleBlockStateProvider(Features.States.OAK_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> REALISTIC_OAK = register("realistic_oak",
           Feature.TREE.configured(
                   new BaseTreeFeatureConfig.Builder(
                           new SimpleBlockStateProvider(Features.States.OAK_LOG),
                           new SimpleBlockStateProvider(Features.States.OAK_LEAVES),
                           new BlobFoliagePlacer(FeatureSpread.fixed(3), FeatureSpread.fixed(1), 4),
                           new StraightTrunkPlacer(6, 3, 1),
                           new TwoLayerFeature(2, 0, 2))
                           .ignoreVines()
                           .build()
           )
   );

   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> DARK_OAK = register("dark_oak", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.DARK_OAK_LOG), new SimpleBlockStateProvider(Features.States.DARK_OAK_LEAVES), new DarkOakFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0)), new DarkOakTrunkPlacer(6, 2, 1), new ThreeLayerFeature(1, 1, 0, 1, 2, OptionalInt.empty()))).maxWaterDepth(Integer.MAX_VALUE).heightmap(Heightmap.Type.MOTION_BLOCKING).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> PALE_OAK = register("pale_oak",
           Feature.TREE.configured(
                   (new BaseTreeFeatureConfig.Builder(
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LOG.defaultBlockState()),  // Log block
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LEAVES.defaultBlockState()),  // Leaves block
                           new DarkOakFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0)),  // Foliage placer
                           new DarkOakTrunkPlacer(6, 2, 1),  // Trunk placer
                           new ThreeLayerFeature(1, 1, 0, 1, 2, OptionalInt.empty()))  // Feature size
                   )
                           .decorators(ImmutableList.of(
                                   new PaleMossDecorator(0.15f, 0.4F),  // The existing PaleMossDecorator
                                   new PaleOakTreeGroundDecorator()  // Replaces ground with pale moss blocks
                           ))
                           .ignoreVines()
                           .build()
           )
   );
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> TALL_PALE_OAK = register("tall_pale_oak",
           Feature.TREE.configured(
                   (new BaseTreeFeatureConfig.Builder(
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LOG.defaultBlockState()),  // Log block
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LEAVES.defaultBlockState()),  // Leaves block
                           new DarkOakFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0)),  // Foliage placer
                           new DarkOakTrunkPlacer(8, 2, 1),  // Trunk placer (increased height from 6 to 11)
                           new ThreeLayerFeature(2, 1, 1, 1, 3, OptionalInt.empty()))  // Feature size
                   )
                           .decorators(ImmutableList.of(
                                   new PaleMossDecorator(0.9f, 0.95F),  // The existing PaleMossDecorator
                                   new PaleOakTreeGroundDecorator()  // Replaces ground with pale moss blocks
                           ))
                           .ignoreVines()
                           .build()
           )
   );

   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> HUGE_PALE_OAK = register("huge_pale_oak",
           Feature.TREE.configured(
                   (new BaseTreeFeatureConfig.Builder(
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LOG.defaultBlockState()),  // Log block
                           new SimpleBlockStateProvider(Blocks.PALE_OAK_LEAVES.defaultBlockState()),  // Leaves block
                           new DarkOakFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0)),  // Foliage placer
                           new PaleOakLargeTrunkPlacer(7, 2, 2),  // Trunk placer (increased height from 6 to 11)
                           new ThreeLayerFeature(4, 2, 3, 3, 5, OptionalInt.empty())  // Feature size
                   )
                           .decorators(ImmutableList.of(
                                   new PaleMossDecorator(0.9f, 0.95F, 0.8F),  // The existing PaleMossDecorator
                                   new PaleOakTreeGroundDecorator(8, 12)  // Replaces ground with pale moss blocks
                           ))
                           .ignoreVines()
                           .build()
           )
   ));
   public static final ConfiguredFeature<?, ?> PALE_MOSS_VEGETATION = register("pale_moss_vegetation",
           Feature.RANDOM_PATCH.configured(
                   (new BlockClusterFeatureConfig.Builder(
                           new WeightedBlockStateProvider()
                                   .add(Blocks.PALE_MOSS_CARPET.defaultBlockState(), 25)
                                   .add(Blocks.GRASS.defaultBlockState(), 50)
                                   .add(Blocks.TALL_GRASS.defaultBlockState(), 10),
                           SimpleBlockPlacer.INSTANCE)
                           .tries(64)  // number of block placement attempts
                           .build()
                   )
           ));

   public static final ConfiguredFeature<?, ?> PALE_MOSS_GROUND_REPLACEMENT = register("pale_moss_ground_replacement",
           Feature.REPLACE_BLOBS.configured(
                           new BlobReplacementConfig(
                                   Blocks.GRASS_BLOCK.defaultBlockState(),  // The block to be replaced
                                   Blocks.PALE_MOSS_BLOCK.defaultBlockState(),  // The block replacing it
                                   FeatureSpread.of(2, 4)  // The size of the replacement blob (controls the range)
                           )
                   ).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(82, 0, 95)))  // Limit to Y 82-95
                   .squared().count(1)  // The number of blobs to place per chunk
   );


   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH = register("birch", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.BIRCH_LOG), new SimpleBlockStateProvider(Features.States.BIRCH_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(5, 2, 0), new TwoLayerFeature(1, 0, 1))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> ACACIA = register("acacia", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.ACACIA_LOG), new SimpleBlockStateProvider(Features.States.ACACIA_LEAVES), new AcaciaFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0)), new ForkyTrunkPlacer(5, 2, 2), new TwoLayerFeature(1, 0, 2))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> SPRUCE = register("spruce", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SPRUCE_LOG), new SimpleBlockStateProvider(Features.States.SPRUCE_LEAVES), new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(1, 1)), new StraightTrunkPlacer(5, 2, 1), new TwoLayerFeature(2, 0, 2))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> PINE = register("pine", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SPRUCE_LOG), new SimpleBlockStateProvider(Features.States.SPRUCE_LEAVES), new PineFoliagePlacer(FeatureSpread.fixed(1), FeatureSpread.fixed(1), FeatureSpread.of(3, 1)), new StraightTrunkPlacer(6, 4, 0), new TwoLayerFeature(2, 0, 2))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> JUNGLE_TREE = register("jungle_tree", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.JUNGLE_LOG), new SimpleBlockStateProvider(Features.States.JUNGLE_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(4, 8, 0), new TwoLayerFeature(1, 0, 1))).decorators(ImmutableList.of(new CocoaTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, LeaveVineTreeDecorator.INSTANCE)).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK = register("fancy_oak", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.OAK_LOG), new SimpleBlockStateProvider(Features.States.OAK_LEAVES), new FancyFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(4), 4), new FancyTrunkPlacer(3, 11, 0), new TwoLayerFeature(0, 0, 0, OptionalInt.of(4)))).ignoreVines().heightmap(Heightmap.Type.MOTION_BLOCKING).build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> JUNGLE_TREE_NO_VINE = register("jungle_tree_no_vine", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.JUNGLE_LOG), new SimpleBlockStateProvider(Features.States.JUNGLE_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(4, 8, 0), new TwoLayerFeature(1, 0, 1))).ignoreVines().build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> MEGA_JUNGLE_TREE = register("mega_jungle_tree", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.JUNGLE_LOG), new SimpleBlockStateProvider(Features.States.JUNGLE_LEAVES), new JungleFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 2), new MegaJungleTrunkPlacer(10, 2, 19), new TwoLayerFeature(1, 1, 2))).decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, LeaveVineTreeDecorator.INSTANCE)).build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> MEGA_SPRUCE = register("mega_spruce", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SPRUCE_LOG), new SimpleBlockStateProvider(Features.States.SPRUCE_LEAVES), new MegaPineFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), FeatureSpread.of(13, 4)), new GiantTrunkPlacer(13, 2, 14), new TwoLayerFeature(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(Features.States.PODZOL)))).build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> MEGA_PINE = register("mega_pine", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SPRUCE_LOG), new SimpleBlockStateProvider(Features.States.SPRUCE_LEAVES), new MegaPineFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), FeatureSpread.of(3, 4)), new GiantTrunkPlacer(13, 2, 14), new TwoLayerFeature(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(Features.States.PODZOL)))).build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> SUPER_BIRCH_BEES_0002 = register("super_birch_bees_0002", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.BIRCH_LOG), new SimpleBlockStateProvider(Features.States.BIRCH_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(5, 2, 6), new TwoLayerFeature(1, 0, 1))).ignoreVines().decorators(ImmutableList.of(Features.Placements.BEEHIVE_0002)).build()));
   public static final ConfiguredFeature<?, ?> SWAMP_TREE = register("swamp_tree", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.OAK_LOG), new SimpleBlockStateProvider(Features.States.OAK_LEAVES), new BlobFoliagePlacer(FeatureSpread.fixed(3), FeatureSpread.fixed(0), 3), new StraightTrunkPlacer(5, 3, 0), new TwoLayerFeature(1, 0, 1))).maxWaterDepth(1).decorators(ImmutableList.of(LeaveVineTreeDecorator.INSTANCE)).build()).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> JUNGLE_BUSH = register("jungle_bush", Feature.TREE.configured((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.JUNGLE_LOG), new SimpleBlockStateProvider(Features.States.OAK_LEAVES), new BushFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(1), 2), new StraightTrunkPlacer(1, 0, 0), new TwoLayerFeature(0, 0, 0))).heightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).build()));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_BEES_0002 = register("oak_bees_0002", Feature.TREE.configured(OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_0002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_BEES_002 = register("oak_bees_002", Feature.TREE.configured(OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_BEES_005 = register("oak_bees_005", Feature.TREE.configured(OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_005))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_BEES_0002 = register("birch_bees_0002", Feature.TREE.configured(BIRCH.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_0002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_BEES_002 = register("birch_bees_002", Feature.TREE.configured(BIRCH.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_BEES_005 = register("birch_bees_005", Feature.TREE.configured(BIRCH.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_005))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_BEES_0002 = register("fancy_oak_bees_0002", Feature.TREE.configured(FANCY_OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_0002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_BEES_002 = register("fancy_oak_bees_002", Feature.TREE.configured(FANCY_OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_002))));
   public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_BEES_005 = register("fancy_oak_bees_005", Feature.TREE.configured(FANCY_OAK.config().withDecorators(ImmutableList.of(Features.Placements.BEEHIVE_005))));
   public static final ConfiguredFeature<?, ?> OAK_BADLANDS = register("oak_badlands", OAK.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(5, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> SPRUCE_SNOWY = register("spruce_snowy", SPRUCE.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> FLOWER_WARM = register("flower_warm", Feature.FLOWER.configured(Features.Configs.DEFAULT_FLOWER_CONFIG).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(4));
   public static final ConfiguredFeature<?, ?> FLOWER_DEFAULT = register("flower_default", Feature.FLOWER.configured(Features.Configs.DEFAULT_FLOWER_CONFIG).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(2));
   public static final ConfiguredFeature<?, ?> FLOWER_FOREST = register("flower_forest", Feature.FLOWER.configured((new BlockClusterFeatureConfig.Builder(ForestFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE)).tries(64).build()).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(100));
   public static final ConfiguredFeature<?, ?> FLOWER_SWAMP = register("flower_swamp", Feature.FLOWER.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.BLUE_ORCHID), SimpleBlockPlacer.INSTANCE)).tries(64).build()).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE));
   public static final ConfiguredFeature<?, ?> FLOWER_PLAIN = register("flower_plain", Feature.FLOWER.configured((new BlockClusterFeatureConfig.Builder(PlainFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE)).tries(64).build()));
   public static final ConfiguredFeature<?, ?> FLOWER_PLAIN_DECORATED = register("flower_plain_decorated", FLOWER_PLAIN.decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP).squared().decorated(Placement.COUNT_NOISE.configured(new NoiseDependant(-0.8D, 15, 4))));
   private static final ImmutableList<Supplier<ConfiguredFeature<?, ?>>> FOREST_FLOWER_FEATURES = ImmutableList.of(() -> {
      return Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.LILAC), new DoublePlantBlockPlacer())).tries(64).noProjection().build());
   }, () -> {
      return Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.ROSE_BUSH), new DoublePlantBlockPlacer())).tries(64).noProjection().build());
   }, () -> {
      return Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.PEONY), new DoublePlantBlockPlacer())).tries(64).noProjection().build());
   }, () -> {
      return Feature.NO_BONEMEAL_FLOWER.configured((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.LILY_OF_THE_VALLEY), SimpleBlockPlacer.INSTANCE)).tries(64).build());
   });
   private static final  ImmutableList<Supplier<ConfiguredFeature<?,?>>> EYEBLOSSOMS = ImmutableList.of(() -> {
      return Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.CLOSED_EYEBLOSSOM.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(6).build());
   });
   public static final ConfiguredFeature<?, ?> EYEBLOSSOM_FEATURE = register("eyeblossom_feature", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(EYEBLOSSOMS)).count(FeatureSpread.of(-1, 4)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(5));

   private static final  ImmutableList<Supplier<ConfiguredFeature<?,?>>> EYEBLOSSOMS_LARGE = ImmutableList.of(() -> {
      return Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.CLOSED_EYEBLOSSOM.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(8).build());
   });
   public static final ConfiguredFeature<?, ?> EYEBLOSSOM_FEATURE_LARGE = register("eyeblossom_feature_large", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(EYEBLOSSOMS_LARGE)).count(FeatureSpread.of(-1, 2)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(6));


   public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION_COMMON = register("forest_flower_vegetation_common", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(FOREST_FLOWER_FEATURES)).count(FeatureSpread.of(-1, 4)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(5));
   public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION = register("forest_flower_vegetation", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(FOREST_FLOWER_FEATURES)).count(FeatureSpread.of(-3, 4)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(5));
   public static final ConfiguredFeature<?, ?> PALE_GARDEN_VEGETATION = register("pale_garden_vegetation",
           Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(
                   ImmutableList.of(
                           PALE_OAK.weighted(0.6666667F),
                           HUGE_PALE_OAK.weighted(0.27F)),
                   PALE_OAK))
                   .decorated(Placement.PALE_OAK_TREE.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> DARK_FOREST_VEGETATION_BROWN = register("dark_forest_vegetation_brown", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(HUGE_BROWN_MUSHROOM.weighted(0.025F), HUGE_RED_MUSHROOM.weighted(0.05F), DARK_OAK.weighted(0.6666667F), PALE_OAK.weighted(0.01F), BIRCH.weighted(0.2F), FANCY_OAK.weighted(0.1F)), OAK)).decorated(Placement.DARK_OAK_TREE.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> DARK_FOREST_VEGETATION_RED = register("dark_forest_vegetation_red", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(HUGE_RED_MUSHROOM.weighted(0.025F), HUGE_BROWN_MUSHROOM.weighted(0.05F), DARK_OAK.weighted(0.6666667F), BIRCH.weighted(0.2F), PALE_OAK.weighted(0.01F), FANCY_OAK.weighted(0.1F)), OAK)).decorated(Placement.DARK_OAK_TREE.configured(IPlacementConfig.NONE)));
   public static final ConfiguredFeature<?, ?> WARM_OCEAN_VEGETATION = register("warm_ocean_vegetation", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(ImmutableList.of(() -> {
      return Feature.CORAL_TREE.configured(IFeatureConfig.NONE);
   }, () -> {
      return Feature.CORAL_CLAW.configured(IFeatureConfig.NONE);
   }, () -> {
      return Feature.CORAL_MUSHROOM.configured(IFeatureConfig.NONE);
   }))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP).squared().decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(20, 400.0D, 0.0D))));
   public static final ConfiguredFeature<?, ?> FOREST_FLOWER_TREES = register("forest_flower_trees", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(BIRCH_BEES_002.weighted(0.2F), REALISTIC_OAK.weighted(0.08F), FANCY_OAK_BEES_002.weighted(0.1F)), OAK_BEES_002)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(6, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TAIGA_VEGETATION = register("taiga_vegetation", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(PINE.weighted(0.33333334F)), SPRUCE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_SHATTERED_SAVANNA = register("trees_shattered_savanna", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(ACACIA.weighted(0.8F)), OAK)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_SAVANNA = register("trees_savanna", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(ACACIA.weighted(0.8F)), OAK)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(1, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> BIRCH_TALL = register("birch_tall", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(SUPER_BIRCH_BEES_0002.weighted(0.5F)), BIRCH_BEES_0002)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_BIRCH = register("trees_birch", BIRCH_BEES_0002.decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN_EDGE = register("trees_mountain_edge", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(SPRUCE.weighted(0.666F), FANCY_OAK.weighted(0.1F)), OAK)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN = register("trees_mountain", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(SPRUCE.weighted(0.666F), FANCY_OAK.weighted(0.1F)), OAK)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_WATER = register("trees_water", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK.weighted(0.1F)), OAK)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> BIRCH_OTHER = register("birch_other", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(BIRCH_BEES_0002.weighted(0.2F), FANCY_OAK_BEES_0002.weighted(0.1F)), OAK_BEES_0002)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> PLAIN_VEGETATION = register("plain_vegetation", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK_BEES_005.weighted(0.33333334F)), OAK_BEES_005)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_JUNGLE_EDGE = register("trees_jungle_edge", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK.weighted(0.1F), JUNGLE_BUSH.weighted(0.5F)), JUNGLE_TREE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_GIANT_SPRUCE = register("trees_giant_spruce", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.weighted(0.33333334F), PINE.weighted(0.33333334F)), SPRUCE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_GIANT = register("trees_giant", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.weighted(0.025641026F), MEGA_PINE.weighted(0.30769232F), PINE.weighted(0.33333334F)), SPRUCE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> TREES_JUNGLE = register("trees_jungle", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK.weighted(0.1F), JUNGLE_BUSH.weighted(0.5F), MEGA_JUNGLE_TREE.weighted(0.33333334F)), JUNGLE_TREE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(50, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> BAMBOO_VEGETATION = register("bamboo_vegetation", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK.weighted(0.05F), JUNGLE_BUSH.weighted(0.15F), MEGA_JUNGLE_TREE.weighted(0.7F)), Feature.RANDOM_PATCH.configured(Features.Configs.JUNGLE_GRASS_CONFIG))).decorated(Features.Placements.HEIGHTMAP_SQUARE).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(30, 0.1F, 1))));
   public static final ConfiguredFeature<?, ?> MUSHROOM_FIELD_VEGETATION = register("mushroom_field_vegetation", Feature.RANDOM_BOOLEAN_SELECTOR.configured(new TwoFeatureChoiceConfig(() -> {
      return HUGE_RED_MUSHROOM;
   }, () -> {
      return HUGE_BROWN_MUSHROOM;
   })).decorated(Features.Placements.HEIGHTMAP_SQUARE));

   private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String p_243968_0_, ConfiguredFeature<FC, ?> p_243968_1_) {
      return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, p_243968_0_, p_243968_1_);
   }

   public static final class Configs {
      public static final BlockClusterFeatureConfig DEFAULT_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.GRASS), SimpleBlockPlacer.INSTANCE)).tries(32).build();
      public static final BlockClusterFeatureConfig TAIGA_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(Features.States.GRASS, 1).add(Features.States.FERN, 4), SimpleBlockPlacer.INSTANCE)).tries(32).build();
      public static final BlockClusterFeatureConfig JUNGLE_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(Features.States.GRASS, 3).add(Features.States.FERN, 1), SimpleBlockPlacer.INSTANCE)).blacklist(ImmutableSet.of(Features.States.PODZOL)).tries(32).build();
      public static final BlockClusterFeatureConfig DEFAULT_FLOWER_CONFIG = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(Features.States.POPPY, 2).add(Features.States.DANDELION, 1), SimpleBlockPlacer.INSTANCE)).tries(64).build();
      public static final BlockClusterFeatureConfig DEAD_BUSH_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.DEAD_BUSH), SimpleBlockPlacer.INSTANCE)).tries(4).build();
      public static final BlockClusterFeatureConfig SWEET_BERRY_BUSH_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SWEET_BERRY_BUSH), SimpleBlockPlacer.INSTANCE)).tries(64).whitelist(ImmutableSet.of(Features.States.GRASS_BLOCK.getBlock())).noProjection().build();
      public static final BlockClusterFeatureConfig TALL_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.TALL_GRASS), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
      public static final BlockClusterFeatureConfig SUGAR_CANE_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Features.States.SUGAR_CANE), new ColumnBlockPlacer(2, 2))).tries(20).xspread(4).yspread(0).zspread(4).noProjection().needWater().build();
      public static final LiquidsConfig LAVA_SPRING_CONFIG = new LiquidsConfig(Features.States.LAVA_STATE, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
      public static final LiquidsConfig CLOSED_NETHER_SPRING_CONFIG = new LiquidsConfig(Features.States.LAVA_STATE, false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
      public static final BlockStateProvidingFeatureConfig CRIMSON_FOREST_CONFIG = new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(Features.States.CRIMSON_ROOTS, 87).add(Features.States.CRIMSON_FUNGUS, 11).add(Features.States.WARPED_FUNGUS, 1));
      public static final BlockStateProvidingFeatureConfig WARPED_FOREST_CONFIG = new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(Features.States.WARPED_ROOTS, 85).add(Features.States.CRIMSON_ROOTS, 1).add(Features.States.WARPED_FUNGUS, 13).add(Features.States.CRIMSON_FUNGUS, 1));
      public static final BlockStateProvidingFeatureConfig NETHER_SPROUTS_CONFIG = new BlockStateProvidingFeatureConfig(new SimpleBlockStateProvider(Features.States.NETHER_SPROUTS));
   }

   public static final class Placements {
      public static final BeehiveTreeDecorator BEEHIVE_0002 = new BeehiveTreeDecorator(0.002F);
      public static final BeehiveTreeDecorator BEEHIVE_002 = new BeehiveTreeDecorator(0.02F);
      public static final BeehiveTreeDecorator BEEHIVE_005 = new BeehiveTreeDecorator(0.05F);
      public static final ConfiguredPlacement<FeatureSpreadConfig> FIRE = Placement.FIRE.configured(new FeatureSpreadConfig(10));
      public static final ConfiguredPlacement<NoPlacementConfig> HEIGHTMAP = Placement.HEIGHTMAP.configured(IPlacementConfig.NONE);
      public static final ConfiguredPlacement<NoPlacementConfig> TOP_SOLID_HEIGHTMAP = Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE);
      public static final ConfiguredPlacement<NoPlacementConfig> HEIGHTMAP_WORLD_SURFACE = Placement.HEIGHTMAP_WORLD_SURFACE.configured(IPlacementConfig.NONE);
      public static final ConfiguredPlacement<NoPlacementConfig> HEIGHTMAP_DOUBLE = Placement.HEIGHTMAP_SPREAD_DOUBLE.configured(IPlacementConfig.NONE);
      public static final ConfiguredPlacement<TopSolidRangeConfig> RANGE_10_20_ROOFED = Placement.RANGE.configured(new TopSolidRangeConfig(10, 20, 128));
      public static final ConfiguredPlacement<TopSolidRangeConfig> RANGE_4_8_ROOFED = Placement.RANGE.configured(new TopSolidRangeConfig(4, 8, 128));
      public static final ConfiguredPlacement<?> ADD_32 = Placement.SPREAD_32_ABOVE.configured(NoPlacementConfig.INSTANCE);
      public static final ConfiguredPlacement<?> HEIGHTMAP_SQUARE = HEIGHTMAP.squared();
      public static final ConfiguredPlacement<?> HEIGHTMAP_DOUBLE_SQUARE = HEIGHTMAP_DOUBLE.squared();
      public static final ConfiguredPlacement<?> TOP_SOLID_HEIGHTMAP_SQUARE = TOP_SOLID_HEIGHTMAP.squared();
   }

   public static final class States {
      protected static final BlockState GRASS = Blocks.GRASS.defaultBlockState();
      protected static final BlockState FERN = Blocks.FERN.defaultBlockState();
      protected static final BlockState PODZOL = Blocks.PODZOL.defaultBlockState();
      protected static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
      protected static final BlockState MYCELIUM = Blocks.MYCELIUM.defaultBlockState();
      protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
      protected static final BlockState ICE = Blocks.ICE.defaultBlockState();
      protected static final BlockState OAK_LOG = Blocks.OAK_LOG.defaultBlockState();
      protected static final BlockState OAK_LEAVES = Blocks.OAK_LEAVES.defaultBlockState();
      protected static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.defaultBlockState();
      protected static final BlockState JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.defaultBlockState();
      protected static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.defaultBlockState();
      protected static final BlockState SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.defaultBlockState();
      protected static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.defaultBlockState();
      protected static final BlockState ACACIA_LEAVES = Blocks.ACACIA_LEAVES.defaultBlockState();
      protected static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.defaultBlockState();
      protected static final BlockState BIRCH_LEAVES = Blocks.BIRCH_LEAVES.defaultBlockState();
      protected static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
      protected static final BlockState PALE_OAK_LOG = Blocks.PALE_OAK_LOG.defaultBlockState();
      protected static final BlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();
      protected static final BlockState PALE_OAK_LEAVES = Blocks.PALE_OAK_LEAVES.defaultBlockState();

      protected static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
      protected static final BlockState LARGE_FERN = Blocks.LARGE_FERN.defaultBlockState();
      protected static final BlockState TALL_GRASS = Blocks.TALL_GRASS.defaultBlockState();
      protected static final BlockState LILAC = Blocks.LILAC.defaultBlockState();
      protected static final BlockState ROSE_BUSH = Blocks.ROSE_BUSH.defaultBlockState();
      protected static final BlockState PEONY = Blocks.PEONY.defaultBlockState();
      protected static final BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.defaultBlockState();
      protected static final BlockState RED_MUSHROOM = Blocks.RED_MUSHROOM.defaultBlockState();
      protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
      protected static final BlockState BLUE_ICE = Blocks.BLUE_ICE.defaultBlockState();
      protected static final BlockState LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
      protected static final BlockState BLUE_ORCHID = Blocks.BLUE_ORCHID.defaultBlockState();
      protected static final BlockState POPPY = Blocks.POPPY.defaultBlockState();
      protected static final BlockState DANDELION = Blocks.DANDELION.defaultBlockState();
      protected static final BlockState DEAD_BUSH = Blocks.DEAD_BUSH.defaultBlockState();
      protected static final BlockState MELON = Blocks.MELON.defaultBlockState();
      protected static final BlockState PUMPKIN = Blocks.PUMPKIN.defaultBlockState();
      protected static final BlockState WHITE_PUMPKIN = Blocks.WHITE_PUMPKIN.defaultBlockState();

      protected static final BlockState SWEET_BERRY_BUSH = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3));
      protected static final BlockState FIRE = Blocks.FIRE.defaultBlockState();
      protected static final BlockState SOUL_FIRE = Blocks.SOUL_FIRE.defaultBlockState();
      protected static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
      protected static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.defaultBlockState();
      protected static final BlockState CRIMSON_ROOTS = Blocks.CRIMSON_ROOTS.defaultBlockState();
      protected static final BlockState LILY_PAD = Blocks.LILY_PAD.defaultBlockState();
      protected static final BlockState SNOW = Blocks.SNOW.defaultBlockState();
      protected static final BlockState JACK_O_LANTERN = Blocks.JACK_O_LANTERN.defaultBlockState();
      protected static final BlockState SUNFLOWER = Blocks.SUNFLOWER.defaultBlockState();
      protected static final BlockState CACTUS = Blocks.CACTUS.defaultBlockState();
      protected static final BlockState SUGAR_CANE = Blocks.SUGAR_CANE.defaultBlockState();
      protected static final BlockState HUGE_RED_MUSHROOM = Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
      protected static final BlockState HUGE_BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(true)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
      protected static final BlockState HUGE_MUSHROOM_STEM = Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
      protected static final FluidState WATER_STATE = Fluids.WATER.defaultFluidState();
      protected static final FluidState LAVA_STATE = Fluids.LAVA.defaultFluidState();
      protected static final BlockState WATER = Blocks.WATER.defaultBlockState();
      protected static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
      protected static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
      protected static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
      protected static final BlockState GRANITE = Blocks.GRANITE.defaultBlockState();
      protected static final BlockState DIORITE = Blocks.DIORITE.defaultBlockState();
      protected static final BlockState ANDESITE = Blocks.ANDESITE.defaultBlockState();
      protected static final BlockState COAL_ORE = Blocks.COAL_ORE.defaultBlockState();
      protected static final BlockState IRON_ORE = Blocks.IRON_ORE.defaultBlockState();
      protected static final BlockState GOLD_ORE = Blocks.GOLD_ORE.defaultBlockState();
      protected static final BlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.defaultBlockState();
      protected static final BlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.defaultBlockState();
      protected static final BlockState LAPIS_ORE = Blocks.LAPIS_ORE.defaultBlockState();
      protected static final BlockState STONE = Blocks.STONE.defaultBlockState();
      protected static final BlockState EMERALD_ORE = Blocks.EMERALD_ORE.defaultBlockState();
      protected static final BlockState INFESTED_STONE = Blocks.INFESTED_STONE.defaultBlockState();
      protected static final BlockState SAND = Blocks.SAND.defaultBlockState();
      protected static final BlockState CLAY = Blocks.CLAY.defaultBlockState();
      protected static final BlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
      protected static final BlockState SEAGRASS = Blocks.SEAGRASS.defaultBlockState();
      protected static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.defaultBlockState();
      protected static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
      protected static final BlockState NETHER_GOLD_ORE = Blocks.NETHER_GOLD_ORE.defaultBlockState();
      protected static final BlockState NETHER_QUARTZ_ORE = Blocks.NETHER_QUARTZ_ORE.defaultBlockState();
      protected static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
      protected static final BlockState ANCIENT_DEBRIS = Blocks.ANCIENT_DEBRIS.defaultBlockState();
      protected static final BlockState BASALT = Blocks.BASALT.defaultBlockState();
      protected static final BlockState CRIMSON_FUNGUS = Blocks.CRIMSON_FUNGUS.defaultBlockState();
      protected static final BlockState WARPED_FUNGUS = Blocks.WARPED_FUNGUS.defaultBlockState();
      protected static final BlockState WARPED_ROOTS = Blocks.WARPED_ROOTS.defaultBlockState();
      protected static final BlockState NETHER_SPROUTS = Blocks.NETHER_SPROUTS.defaultBlockState();
   }
}