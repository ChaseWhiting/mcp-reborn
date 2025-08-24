package net.minecraft.util.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.*;
import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface StreamCodec<B, V>
extends StreamDecoder<B, V>,
StreamEncoder<B, V> {
    public static <B, V> StreamCodec<B, V> of(final StreamEncoder<B, V> streamEncoder, final StreamDecoder<B, V> streamDecoder) {
        return new StreamCodec<B, V>(){

            @Override
            public V decode(B b) {
                return streamDecoder.decode(b);
            }

            @Override
            public void encode(B b, V v) {
                streamEncoder.encode(b, v);
            }
        };
    }

    public static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> streamMemberEncoder, final StreamDecoder<B, V> streamDecoder) {
        return new StreamCodec<B, V>(){

            @Override
            public V decode(B b) {
                return streamDecoder.decode(b);
            }

            @Override
            public void encode(B b, V v) {
                streamMemberEncoder.encode(v, b);
            }
        };
    }

    public static <B, V> StreamCodec<B, V> unit(final V v) {
        return new StreamCodec<B, V>(){

            @Override
            public V decode(B b) {
                return v;
            }

            @Override
            public void encode(B b, V v2) {
                if (!v2.equals(v)) {
                    throw new IllegalStateException("Can't encode '" + String.valueOf(v2) + "', expected '" + String.valueOf(v) + "'");
                }
            }
        };
    }

    default public <O> StreamCodec<B, O> apply(CodecOperation<B, V, O> codecOperation) {
        return codecOperation.apply(this);
    }

    default public <O> StreamCodec<B, O> map(final Function<? super V, ? extends O> function, final Function<? super O, ? extends V> function2) {
        return new StreamCodec<B, O>(){

            @Override
            public O decode(B b) {
                return function.apply(StreamCodec.this.decode(b));
            }

            @Override
            public void encode(B b, O o) {
                StreamCodec.this.encode(b, function2.apply(o));
            }
        };
    }

    default public <O extends ByteBuf> StreamCodec<O, V> mapStream(final Function<O, ? extends B> function) {
        return new StreamCodec<O, V>(){

            @Override
            public V decode(O o) {
                B r = function.apply(o);
                return StreamCodec.this.decode(r);
            }

            @Override
            public void encode(O o, V v) {
                B r = function.apply(o);
                StreamCodec.this.encode(r, v);
            }

        };
    }

    default public <U> StreamCodec<B, U> dispatch(final Function<? super U, ? extends V> function, final Function<? super V, ? extends StreamCodec<? super B, ? extends U>> function2) {
        return new StreamCodec<B, U>(){

            @Override
            public U decode(B b) {
                V t = StreamCodec.this.decode(b);
                StreamCodec streamCodec = (StreamCodec)function2.apply(t);
                return (U) streamCodec.decode(b);
            }

            @Override
            public void encode(B b, U u) {
                V r = function.apply(u);
                StreamCodec streamCodec = (StreamCodec)function2.apply(r);
                StreamCodec.this.encode(b, (V) r);
                streamCodec.encode(b, u);
            }
        };
    }

    public static <B, C, T1> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final Function<T1, C> function2) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                Object t = streamCodec.decode(b);
                return function2.apply((T1) t);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
            }
        };
    }

    public static <B, C, T1, T2> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final BiFunction<T1, T2, C> biFunction) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                return biFunction.apply(t, t2);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final Function3<T1, T2, T3, C> function32) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                return function32.apply(t, t2, t3);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final Function4<T1, T2, T3, T4, C> function42) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                return function42.apply(t, t2, t3, t4);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final Function5<T1, T2, T3, T4, T5, C> function52) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                T5 t5 = streamCodec5.decode(b);
                return function52.apply(t, t2, t3, t4, t5);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
                streamCodec5.encode(b, function5.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final Function6<T1, T2, T3, T4, T5, T6, C> function62) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                T5 t5 = streamCodec5.decode(b);
                T6 t6 = streamCodec6.decode(b);
                return function62.apply(t, t2, t3, t4, t5, t6);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
                streamCodec5.encode(b, function5.apply(c));
                streamCodec6.encode(b, function6.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> function7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> function72) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                T5 t5 = streamCodec5.decode(b);
                T6 t6 = streamCodec6.decode(b);
                T7 t7 = streamCodec7.decode(b);
                return function72.apply(t, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
                streamCodec5.encode(b, function5.apply(c));
                streamCodec6.encode(b, function6.apply(c));
                streamCodec7.encode(b, function7.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> function7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> function8, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> function82) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                T5 t5 = streamCodec5.decode(b);
                T6 t6 = streamCodec6.decode(b);
                T7 t7 = streamCodec7.decode(b);
                T8 t8 = streamCodec8.decode(b);
                return function82.apply(t, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
                streamCodec5.encode(b, function5.apply(c));
                streamCodec6.encode(b, function6.apply(c));
                streamCodec7.encode(b, function7.apply(c));
                streamCodec8.encode(b, function8.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> function7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> function8, final StreamCodec<? super B, T9> streamCodec9, final Function<C, T9> function9, final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> function92) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B b) {
                T1 t = streamCodec.decode(b);
                T2 t2 = streamCodec2.decode(b);
                T3 t3 = streamCodec3.decode(b);
                T4 t4 = streamCodec4.decode(b);
                T5 t5 = streamCodec5.decode(b);
                T6 t6 = streamCodec6.decode(b);
                T7 t7 = streamCodec7.decode(b);
                T8 t8 = streamCodec8.decode(b);
                T9 t9 = streamCodec9.decode(b);
                return function92.apply(t, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(B b, C c) {
                streamCodec.encode(b, function.apply(c));
                streamCodec2.encode(b, function2.apply(c));
                streamCodec3.encode(b, function3.apply(c));
                streamCodec4.encode(b, function4.apply(c));
                streamCodec5.encode(b, function5.apply(c));
                streamCodec6.encode(b, function6.apply(c));
                streamCodec7.encode(b, function7.apply(c));
                streamCodec8.encode(b, function8.apply(c));
                streamCodec9.encode(b, function9.apply(c));
            }
        };
    }

    public static <B, T> StreamCodec<B, T> recursive(final UnaryOperator<StreamCodec<B, T>> unaryOperator) {
        return new StreamCodec<B, T>(){
            private final Supplier<StreamCodec<B, T>> inner = Suppliers.memoize(() -> (StreamCodec)unaryOperator.apply(this));

            @Override
            public T decode(B b) {
                return this.inner.get().decode(b);
            }

            @Override
            public void encode(B b, T t) {
                this.inner.get().encode(b, t);
            }
        };
    }

    default public <S extends B> StreamCodec<S, V> cast() {
        return (StreamCodec<S, V>) this;
    }

    @FunctionalInterface
    public static interface CodecOperation<B, S, T> {
        public StreamCodec<B, T> apply(StreamCodec<B, S> var1);
    }
}