package net.minecraft.world.biome.newBiome.climate;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.DensityFunction;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.DensityFunctions;
import net.minecraft.world.biome.newBiome.noise.blending.QuartPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class Climate {
    private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
    private static final float QUANTIZATION_FACTOR = 10000.0f;
    @VisibleForTesting
    protected static final int PARAMETER_COUNT = 7;

    public static TargetPoint target(float f, float f2, float f3, float f4, float f5, float f6) {
        return new TargetPoint(Climate.quantizeCoord(f), Climate.quantizeCoord(f2), Climate.quantizeCoord(f3), Climate.quantizeCoord(f4), Climate.quantizeCoord(f5), Climate.quantizeCoord(f6));
    }

    public static ParameterPoint parameters(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        return new ParameterPoint(Parameter.point(f), Parameter.point(f2), Parameter.point(f3), Parameter.point(f4), Parameter.point(f5), Parameter.point(f6), Climate.quantizeCoord(f7));
    }

    public static ParameterPoint parameters(Parameter parameter, Parameter parameter2, Parameter parameter3, Parameter parameter4, Parameter parameter5, Parameter parameter6, float f) {
        return new ParameterPoint(parameter, parameter2, parameter3, parameter4, parameter5, parameter6, Climate.quantizeCoord(f));
    }

    public static long quantizeCoord(float f) {
        return (long)(f * 10000.0f);
    }

    public static float unquantizeCoord(long l) {
        return (float)l / 10000.0f;
    }

    public static Sampler empty() {
        DensityFunction densityFunction = DensityFunctions.zero();
        return new Sampler(densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, List.of());
    }

    public static BlockPos findSpawnPosition(List<ParameterPoint> list, Sampler sampler) {
        return new SpawnFinder(list, (Sampler)sampler).result.location();
    }

    public static final class TargetPoint {
        final long temperature;
        final long humidity;
        final long continentalness;
        final long erosion;
        final long depth;
        final long weirdness;

        public TargetPoint(long l, long l2, long l3, long l4, long l5, long l6) {
            this.temperature = l;
            this.humidity = l2;
            this.continentalness = l3;
            this.erosion = l4;
            this.depth = l5;
            this.weirdness = l6;
        }

        @VisibleForTesting
        private long[] toParameterArray() {
            return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
        }


        public long temperature() {
            return this.temperature;
        }

        public long humidity() {
            return this.humidity;
        }

        public long continentalness() {
            return this.continentalness;
        }

        public long erosion() {
            return this.erosion;
        }

        public long depth() {
            return this.depth;
        }

        public long weirdness() {
            return this.weirdness;
        }
    }

    public record ParameterPoint(Parameter temperature, Parameter humidity, Parameter continentalness, Parameter erosion, Parameter depth, Parameter weirdness, long offset) {
        //public static final Codec<ParameterPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Parameter.CODEC.fieldOf("temperature").forGetter(parameterPoint -> parameterPoint.temperature), (App)Parameter.CODEC.fieldOf("humidity").forGetter(parameterPoint -> parameterPoint.humidity), Parameter.CODEC.fieldOf("continentalness").forGetter(parameterPoint -> parameterPoint.continentalness), (App)Parameter.CODEC.fieldOf("erosion").forGetter(parameterPoint -> parameterPoint.erosion), (App)Parameter.CODEC.fieldOf("depth").forGetter(parameterPoint -> parameterPoint.depth), (App)Parameter.CODEC.fieldOf("weirdness").forGetter(parameterPoint -> parameterPoint.weirdness), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(parameterPoint -> parameterPoint.offset)).apply((Applicative)instance, ParameterPoint::new));

//        long fitness(TargetPoint targetPoint) {
//            return MathHelper.square(this.temperature.distance(targetPoint.temperature)) + MathHelper.square(this.humidity.distance(targetPoint.humidity)) + MathHelper.square(this.continentalness.distance(targetPoint.continentalness)) + MathHelper.square(this.erosion.distance(targetPoint.erosion)) + MathHelper.square(this.depth.distance(targetPoint.depth)) + MathHelper.square(this.weirdness.distance(targetPoint.weirdness)) + MathHelper.square(this.offset);
//        }

        protected List<Parameter> parameterSpace() {
            return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Parameter(this.offset, this.offset));
        }
    }

    public record Parameter(long min, long max) {
//        public static final Codec<Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange((float)-2.0f, (float)2.0f), "min", "max", (f, f2) -> {
//            if (f.compareTo((Float)f2) > 0) {
//                return DataResult.error(() -> "Cannon construct interval, min > max (" + f + " > " + f2 + ")");
//            }
//            return DataResult.success((Object)new Parameter(Climate.quantizeCoord(f.floatValue()), Climate.quantizeCoord(f2.floatValue())));
//        }, parameter -> Float.valueOf(Climate.unquantizeCoord(parameter.min())), parameter -> Float.valueOf(Climate.unquantizeCoord(parameter.max())));

        public static Parameter point(float f) {
            return Parameter.span(f, f);
        }

        public static Parameter span(float f, float f2) {
            if (f > f2) {
                throw new IllegalArgumentException("min > max: " + f + " " + f2);
            }
            return new Parameter(Climate.quantizeCoord(f), Climate.quantizeCoord(f2));
        }

        public static Parameter span(Parameter parameter, Parameter parameter2) {
            if (parameter.min() > parameter2.max()) {
                throw new IllegalArgumentException("min > max: " + parameter + " " + parameter2);
            }
            return new Parameter(parameter.min(), parameter2.max());
        }

        @Override
        public String toString() {
            return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
        }

        public long distance(long l) {
            long l2 = l - this.max;
            long l3 = this.min - l;
            if (l2 > 0L) {
                return l2;
            }
            return Math.max(l3, 0L);
        }

        public long distance(Parameter parameter) {
            long l = parameter.min() - this.max;
            long l2 = this.min - parameter.max();
            if (l > 0L) {
                return l;
            }
            return Math.max(l2, 0L);
        }

        public Parameter span(@Nullable Parameter parameter) {
            return parameter == null ? this : new Parameter(Math.min(this.min, parameter.min()), Math.max(this.max, parameter.max()));
        }
    }

    public record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<ParameterPoint> spawnTarget) {
        public TargetPoint sample(int n, int n2, int n3) {
            int n4 = QuartPos.toBlock(n);
            int n5 = QuartPos.toBlock(n2);
            int n6 = QuartPos.toBlock(n3);
            DensityFunction.SinglePointContext singlePointContext = new DensityFunction.SinglePointContext(n4, n5, n6);
            return Climate.target((float)this.temperature.compute(singlePointContext), (float)this.humidity.compute(singlePointContext), (float)this.continentalness.compute(singlePointContext), (float)this.erosion.compute(singlePointContext), (float)this.depth.compute(singlePointContext), (float)this.weirdness.compute(singlePointContext));
        }

        public BlockPos findSpawnPosition() {
            if (this.spawnTarget.isEmpty()) {
                return BlockPos.ZERO;
            }
            return Climate.findSpawnPosition(this.spawnTarget, this);
        }
    }

    static class SpawnFinder {
        Result result;

        SpawnFinder(List<ParameterPoint> list, Sampler sampler) {
            this.result = SpawnFinder.getSpawnPositionAndFitness(list, sampler, 0, 0);
            this.radialSearch(list, sampler, 2048.0f, 512.0f);
            this.radialSearch(list, sampler, 512.0f, 32.0f);
        }

        private void radialSearch(List<ParameterPoint> list, Sampler sampler, float f, float f2) {
            float f3 = 0.0f;
            float f4 = f2;
            BlockPos blockPos = this.result.location();
            while (f4 <= f) {
                int n;
                int n2 = blockPos.getX() + (int)(Math.sin(f3) * (double)f4);
                Result result = SpawnFinder.getSpawnPositionAndFitness(list, sampler, n2, n = blockPos.getZ() + (int)(Math.cos(f3) * (double)f4));
                if (result.fitness() < this.result.fitness()) {
                    this.result = result;
                }
                if (!((double)(f3 += f2 / f4) > Math.PI * 2)) continue;
                f3 = 0.0f;
                f4 += f2;
            }
        }

        private static Result getSpawnPositionAndFitness(List<ParameterPoint> list, Sampler sampler, int n, int n2) {
            double d = MathHelper.square(2500.0);
            int n3 = 2;
            long l = (long)((double)MathHelper.square(10000.0f) * Math.pow((double)(MathHelper.square((long)n) + MathHelper.square((long)n2)) / d, 2.0));
            TargetPoint targetPoint = sampler.sample(QuartPos.fromBlock(n), 0, QuartPos.fromBlock(n2));
            TargetPoint targetPoint2 = new TargetPoint(targetPoint.temperature(), targetPoint.humidity(), targetPoint.continentalness(), targetPoint.erosion(), 0L, targetPoint.weirdness());
            long l2 = Long.MAX_VALUE;
            for (ParameterPoint parameterPoint : list) {
                //l2 = Math.min(l2, parameterPoint.fitness(targetPoint2));
            }
            return new Result(new BlockPos(n, 0, n2), l + l2);
        }

        record Result(BlockPos location, long fitness) {
        }
    }

    public static class ParameterList<T> {
        private final List<Pair<ParameterPoint, T>> values;
        private final RTree<T> index;

//        public static <T> Codec<ParameterList<T>> codec(MapCodec<T> mapCodec) {
//            return ExtraCodecs.nonEmptyList(RecordCodecBuilder.create(instance -> instance.group((App)ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)mapCodec.forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).listOf()).xmap(ParameterList::new, ParameterList::values);
//        }

        public ParameterList(List<Pair<ParameterPoint, T>> list) {
            this.values = list;
            this.index = RTree.create(list);
        }

        public List<Pair<ParameterPoint, T>> values() {
            return this.values;
        }

        public T findValue(TargetPoint targetPoint) {
            return this.findValueIndex(targetPoint);
        }

        @VisibleForTesting
        public T findValueBruteForce(TargetPoint targetPoint) {
            Iterator<Pair<ParameterPoint, T>> iterator = this.values().iterator();
            Pair<ParameterPoint, T> pair = iterator.next();
            //long l = ((ParameterPoint)pair.getFirst()).fitness(targetPoint);
            Object object = pair.getSecond();
//            while (iterator.hasNext()) {
//                Pair<ParameterPoint, T> pair2 = iterator.next();
//                long l2 = ((ParameterPoint)pair2.getFirst()).fitness(targetPoint);
//                if (l2 >= l) continue;
//                l = l2;
//                object = pair2.getSecond();
//            }
            return (T)object;
        }

        public T findValueIndex(TargetPoint targetPoint) {
            return this.findValueIndex(targetPoint, RTree.Node::distance);
        }

        protected T findValueIndex(TargetPoint targetPoint, DistanceMetric<T> distanceMetric) {
            return this.index.search(targetPoint, distanceMetric);
        }
    }

    protected static final class RTree<T> {
        private static final int CHILDREN_PER_NODE = 6;
        private final Node<T> root;
        private final ThreadLocal<Leaf<T>> lastResult = new ThreadLocal();

        private RTree(Node<T> node) {
            this.root = node;
        }

        public static <T> RTree<T> create(List<Pair<ParameterPoint, T>> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            }
            int n = ((ParameterPoint)list.get(0).getFirst()).parameterSpace().size();
            if (n != 7) {
                throw new IllegalStateException("Expecting parameter space to be 7, got " + n);
            }
            List list2 = list.stream().map(pair -> new Leaf<Object>((ParameterPoint)pair.getFirst(), pair.getSecond())).collect(Collectors.toCollection(ArrayList::new));
            return new RTree<T>(RTree.build(n, list2));
        }

        private static <T> Node<T> build(int n, List<? extends Node<T>> list) {
            if (list.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            if (list.size() <= 6) {
                list.sort(Comparator.comparingLong(node -> {
                    long l = 0L;
                    for (int i = 0; i < n; ++i) {
                        Parameter parameter = node.parameterSpace[i];
                        l += Math.abs((parameter.min() + parameter.max()) / 2L);
                    }
                    return l;
                }));
                return new SubTree(list);
            }
            long l = Long.MAX_VALUE;
            int n2 = -1;
            List<SubTree<T>> list2 = null;
            for (int i = 0; i < n; ++i) {
                RTree.sort(list, n, i, false);
                List<SubTree<T>> list3 = RTree.bucketize(list);
                long l2 = 0L;
                for (SubTree<T> subTree2 : list3) {
                    l2 += RTree.cost(subTree2.parameterSpace);
                }
                if (l <= l2) continue;
                l = l2;
                n2 = i;
                list2 = list3;
            }
            RTree.sort(list2, n, n2, true);
            return new SubTree(list2.stream().map(subTree -> RTree.build(n, Arrays.asList(subTree.children))).collect(Collectors.toList()));
        }

        private static <T> void sort(List<? extends Node<T>> list, int n, int n2, boolean bl) {
            Comparator<Node<Node<T>>> comparator = RTree.comparator(n2, bl);
            for (int i = 1; i < n; ++i) {
                comparator = comparator.thenComparing(RTree.comparator((n2 + i) % n, bl));
            }
            //list.sort(comparator);
        }

        private static <T> Comparator<Node<T>> comparator(int n, boolean bl) {
            return Comparator.comparingLong(node -> {
                Parameter parameter = node.parameterSpace[n];
                long l = (parameter.min() + parameter.max()) / 2L;
                return bl ? Math.abs(l) : l;
            });
        }

        private static <T> List<SubTree<T>> bucketize(List<? extends Node<T>> list) {
            ArrayList arrayList = Lists.newArrayList();
            ArrayList arrayList2 = Lists.newArrayList();
            int n = (int)Math.pow(6.0, Math.floor(Math.log((double)list.size() - 0.01) / Math.log(6.0)));
            for (Node<T> node : list) {
                arrayList2.add(node);
                if (arrayList2.size() < n) continue;
                arrayList.add(new SubTree(arrayList2));
                arrayList2 = Lists.newArrayList();
            }
            if (!arrayList2.isEmpty()) {
                arrayList.add(new SubTree(arrayList2));
            }
            return arrayList;
        }

        private static long cost(Parameter[] parameterArray) {
            long l = 0L;
            for (Parameter parameter : parameterArray) {
                l += Math.abs(parameter.max() - parameter.min());
            }
            return l;
        }

        static <T> List<Parameter> buildParameterSpace(List<? extends Node<T>> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            }
            int n = 7;
            ArrayList arrayList = Lists.newArrayList();
            for (int i = 0; i < 7; ++i) {
                arrayList.add(null);
            }
            for (Node<T> node : list) {
                for (int i = 0; i < 7; ++i) {
                    arrayList.set(i, node.parameterSpace[i].span((Parameter)arrayList.get(i)));
                }
            }
            return arrayList;
        }

        public T search(TargetPoint targetPoint, DistanceMetric<T> distanceMetric) {
            long[] lArray = targetPoint.toParameterArray();
            Leaf<T> leaf = this.root.search(lArray, this.lastResult.get(), distanceMetric);
            this.lastResult.set(leaf);
            return leaf.value;
        }

        static abstract class Node<T> {
            protected final Parameter[] parameterSpace;

            protected Node(List<Parameter> list) {
                this.parameterSpace = list.toArray(new Parameter[0]);
            }

            protected abstract Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3);

            protected long distance(long[] lArray) {
                long l = 0L;
                for (int i = 0; i < 7; ++i) {
                    l += MathHelper.square(this.parameterSpace[i].distance(lArray[i]));
                }
                return l;
            }

            public String toString() {
                return Arrays.toString(this.parameterSpace);
            }
        }

        static final class SubTree<T>
        extends Node<T> {
            final Node<T>[] children;

            protected SubTree(List<? extends Node<T>> list) {
                this(RTree.buildParameterSpace(list), list);
            }

            protected SubTree(List<Parameter> list, List<? extends Node<T>> list2) {
                super(list);
                this.children = list2.toArray(new Node[0]);
            }

            @Override
            protected Leaf<T> search(long[] lArray, @Nullable Leaf<T> leaf, DistanceMetric<T> distanceMetric) {
                long l = leaf == null ? Long.MAX_VALUE : distanceMetric.distance(leaf, lArray);
                Leaf<T> leaf2 = leaf;
                for (Node<T> node : this.children) {
                    long l2;
                    long l3 = distanceMetric.distance(node, lArray);
                    if (l <= l3) continue;
                    Leaf<T> leaf3 = node.search(lArray, leaf2, distanceMetric);
                    long l4 = l2 = node == leaf3 ? l3 : distanceMetric.distance(leaf3, lArray);
                    if (l <= l2) continue;
                    l = l2;
                    leaf2 = leaf3;
                }
                return leaf2;
            }
        }

        static final class Leaf<T>
        extends Node<T> {
            final T value;

            Leaf(ParameterPoint parameterPoint, T t) {
                super(parameterPoint.parameterSpace());
                this.value = t;
            }

            @Override
            protected Leaf<T> search(long[] lArray, @Nullable Leaf<T> leaf, DistanceMetric<T> distanceMetric) {
                return this;
            }
        }
    }

    static interface DistanceMetric<T> {
        public long distance(RTree.Node<T> var1, long[] var2);
    }
}

