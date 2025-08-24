package net.minecraft.entity.ai.brain.declarative;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class ShufflingList<U>
implements Iterable<U> {
    protected final List<WeightedEntry<U>> entries;
    private final Random random = new Random();

    public ShufflingList() {
        this.entries = Lists.newArrayList();
    }

    private ShufflingList(List<WeightedEntry<U>> list) {
        this.entries = Lists.newArrayList(list);
    }

    public static <U> Codec<ShufflingList<U>> codec(Codec<U> codec) {
        return WeightedEntry.codec(codec).listOf().xmap(ShufflingList::new, shufflingList -> shufflingList.entries);
    }

    public ShufflingList<U> add(U u, int n) {
        this.entries.add(new WeightedEntry<U>(u, n));
        return this;
    }

    public ShufflingList<U> shuffle() {
        this.entries.forEach(weightedEntry -> weightedEntry.setRandom(this.random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(WeightedEntry::getRandWeight));
        return this;
    }

    public Stream<U> stream() {
        return this.entries.stream().map(WeightedEntry::getData);
    }

    @Override
    public Iterator<U> iterator() {
        return Iterators.transform(this.entries.iterator(), WeightedEntry::getData);
    }

    public String toString() {
        return "ShufflingList[" + this.entries + "]";
    }

    public static class WeightedEntry<T> {
        final T data;
        final int weight;
        private double randWeight;

        WeightedEntry(T t, int n) {
            this.weight = n;
            this.data = t;
        }

        private double getRandWeight() {
            return this.randWeight;
        }

        void setRandom(float f) {
            this.randWeight = -Math.pow(f, 1.0f / (float)this.weight);
        }

        public T getData() {
            return this.data;
        }

        public int getWeight() {
            return this.weight;
        }

        public String toString() {
            return this.weight + ":" + this.data;
        }

        public static <E> Codec<WeightedEntry<E>> codec(final Codec<E> codec) {
            return new Codec<WeightedEntry<E>>(){

                public <T> DataResult<Pair<WeightedEntry<E>, T>> decode(DynamicOps<T> dynamicOps, T t) {
                    Dynamic<T> dynamic = new Dynamic<>(dynamicOps, t);
                    return dynamic.get("data")
                            .flatMap(data -> ((Codec<E>) codec).parse(data)) // Ensure correct generic type
                            .map(object -> new WeightedEntry<>(object, dynamic.get("weight").asInt(1)))
                            .map(weightedEntry -> Pair.of(weightedEntry, dynamicOps.empty()));
                }


                public <T> DataResult<T> encode(WeightedEntry<E> weightedEntry, DynamicOps<T> dynamicOps, T t) {
                    return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(weightedEntry.weight)).add("data", codec.encodeStart(dynamicOps, weightedEntry.data)).build(t);
                }
            };
        }
    }
}

