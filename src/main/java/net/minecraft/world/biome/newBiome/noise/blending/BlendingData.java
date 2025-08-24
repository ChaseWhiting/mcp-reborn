package net.minecraft.world.biome.newBiome.noise.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.SectionPos;

import java.util.*;


//TODO FIX LITERALLY EVERYTHING
public class BlendingData {



    private static final double BLENDING_DENSITY_FACTOR = 0.1;
    protected static final int CELL_WIDTH = 4;
    protected static final int CELL_HEIGHT = 8;
    protected static final int CELL_RATIO = 2;
    private static final double SOLID_DENSITY = 1.0;
    private static final double AIR_DENSITY = -1.0;
    private static final int CELLS_PER_SECTION_Y = 2;
    private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
    private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
    private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
    private static final int CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
    private static final int CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
    private static final int CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
    //private final LevelHeightAccessor areaWithOldGeneration;
    private static final List<Block> SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
    protected static final double NO_VALUE = Double.MAX_VALUE;
    private boolean hasCalculatedData;
    private final double[] heights;
    //private final List<List<Holder<Biome>>> biomes;
    private final transient double[][] densities;
    private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
    //public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("min_section").forGetter(blendingData -> blendingData.areaWithOldGeneration.getMinSection()), (App)Codec.INT.fieldOf("max_section").forGetter(blendingData -> blendingData.areaWithOldGeneration.getMaxSection()), (App)DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter(blendingData -> DoubleStream.of(blendingData.heights).anyMatch(d -> d != Double.MAX_VALUE) ? Optional.of(blendingData.heights) : Optional.empty())).apply((Applicative)instance, BlendingData::new)).comapFlatMap(BlendingData::validateArraySize, Function.identity());

    private static DataResult<BlendingData> validateArraySize(BlendingData blendingData) {
        if (blendingData.heights.length != CELL_COLUMN_COUNT) {
            return DataResult.error("heights has to be of length " + CELL_COLUMN_COUNT);
        }
        return DataResult.success(blendingData);
    }

