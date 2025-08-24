package net.minecraft.world.biome.newBiome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface CubicSpline<C, I extends ToFloatFunction<C>>
extends ToFloatFunction<C> {
    //@VisibleForDebug
    public String parityString();

    public CubicSpline<C, I> mapAll(CoordinateVisitor<I> var1);

    public static <C, I extends ToFloatFunction<C>> Codec<CubicSpline<C, I>> codec(Codec<I> codec) {
        // Define the Point record with generics
        record Point<C, I extends ToFloatFunction<C>>(float location, CubicSpline<C, I> value, float derivative) {}

        MutableObject<Codec<CubicSpline<C, I>>> mutableObject = new MutableObject<>();

        // Codec for Point
        Codec<Point<C, I>> pointCodec = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("location").forGetter(Point::location),
                ExtraCodecs.lazyInitializedCodec(mutableObject::getValue).fieldOf("value").forGetter(Point::value),
                Codec.FLOAT.fieldOf("derivative").forGetter(Point::derivative)
        ).apply(instance, Point::new));

        // Codec for Multipoint
        Codec<CubicSpline.Multipoint<C, I>> multipointCodec = RecordCodecBuilder.create(instance -> instance.group(
                codec.fieldOf("coordinate").forGetter(Multipoint -> Multipoint.coordinate),
                ExtraCodecs.nonEmptyList(pointCodec.listOf()).fieldOf("points").forGetter(multipoint ->
                        IntStream.range(0, multipoint.locations().length)
                                .mapToObj(i -> new Point<>(
                                        multipoint.locations()[i],
                                        multipoint.values().get(i),
                                        multipoint.derivatives()[i]))
                                .toList())
        ).apply(instance, (coordinate, points) -> {
            float[] locations = new float[points.size()];
            ImmutableList.Builder<CubicSpline<C, I>> valuesBuilder = ImmutableList.builder();
            float[] derivatives = new float[points.size()];

            for (int i = 0; i < points.size(); ++i) {
                Point<C, I> point = points.get(i);
                locations[i] = point.location();
                valuesBuilder.add(point.value());
                derivatives[i] = point.derivative();
            }

            return Multipoint.create(coordinate, locations, valuesBuilder.build(), derivatives);
        }));

        // Final codec assignment (either constant float or multipoint)
        mutableObject.setValue(Codec.either(Codec.FLOAT, multipointCodec)
                .xmap(either -> either.map(Constant::new, v -> v),
                        spline -> {
                            if (spline instanceof Constant constant) {
                                return Either.left(constant.value());
                            } else {
                                return Either.right((Multipoint<C, I>) spline);
                            }
                        }));

        return mutableObject.getValue();
    }


    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> constant(float f) {
        return new Constant(f);
    }

    public static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I i) {
        return new Builder(i);
    }

    public static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I i, ToFloatFunction<Float> toFloatFunction) {
        return new Builder(i, toFloatFunction);
    }

    //@VisibleForDebug
    public record Constant<C, I extends ToFloatFunction<C>>(float value) implements CubicSpline<C, I>
    {
        @Override
        public float apply(C c) {
            return this.value;
        }

        @Override
        public String parityString() {
            return String.format(Locale.ROOT, "k=%.3f", Float.valueOf(this.value));
        }

        @Override
        public float minValue() {
            return this.value;
        }

        @Override
        public float maxValue() {
            return this.value;
        }

        @Override
        public CubicSpline<C, I> mapAll(CoordinateVisitor<I> coordinateVisitor) {
            return this;
        }
    }

    public static final class Builder<C, I extends ToFloatFunction<C>> {
        private final I coordinate;
        private final ToFloatFunction<Float> valueTransformer;
        private final FloatList locations = new FloatArrayList();
        private final List<CubicSpline<C, I>> values = Lists.newArrayList();
        private final FloatList derivatives = new FloatArrayList();

        protected Builder(I i) {
            this(i, ToFloatFunction.IDENTITY);
        }

        protected Builder(I i, ToFloatFunction<Float> toFloatFunction) {
            this.coordinate = i;
            this.valueTransformer = toFloatFunction;
        }

        public Builder<C, I> addPoint(float f, float f2) {
            return this.addPoint(f, new Constant(this.valueTransformer.apply(Float.valueOf(f2))), 0.0f);
        }

        public Builder<C, I> addPoint(float f, float f2, float f3) {
            return this.addPoint(f, new Constant(this.valueTransformer.apply(Float.valueOf(f2))), f3);
        }

        public Builder<C, I> addPoint(float f, CubicSpline<C, I> cubicSpline) {
            return this.addPoint(f, cubicSpline, 0.0f);
        }

        private Builder<C, I> addPoint(float f, CubicSpline<C, I> cubicSpline, float f2) {
            if (!this.locations.isEmpty() && f <= this.locations.getFloat(this.locations.size() - 1)) {
                throw new IllegalArgumentException("Please register points in ascending order");
            }
            this.locations.add(f);
            this.values.add(cubicSpline);
            this.derivatives.add(f2);
            return this;
        }

        public CubicSpline<C, I> build() {
            if (this.locations.isEmpty()) {
                throw new IllegalStateException("No elements added");
            }
            return Multipoint.create(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
        }
    }

    //@VisibleForDebug
    public static final class Multipoint<C, I extends ToFloatFunction<C>>
    implements CubicSpline<C, I> {
        private final I coordinate;
        final float[] locations;
        private final List<CubicSpline<C, I>> values;
        private final float[] derivatives;
        private final float minValue;
        private final float maxValue;

        public Multipoint(I i, float[] fArray, List<CubicSpline<C, I>> list, float[] fArray2, float f, float f2) {
            Multipoint.validateSizes(fArray, list, fArray2);
            this.coordinate = i;
            this.locations = fArray;
            this.values = list;
            this.derivatives = fArray2;
            this.minValue = f;
            this.maxValue = f2;
        }

        static <C, I extends ToFloatFunction<C>> Multipoint<C, I> create(I i, float[] fArray, List<CubicSpline<C, I>> list, float[] fArray2) {
            float f;
            float f2;
            Multipoint.validateSizes(fArray, list, fArray2);
            int n = fArray.length - 1;
            float f3 = Float.POSITIVE_INFINITY;
            float f4 = Float.NEGATIVE_INFINITY;
            float f5 = i.minValue();
            float f6 = i.maxValue();
            if (f5 < fArray[0]) {
                f2 = Multipoint.linearExtend(f5, fArray, list.get(0).minValue(), fArray2, 0);
                f = Multipoint.linearExtend(f5, fArray, list.get(0).maxValue(), fArray2, 0);
                f3 = Math.min(f3, Math.min(f2, f));
                f4 = Math.max(f4, Math.max(f2, f));
            }
            if (f6 > fArray[n]) {
                f2 = Multipoint.linearExtend(f6, fArray, list.get(n).minValue(), fArray2, n);
                f = Multipoint.linearExtend(f6, fArray, list.get(n).maxValue(), fArray2, n);
                f3 = Math.min(f3, Math.min(f2, f));
                f4 = Math.max(f4, Math.max(f2, f));
            }
            for (CubicSpline<C, I> cubicSpline : list) {
                f3 = Math.min(f3, cubicSpline.minValue());
                f4 = Math.max(f4, cubicSpline.maxValue());
            }
            for (int j = 0; j < n; ++j) {
                f = fArray[j];
                float f7 = fArray[j + 1];
                float f8 = f7 - f;
                CubicSpline<C, I> cubicSpline = list.get(j);
                CubicSpline<C, I> cubicSpline2 = list.get(j + 1);
                float f9 = cubicSpline.minValue();
                float f10 = cubicSpline.maxValue();
                float f11 = cubicSpline2.minValue();
                float f12 = cubicSpline2.maxValue();
                float f13 = fArray2[j];
                float f14 = fArray2[j + 1];
                if (f13 == 0.0f && f14 == 0.0f) continue;
                float f15 = f13 * f8;
                float f16 = f14 * f8;
                float f17 = Math.min(f9, f11);
                float f18 = Math.max(f10, f12);
                float f19 = f15 - f12 + f9;
                float f20 = f15 - f11 + f10;
                float f21 = -f16 + f11 - f10;
                float f22 = -f16 + f12 - f9;
                float f23 = Math.min(f19, f21);
                float f24 = Math.max(f20, f22);
                f3 = Math.min(f3, f17 + 0.25f * f23);
                f4 = Math.max(f4, f18 + 0.25f * f24);
            }
            return new Multipoint<C, I>(i, fArray, list, fArray2, f3, f4);
        }

        private static float linearExtend(float f, float[] fArray, float f2, float[] fArray2, int n) {
            float f3 = fArray2[n];
            if (f3 == 0.0f) {
                return f2;
            }
            return f2 + f3 * (f - fArray[n]);
        }

        private static <C, I extends ToFloatFunction<C>> void validateSizes(float[] fArray, List<CubicSpline<C, I>> list, float[] fArray2) {
            if (fArray.length != list.size() || fArray.length != fArray2.length) {
                throw new IllegalArgumentException("All lengths must be equal, got: " + fArray.length + " " + list.size() + " " + fArray2.length);
            }
            if (fArray.length == 0) {
                throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
        }

        @Override
        public float apply(C c) {
            float f = this.coordinate.apply(c);
            int n = Multipoint.findIntervalStart(this.locations, f);
            int n2 = this.locations.length - 1;
            if (n < 0) {
                return Multipoint.linearExtend(f, this.locations, this.values.get(0).apply(c), this.derivatives, 0);
            }
            if (n == n2) {
                return Multipoint.linearExtend(f, this.locations, this.values.get(n2).apply(c), this.derivatives, n2);
            }
            float f2 = this.locations[n];
            float f3 = this.locations[n + 1];
            float f4 = (f - f2) / (f3 - f2);
            ToFloatFunction toFloatFunction = this.values.get(n);
            ToFloatFunction toFloatFunction2 = this.values.get(n + 1);
            float f5 = this.derivatives[n];
            float f6 = this.derivatives[n + 1];
            float f7 = toFloatFunction.apply(c);
            float f8 = toFloatFunction2.apply(c);
            float f9 = f5 * (f3 - f2) - (f8 - f7);
            float f10 = -f6 * (f3 - f2) + (f8 - f7);
            float f11 = MathHelper.lerp(f4, f7, f8) + f4 * (1.0f - f4) * MathHelper.lerp(f4, f9, f10);
            return f11;
        }

        private static int findIntervalStart(float[] fArray, float f) {
            return MathHelper.binarySearch(0, fArray.length, n -> f < fArray[n]) - 1;
        }

        @Override
        @VisibleForTesting
        public String parityString() {
            return "Spline{coordinate=" + this.coordinate + ", locations=" + this.toString(this.locations) + ", derivatives=" + this.toString(this.derivatives) + ", values=" + this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
        }

        private String toString(float[] fArray) {
            return "[" + IntStream.range(0, fArray.length).mapToDouble(n -> fArray[n]).mapToObj(d -> String.format(Locale.ROOT, "%.3f", d)).collect(Collectors.joining(", ")) + "]";
        }

        @Override
        public CubicSpline<C, I> mapAll(CoordinateVisitor<I> coordinateVisitor) {
            return Multipoint.create(coordinateVisitor.visit(this.coordinate), this.locations, this.values().stream().map(cubicSpline -> cubicSpline.mapAll(coordinateVisitor)).toList(), this.derivatives);
        }

        public I coordinate() {
            return this.coordinate;
        }

        public float[] locations() {
            return this.locations;
        }

        public List<CubicSpline<C, I>> values() {
            return this.values;
        }

        public float[] derivatives() {
            return this.derivatives;
        }

        @Override
        public float minValue() {
            return this.minValue;
        }

        @Override
        public float maxValue() {
            return this.maxValue;
        }
    }

    public static interface CoordinateVisitor<I> {
        public I visit(I var1);
    }
}
