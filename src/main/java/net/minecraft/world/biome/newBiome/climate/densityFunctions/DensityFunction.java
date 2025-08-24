package net.minecraft.world.biome.newBiome.climate.densityFunctions;

import com.mojang.serialization.Codec;
import net.minecraft.world.biome.newBiome.noise.NormalNoise;

import javax.annotation.Nullable;

public interface DensityFunction {
    public static final Codec<DensityFunction> DIRECT_CODEC = DensityFunctions.DIRECT_CODEC;
//    public static final Codec<DensityFunction> CODEC = RegistryKeyCodec
//            .create(Registry.DENSITY_FUNCTION_REGISTRY, DIRECT_CODEC)
//            .xmap(Supplier::get, df -> () -> df);




//    public static final Codec<DensityFunction> HOLDER_HELPER_CODEC = CODEC.xmap(DensityFunctions.HolderHolder::new, densityFunction -> {
//        if (densityFunction instanceof DensityFunctions.HolderHolder) {
//            DensityFunctions.HolderHolder holderHolder = (DensityFunctions.HolderHolder)densityFunction;
//            return holderHolder.function();
//        }
//        return densityFunction;
//    });

    public double compute(FunctionContext var1);

    public void fillArray(double[] var1, ContextProvider var2);

    public DensityFunction mapAll(Visitor var1);

    public double minValue();

    public double maxValue();

    public KeyDispatchDataCodec<? extends DensityFunction> codec();

    default public DensityFunction clamp(double d, double d2) {
        return new DensityFunctions.Clamp(this, d, d2);
    }

    default public DensityFunction abs() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.ABS);
    }

    default public DensityFunction square() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.SQUARE);
    }

    default public DensityFunction cube() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.CUBE);
    }

    default public DensityFunction halfNegative() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.HALF_NEGATIVE);
    }

    default public DensityFunction quarterNegative() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.QUARTER_NEGATIVE);
    }

    default public DensityFunction squeeze() {
        return DensityFunctions.map(this, DensityFunctions.Mapped.Type.SQUEEZE);
    }

    public record SinglePointContext(int blockX, int blockY, int blockZ) implements FunctionContext
    {
    }

    public static interface FunctionContext {
        public int blockX();

        public int blockY();

        public int blockZ();

//        default public Blender getBlender() {
//            return Blender.empty();
//        }
    }

    public static interface SimpleFunction
    extends DensityFunction {
        @Override
        default public void fillArray(double[] dArray, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(dArray, this);
        }

        @Override
        default public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(this);
        }
    }

    public static interface Visitor {
        public DensityFunction apply(DensityFunction var1);

        default public NoiseHolder visitNoise(NoiseHolder noiseHolder) {
            return noiseHolder;
        }
    }

    public record NoiseHolder(NormalNoise.NoiseParameters noiseData, @Nullable NormalNoise noise) {
//        public static final Codec<NoiseHolder> CODEC = NormalNoise.NoiseParameters.CODEC.xmap(holder -> new NoiseHolder((NormalNoise.NoiseParameters)holder, null), NoiseHolder::noiseData);

        public NoiseHolder(NormalNoise.NoiseParameters holder) {
            this(holder, null);
        }

        public double getValue(double d, double d2, double d3) {
            return this.noise == null ? 0.0 : this.noise.getValue(d, d2, d3);
        }

        public double maxValue() {
            return this.noise == null ? 2.0 : this.noise.maxValue();
        }
    }

    public static interface ContextProvider {
        public FunctionContext forIndex(int var1);

        public void fillAllDirectly(double[] var1, DensityFunction var2);
    }
}