    private BlendingData(int n, int n2, Optional<double[]> optional) {
        this.heights = optional.orElse(Util.make(new double[CELL_COLUMN_COUNT], dArray -> Arrays.fill(dArray, Double.MAX_VALUE)));
        this.densities = new double[CELL_COLUMN_COUNT][];
        ObjectArrayList objectArrayList = new ObjectArrayList(CELL_COLUMN_COUNT);
        objectArrayList.size(CELL_COLUMN_COUNT);
        //this.biomes = objectArrayList;
        int n3 = SectionPos.sectionToBlockCoord(n);
        int n4 = SectionPos.sectionToBlockCoord(n2) - n3;
        //this.areaWithOldGeneration = LevelHeightAccessor.create(n3, n4);
    }
//
//    @Nullable
//    public static BlendingData getOrUpdateBlendingData(WorldGenRegion worldGenRegion, int n, int n2) {
//        //ChunkAccess chunkAccess = worldGenRegion.getChunk(n, n2);
//        //BlendingData blendingData = chunkAccess.getBlendingData();
//        if (blendingData == null || !chunkAccess.getHighestGeneratedStatus().isOrAfter(ChunkStatus.BIOMES)) {
//            return null;
//        }
//        blendingData.calculateData(chunkAccess, BlendingData.sideByGenerationAge(worldGenRegion, n, n2, false));
//        return blendingData;
//    }
//
//    //
//
//    public static Set<Direction8> sideByGenerationAge(WorldGenRegion worldGenLevel, int n, int n2, boolean bl) {
//        EnumSet<Direction8> enumSet = EnumSet.noneOf(Direction8.class);
//        for (Direction8 direction8 : Direction8.values()) {
//            int n3;
//            int n4 = n + direction8.getStepX();
//            //if (worldGenLevel.getChunk(n4, n3 = n2 + direction8.getStepZ()).isOldNoiseGeneration() != bl) continue;
//            enumSet.add(direction8);
//        }
//        return enumSet;
//    }
//
//    private void calculateData(ChunkAccess chunkAccess, Set<Direction8> set) {
//        int n;
//        if (this.hasCalculatedData) {
//            return;
//        }
//        if (set.contains((Object) Direction8.NORTH) || set.contains((Object)Direction8.WEST) || set.contains((Object)Direction8.NORTH_WEST)) {
//            this.addValuesForColumn(BlendingData.getInsideIndex(0, 0), chunkAccess, 0, 0);
//        }
//        if (set.contains((Object)Direction8.NORTH)) {
//            for (n = 1; n < QUARTS_PER_SECTION; ++n) {
//                this.addValuesForColumn(BlendingData.getInsideIndex(n, 0), chunkAccess, 4 * n, 0);
//            }
//        }
//        if (set.contains((Object)Direction8.WEST)) {
//            for (n = 1; n < QUARTS_PER_SECTION; ++n) {
//                this.addValuesForColumn(BlendingData.getInsideIndex(0, n), chunkAccess, 0, 4 * n);
//            }
//        }
//        if (set.contains((Object)Direction8.EAST)) {
//            for (n = 1; n < QUARTS_PER_SECTION; ++n) {
//                this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, n), chunkAccess, 15, 4 * n);
//            }
//        }
//        if (set.contains((Object)Direction8.SOUTH)) {
//            for (n = 0; n < QUARTS_PER_SECTION; ++n) {
//                this.addValuesForColumn(BlendingData.getOutsideIndex(n, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), chunkAccess, 4 * n, 15);
//            }
//        }
//        if (set.contains((Object)Direction8.EAST) && set.contains((Object)Direction8.NORTH_EAST)) {
//            this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), chunkAccess, 15, 0);
//        }
//        if (set.contains((Object)Direction8.EAST) && set.contains((Object)Direction8.SOUTH) && set.contains((Object)Direction8.SOUTH_EAST)) {
//            this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), chunkAccess, 15, 15);
//        }
//        this.hasCalculatedData = true;
//    }
//
//    private void addValuesForColumn(int n, ChunkAccess chunkAccess, int n2, int n3) {
//        if (this.heights[n] == Double.MAX_VALUE) {
//            this.heights[n] = this.getHeightAtXZ(chunkAccess, n2, n3);
//        }
//        this.densities[n] = this.getDensityColumn(chunkAccess, n2, n3, Mth.floor(this.heights[n]));
//        this.biomes.set(n, this.getBiomeColumn(chunkAccess, n2, n3));
//    }
//
//    private int getHeightAtXZ(ChunkAccess chunkAccess, int n, int n2) {
//        int n3 = chunkAccess.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG) ? Math.min(chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, n, n2) + 1, this.areaWithOldGeneration.getMaxBuildHeight()) : this.areaWithOldGeneration.getMaxBuildHeight();
//        int n4 = this.areaWithOldGeneration.getMinBuildHeight();
//        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n, n3, n2);
//        while (mutableBlockPos.getY() > n4) {
//            mutableBlockPos.move(Direction.DOWN);
//            if (!SURFACE_BLOCKS.contains(chunkAccess.getBlockState(mutableBlockPos).getBlock())) continue;
//            return mutableBlockPos.getY();
//        }
//        return n4;
//    }
//
//    private static double read1(ChunkAccess chunkAccess, BlockPos.MutableBlockPos mutableBlockPos) {
//        return BlendingData.isGround(chunkAccess, mutableBlockPos.move(Direction.DOWN)) ? 1.0 : -1.0;
//    }
//
//    private static double read7(ChunkAccess chunkAccess, BlockPos.MutableBlockPos mutableBlockPos) {
//        double d = 0.0;
//        for (int i = 0; i < 7; ++i) {
//            d += BlendingData.read1(chunkAccess, mutableBlockPos);
//        }
//        return d;
//    }
//
//    private double[] getDensityColumn(ChunkAccess chunkAccess, int n, int n2, int n3) {
//        double d;
//        double d2;
//        int n4;
//        double[] dArray = new double[this.cellCountPerColumn()];
//        Arrays.fill(dArray, -1.0);
//        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n, this.areaWithOldGeneration.getMaxBuildHeight(), n2);
//        double d3 = BlendingData.read7(chunkAccess, mutableBlockPos);
//        for (n4 = dArray.length - 2; n4 >= 0; --n4) {
//            d2 = BlendingData.read1(chunkAccess, mutableBlockPos);
//            d = BlendingData.read7(chunkAccess, mutableBlockPos);
//            dArray[n4] = (d3 + d2 + d) / 15.0;
//            d3 = d;
//        }
//        n4 = this.getCellYIndex(Mth.floorDiv(n3, 8));
//        if (n4 >= 0 && n4 < dArray.length - 1) {
//            d2 = ((double)n3 + 0.5) % 8.0 / 8.0;
//            d = (1.0 - d2) / d2;
//            double d4 = Math.max(d, 1.0) * 0.25;
//            dArray[n4 + 1] = -d / d4;
//            dArray[n4] = 1.0 / d4;
//        }
//        return dArray;
//    }
//
//    private List<Holder<Biome>> getBiomeColumn(ChunkAccess chunkAccess, int n, int n2) {
//        ObjectArrayList objectArrayList = new ObjectArrayList(this.quartCountPerColumn());
//        objectArrayList.size(this.quartCountPerColumn());
//        for (int i = 0; i < objectArrayList.size(); ++i) {
//            int n3 = i + QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
//            objectArrayList.set(i, chunkAccess.getNoiseBiome(QuartPos.fromBlock(n), n3, QuartPos.fromBlock(n2)));
//        }
//        return objectArrayList;
//    }
//
//    private static boolean isGround(ChunkAccess chunkAccess, BlockPos blockPos) {
//        BlockState blockState = chunkAccess.getBlockState(blockPos);
//        if (blockState.isAir()) {
//            return false;
//        }
//        if (blockState.is(BlockTags.LEAVES)) {
//            return false;
//        }
//        if (blockState.is(BlockTags.LOGS)) {
//            return false;
//        }
//        if (blockState.is(Blocks.BROWN_MUSHROOM_BLOCK) || blockState.is(Blocks.RED_MUSHROOM_BLOCK)) {
//            return false;
//        }
//        return !blockState.getCollisionShape(chunkAccess, blockPos).isEmpty();
//    }
//
//    protected double getHeight(int n, int n2, int n3) {
//        if (n == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || n3 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
//            return this.heights[BlendingData.getOutsideIndex(n, n3)];
//        }
//        if (n == 0 || n3 == 0) {
//            return this.heights[BlendingData.getInsideIndex(n, n3)];
//        }
//        return Double.MAX_VALUE;
//    }
//
//    private double getDensity(@Nullable double[] dArray, int n) {
//        if (dArray == null) {
//            return Double.MAX_VALUE;
//        }
//        int n2 = this.getCellYIndex(n);
//        if (n2 < 0 || n2 >= dArray.length) {
//            return Double.MAX_VALUE;
//        }
//        return dArray[n2] * 0.1;
//    }
//
//    protected double getDensity(int n, int n2, int n3) {
//        if (n2 == this.getMinY()) {
//            return 0.1;
//        }
//        if (n == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || n3 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
//            return this.getDensity(this.densities[BlendingData.getOutsideIndex(n, n3)], n2);
//        }
//        if (n == 0 || n3 == 0) {
//            return this.getDensity(this.densities[BlendingData.getInsideIndex(n, n3)], n2);
//        }
//        return Double.MAX_VALUE;
//    }
//
//    protected void iterateBiomes(int n, int n2, int n3, BiomeConsumer biomeConsumer) {
//        if (n2 < QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight()) || n2 >= QuartPos.fromBlock(this.areaWithOldGeneration.getMaxBuildHeight())) {
//            return;
//        }
//        int n4 = n2 - QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
//        for (int i = 0; i < this.biomes.size(); ++i) {
//            Holder<Biome> holder;
//            if (this.biomes.get(i) == null || (holder = this.biomes.get(i).get(n4)) == null) continue;
//            biomeConsumer.consume(n + BlendingData.getX(i), n3 + BlendingData.getZ(i), holder);
//        }
//    }
//
//    protected void iterateHeights(int n, int n2, HeightConsumer heightConsumer) {
//        for (int i = 0; i < this.heights.length; ++i) {
//            double d = this.heights[i];
//            if (d == Double.MAX_VALUE) continue;
//            heightConsumer.consume(n + BlendingData.getX(i), n2 + BlendingData.getZ(i), d);
//        }
//    }
//
//    protected void iterateDensities(int n, int n2, int n3, int n4, DensityConsumer densityConsumer) {
//        int n5 = this.getColumnMinY();
//        int n6 = Math.max(0, n3 - n5);
//        int n7 = Math.min(this.cellCountPerColumn(), n4 - n5);
//        for (int i = 0; i < this.densities.length; ++i) {
//            double[] dArray = this.densities[i];
//            if (dArray == null) continue;
//            int n8 = n + BlendingData.getX(i);
//            int n9 = n2 + BlendingData.getZ(i);
//            for (int j = n6; j < n7; ++j) {
//                densityConsumer.consume(n8, j + n5, n9, dArray[j] * 0.1);
//            }
//        }
//    }
//
//    private int cellCountPerColumn() {
//        return this.areaWithOldGeneration.getSectionsCount() * 2;
//    }
//
//    private int quartCountPerColumn() {
//        return QuartPos.fromSection(this.areaWithOldGeneration.getSectionsCount());
//    }
//
//    private int getColumnMinY() {
//        return this.getMinY() + 1;
//    }
//
//    private int getMinY() {
//        return this.areaWithOldGeneration.getMinSection() * 2;
//    }
//
//    private int getCellYIndex(int n) {
//        return n - this.getColumnMinY();
//    }
//
//    private static int getInsideIndex(int n, int n2) {
//        return CELL_HORIZONTAL_MAX_INDEX_INSIDE - n + n2;
//    }
//
//    private static int getOutsideIndex(int n, int n2) {
//        return CELL_COLUMN_INSIDE_COUNT + n + CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - n2;
//    }
//
//    private static int getX(int n) {
//        if (n < CELL_COLUMN_INSIDE_COUNT) {
//            return BlendingData.zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_INSIDE - n);
//        }
//        int n2 = n - CELL_COLUMN_INSIDE_COUNT;
//        return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - BlendingData.zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - n2);
//    }
//
//    private static int getZ(int n) {
//        if (n < CELL_COLUMN_INSIDE_COUNT) {
//            return BlendingData.zeroIfNegative(n - CELL_HORIZONTAL_MAX_INDEX_INSIDE);
//        }
//        int n2 = n - CELL_COLUMN_INSIDE_COUNT;
//        return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - BlendingData.zeroIfNegative(n2 - CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
//    }
//
//    private static int zeroIfNegative(int n) {
//        return n & ~(n >> 31);
//    }
//
//    public LevelHeightAccessor getAreaWithOldGeneration() {
//        return this.areaWithOldGeneration;
//    }
//
//    protected static interface BiomeConsumer {
//        public void consume(int var1, int var2, Holder<Biome> var3);
//    }
//
//    protected static interface HeightConsumer {
//        public void consume(int var1, int var2, double var3);
//    }
//
//    protected static interface DensityConsumer {
//        public void consume(int var1, int var2, int var3, double var4);
//    }
}

