package net.minecraft.util.component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.INBT;
import org.jetbrains.annotations.NotNull;

public class EncoderCache {
    final LoadingCache<Key<?, ?>, DataResult<?>> cache;

    public EncoderCache(int n) {
        this.cache = CacheBuilder.newBuilder().maximumSize((long)n).concurrencyLevel(1).softValues().build(new CacheLoader<>() {

            public DataResult<?> load(@NotNull Key<?, ?> key) {
                return key.resolve();
            }

        });
    }

    public <A> Codec<A> wrap(final Codec<A> codec) {
        return new Codec<A>(){

            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicOps, T t) {
                return codec.decode(dynamicOps, t);
            }

            public <T> DataResult<T> encode(A a, DynamicOps<T> dynamicOps, T t) {
                return ((DataResult)EncoderCache.this.cache.getUnchecked(new Key(codec, a, dynamicOps))).map(object -> {
                    if (object instanceof INBT tag) {
                        return tag.copy();
                    }
                    return object;
                });
            }
        };
    }

    record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
        public DataResult<T> resolve() {
            return this.codec.encodeStart(this.ops, this.value);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Key) {
                Key key = (Key)object;
                return this.codec == key.codec && this.value.equals(key.value) && this.ops.equals(key.ops);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int n = System.identityHashCode(this.codec);
            n = 31 * n + this.value.hashCode();
            n = 31 * n + this.ops.hashCode();
            return n;
        }
    }
}
