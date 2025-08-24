package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.util.codec.DispatchedMapCodec;
import net.minecraft.util.codec.RecursiveCodec;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class ExtraCodecs {

    private static Codec<Integer> intRangeWithMessage(int n, int n2, Function<Integer, String> function) {
        return validate(Codec.INT, n3 -> {
            if (n3.compareTo(n) >= 0 && n3.compareTo(n2) <= 0) {
                return DataResult.success(n3);
            }
            return DataResult.error(function.apply((Integer)n3));
        });
    }

    public static Codec<Integer> intRange(int n, int n2) {
        return ExtraCodecs.intRangeWithMessage(n, n2, n3 -> "Value must be within range [" + n + ";" + n2 + "]: " + n3);
    }

    public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE.flatComapMap(UnsignedBytes::toInt, n -> {
        if (n > 255) {
            return DataResult.error("Unsigned byte was too large: " + n + " > 255");
        }
        return DataResult.success(n.byteValue());
    });

    public static <A> MapCodec<A> recursiveMap(final String name, final Function<Codec<A>, MapCodec<A>> wrapped) {
        return new RecursiveMapCodec<>(name, wrapped);
    }

    private static class RecursiveMapCodec<A> extends MapCodec<A> {
        private final String name;
        private final Supplier<MapCodec<A>> wrapped;

        private RecursiveMapCodec(final String name, final Function<Codec<A>, MapCodec<A>> wrapped) {
            this.name = name;
            this.wrapped = Suppliers.memoize(() -> wrapped.apply(codec()));
        }

        @Override
        public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
            return wrapped.get().encode(input, ops, prefix);
        }

        @Override
        public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
            return wrapped.get().decode(ops, input);
        }

        @Override
        public <T> Stream<T> keys(final DynamicOps<T> ops) {
            return wrapped.get().keys(ops);
        }

        @Override
        public String toString() {
            return "RecursiveMapCodec[" + name + ']';
        }
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> supplier) {
        return new RecursiveCodec<>(supplier.toString(), codec -> supplier.get());
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return ExtraCodecs.validate(codec, (List<T> list) ->
                list.isEmpty()
                        ? DataResult.error("List must have contents")
                        : DataResult.success(list));
    }

    public static <F, S> Codec<Either<F, S>> xor(final Codec<F> first, final Codec<S> second) {
        return new XorCodec<>(first, second);
    }


    public static <T> Codec<T> withAlternative(Codec<T> codec, Codec<? extends T> codec2) {
        return Codec.either(codec, codec2).xmap(either -> either.map(object -> object, object -> object), Either::left);
    }

    public static <A> Codec<A> lazyInitialized(final Supplier<Codec<A>> delegate) {
        return new RecursiveCodec<>(delegate.toString(), self -> delegate.get());
    }

    public static <A> Codec<A> recursive(final String name, final Function<Codec<A>, Codec<A>> wrapped) {
        return new RecursiveCodec<>(name, wrapped);
    }



    public static <A> Codec<A> validate(Codec<A> codec, final Function<A, DataResult<A>> checker) {
        return codec.flatXmap(checker, checker);
    }

    public static <K, V> Codec<Map<K, V>> dispatchedMap(final Codec<K> keyCodec, final Function<K, Codec<? extends V>> valueCodecFunction) {
        return new DispatchedMapCodec<>(keyCodec, valueCodecFunction);
    }



    public static <E> Codec<E> idResolverCodec(ToIntFunction<E> toIntFunction, IntFunction<E> intFunction, int n2) {
        return Codec.INT.flatXmap(n -> Optional.ofNullable(intFunction.apply((int)n)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element id: " + n)), object -> {
            int n3 = toIntFunction.applyAsInt(object);
            return n3 == n3 ? DataResult.error("Element with unknown id: " + object) : DataResult.success(n3);
        });
    }

    public static <E> Codec<E> stringResolverCodec(Function<E, String> function, Function<String, E> function2) {
        return Codec.STRING.flatXmap(string -> Optional.ofNullable(function2.apply((String)string))
                .map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name:" + string)), object ->
                Optional.ofNullable((String)function.apply(object)).map(DataResult::success).orElseGet(() -> DataResult.error("Element with unknown name: " + object)));
    }

    public static <E> Codec<E> orCompressed(final Codec<E> codec, final Codec<E> codec2) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E e, DynamicOps<T> dynamicOps, T t) {
                if (dynamicOps.compressMaps()) {
                    return codec2.encode(e, dynamicOps, t);
                }
                return codec.encode(e, dynamicOps, t);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T t) {
                if (dynamicOps.compressMaps()) {
                    return codec2.decode(dynamicOps, t);
                }
                return codec.decode(dynamicOps, t);
            }

            public String toString() {
                return codec + " orCompressed " + codec2;
            }
        };
    }

    public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH.xmap(dynamic -> (JsonElement)dynamic.convert((DynamicOps) JsonOps.INSTANCE).getValue(), jsonElement -> new Dynamic((DynamicOps)JsonOps.INSTANCE, jsonElement));

    public static final Codec<ITextComponent> COMPONENT = JSON.flatXmap(
            jsonElement -> {
                try {
                    return DataResult.success(ITextComponent.Serializer.fromJson(jsonElement));
                } catch (JsonParseException jsonParseException) {
                    return DataResult.error(jsonParseException.getMessage());
                }
            },
            component -> {
                try {
                    return DataResult.success(ITextComponent.Serializer.toJsonTree(component));
                } catch (IllegalArgumentException illegalArgumentException) {
                    return DataResult.error(illegalArgumentException.getMessage());
                }
            }
    );






    public static final Codec<Integer> NON_NEGATIVE_INT = ExtraCodecs.intRangeWithMessage(0, Integer.MAX_VALUE, n -> "Value must be non-negative: " + n);
    public static final Codec<Integer> POSITIVE_INT = ExtraCodecs.intRangeWithMessage(1, Integer.MAX_VALUE, n -> "Value must be positive: " + n);
    public static final Codec<Float> NON_NEGATIVE_FLOAT = ExtraCodecs.floatRangeMinInclusiveWithMessage(0.0f, Float.MAX_VALUE, f -> "Value must be non-negative: " + f);
    public static final Codec<Float> POSITIVE_FLOAT = ExtraCodecs.floatRangeMinExclusiveWithMessage(0.0f, Float.MAX_VALUE, f -> "Value must be positive: " + f);

    private static Codec<Float> floatRangeMinInclusiveWithMessage(float f, float f2, Function<Float, String> function) {
        return validate(Codec.FLOAT,f3 -> {
            if (f3.compareTo(Float.valueOf(f)) >= 0 && f3.compareTo(Float.valueOf(f2)) <= 0) {
                return DataResult.success(f3);
            }
            return DataResult.error(function.apply((Float)f3));
        });
    }

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float f, float f2, Function<Float, String> function) {
        return validate(Codec.FLOAT, f3 -> {
            if (f3.compareTo(Float.valueOf(f)) > 0 && f3.compareTo(Float.valueOf(f2)) <= 0) {
                return DataResult.success(f3);
            }
            return DataResult.error((String)function.apply((Float)f3));
        });
    }

    public static Codec<Float> floatRange(float f, float f2) {
        return ExtraCodecs.floatRangeMinInclusiveWithMessage(f, f2, f3 -> "Value must be within range [" + f + ";" + f2 + "]: " + f3);
    }

    private static <N extends Number> Function<N, DataResult<N>> checkRangeWithMessage(N n, N n2, Function<N, String> function) {
        return number3 -> {
            if (((Comparable)(number3)).compareTo(n) >= 0 && ((Comparable)(number3)).compareTo(n2) <= 0) {
                return DataResult.success(number3);
            }
            return DataResult.error((String)((String)function.apply(number3)));
        };
    }


}
