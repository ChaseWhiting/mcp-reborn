package net.minecraft.world.gen.feature.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.random.LegacyRandomSource;
import net.minecraft.util.random.WorldgenRandom;

public abstract class StructurePlacement {
    //public static final Codec<StructurePlacement> CODEC = Registry.STRUCTURE_PLACEMENT.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);
    private static final int HIGHLY_ARBITRARY_RANDOM_SALT = 10387320;
    private final Vector3i locateOffset;
    private final FrequencyReductionMethod frequencyReductionMethod;
    private final float frequency;
    private final int salt;
    //private final Optional<ExclusionZone> exclusionZone;
//
//    protected static <S extends StructurePlacement> Products.P5<RecordCodecBuilder.Mu<S>, Vector3i, FrequencyReductionMethod, Float, Integer, Optional<ExclusionZone>> placementCodec(RecordCodecBuilder.Instance<S> instance) {
//        return instance.group(Vector3i.offsetCodec(16).optionalFieldOf("locate_offset", Vector3i.ZERO).forGetter(StructurePlacement::locateOffset), FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::frequencyReductionMethod), Codec.floatRange((float)0.0f, (float)1.0f).optionalFieldOf("frequency", Float.valueOf(1.0f)).forGetter(StructurePlacement::frequency), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::salt), ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::exclusionZone));
//    }

    protected StructurePlacement(Vector3i vec3i, FrequencyReductionMethod frequencyReductionMethod, float f, int n/*, Optional<ExclusionZone> optional*/) {
        this.locateOffset = vec3i;
        this.frequencyReductionMethod = frequencyReductionMethod;
        this.frequency = f;
        this.salt = n;
        //this.exclusionZone = optional;
    }

    protected Vector3i locateOffset() {
        return this.locateOffset;
    }

    protected FrequencyReductionMethod frequencyReductionMethod() {
        return this.frequencyReductionMethod;
    }

    protected float frequency() {
        return this.frequency;
    }

    protected int salt() {
        return this.salt;
    }

//    protected Optional<ExclusionZone> exclusionZone() {
//        return this.exclusionZone;
//    }
//
//    public boolean isStructureChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int n, int n2) {
//        if (!this.isPlacementChunk(chunkGeneratorStructureState, n, n2)) {
//            return false;
//        }
//        if (this.frequency < 1.0f && !this.frequencyReductionMethod.shouldGenerate(chunkGeneratorStructureState.getLevelSeed(), this.salt, n, n2, this.frequency)) {
//            return false;
//        }
//        return !this.exclusionZone.isPresent() || !this.exclusionZone.get().isPlacementForbidden(chunkGeneratorStructureState, n, n2);
//    }

    //protected abstract boolean isPlacementChunk(ChunkGeneratorStructureState var1, int var2, int var3);

    public BlockPos getLocatePos(ChunkPos chunkPos) {
        return new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ()).offset(this.locateOffset());
    }

    public abstract StructurePlacementType<?> type();

    private static boolean probabilityReducer(long l, int n, int n2, int n3, float f) {
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureWithSalt(l, n, n2, n3);
        return worldgenRandom.nextFloat() < f;
    }

    private static boolean legacyProbabilityReducerWithDouble(long l, int n, int n2, int n3, float f) {
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureSeed(l, n2, n3);
        return worldgenRandom.nextDouble() < (double)f;
    }

    private static boolean legacyArbitrarySaltProbabilityReducer(long l, int n, int n2, int n3, float f) {
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureWithSalt(l, n2, n3, 10387320);
        return worldgenRandom.nextFloat() < f;
    }

    private static boolean legacyPillagerOutpostReducer(long l, int n, int n2, int n3, float f) {
        int n4 = n2 >> 4;
        int n5 = n3 >> 4;
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setSeed((long)(n4 ^ n5 << 4) ^ l);
        worldgenRandom.nextInt();
        return worldgenRandom.nextInt((int)(1.0f / f)) == 0;
    }

    public static enum FrequencyReductionMethod implements IStringSerializable
    {
        DEFAULT("default", StructurePlacement::probabilityReducer),
        LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyPillagerOutpostReducer),
        LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyArbitrarySaltProbabilityReducer),
        LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyProbabilityReducerWithDouble);

        public static final Codec<FrequencyReductionMethod> CODEC;
        private final String name;
        private final FrequencyReducer reducer;

        private FrequencyReductionMethod(String string2, FrequencyReducer frequencyReducer) {
            this.name = string2;
            this.reducer = frequencyReducer;
        }

        public boolean shouldGenerate(long l, int n, int n2, int n3, float f) {
            return this.reducer.shouldGenerate(l, n, n2, n3, f);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = IStringSerializable.fromEnum(FrequencyReductionMethod::values);
        }
    }

//    @Deprecated
//    public record ExclusionZone(Holder<StructureSet> otherSet, int chunkCount) {
//        public static final Codec<ExclusionZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryKeyCodec.create(Registry.STRUCTURE_SET, StructureSet.DIRECT_CODEC, false).fieldOf("other_set").forGetter(ExclusionZone::otherSet), Codec.intRange((int)1, (int)16).fieldOf("chunk_count").forGetter(ExclusionZone::chunkCount)).apply(instance, ExclusionZone::new));
//
//        boolean isPlacementForbidden(ChunkGeneratorStructureState chunkGeneratorStructureState, int n, int n2) {
//            return chunkGeneratorStructureState.hasStructureChunkInRange(this.otherSet, n, n2, this.chunkCount);
//        }
//    }

    @FunctionalInterface
    public static interface FrequencyReducer {
        public boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6);
    }
}
