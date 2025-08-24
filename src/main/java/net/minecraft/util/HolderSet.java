
package net.minecraft.util;

import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface HolderSet<T>
extends Iterable<Holder<T>> {
    public Stream<Holder<T>> stream();

    public int size();

    public Either<Collection<T>, List<Holder<T>>> unwrap();

    public Optional<Holder<T>> getRandomElement(Random var1);

    public Holder<T> get(int var1);

    public boolean contains(Holder<T> var1);


    @Deprecated
    @VisibleForTesting
    public static <T> Named<T> emptyNamed(Collection<T> tagKey) {
        return new Named<T>(tagKey);
    }

    @SafeVarargs
    public static <T> Direct<T> direct(Holder<T> ... holderArray) {
        return new Direct<T>(List.of(holderArray));
    }

    public static <T> Direct<T> direct(List<? extends Holder<T>> list) {
        return new Direct(List.copyOf(list));
    }

    @SafeVarargs
    public static <E, T> Direct<T> direct(Function<E, Holder<T>> function, E ... EArray) {
        return HolderSet.direct(Stream.of(EArray).map(function).collect(Collectors.toList()));
    }

    public static <E, T> Direct<T> direct(Function<E, Holder<T>> function, List<E> list) {
        return HolderSet.direct(list.stream().map(function).collect(Collectors.toList()));
    }

    public static class Named<T>
    extends ListBacked<T> {
        private final Collection<T> key;
        private List<Holder<T>> contents = List.of();

        Named(Collection<T> tagKey) {
            this.key = tagKey;
        }

        void bind(List<Holder<T>> list) {
            this.contents = List.copyOf(list);
        }

        public Collection<T> key() {
            return this.key;
        }

        @Override
        protected List<Holder<T>> contents() {
            return this.contents;
        }

        @Override
        public Either<Collection<T>, List<Holder<T>>> unwrap() {
            return Either.left(this.key);
        }


        @Override
        public boolean contains(Holder<T> holder) {
            return holder.is(this.key);
        }

        public String toString() {
            return "NamedSet(" + this.key + ")[" + this.contents + "]";
        }

    }

    public static class Direct<T>
    extends ListBacked<T> {
        private final List<Holder<T>> contents;
        @Nullable
        private Set<Holder<T>> contentsSet;

        Direct(List<Holder<T>> list) {
            this.contents = list;
        }

        @Override
        protected List<Holder<T>> contents() {
            return this.contents;
        }

        @Override
        public Either<Collection<T>, List<Holder<T>>> unwrap() {
            return Either.right(this.contents);
        }

        @Override
        public boolean contains(Holder<T> holder) {
            if (this.contentsSet == null) {
                this.contentsSet = Set.copyOf(this.contents);
            }
            return this.contentsSet.contains(holder);
        }

        public String toString() {
            return "DirectSet[" + this.contents + "]";
        }
    }

    public static abstract class ListBacked<T>
    implements HolderSet<T> {
        protected abstract List<Holder<T>> contents();

        @Override
        public int size() {
            return this.contents().size();
        }

        @Override
        public Spliterator<Holder<T>> spliterator() {
            return this.contents().spliterator();
        }

        @Override
        public Iterator<Holder<T>> iterator() {
            return this.contents().iterator();
        }

        @Override
        public Stream<Holder<T>> stream() {
            return this.contents().stream();
        }

        @Override
        public Optional<Holder<T>> getRandomElement(Random randomSource) {
            return Util.getRandomSafe(this.contents(), randomSource);
        }

        @Override
        public Holder<T> get(int n) {
            return this.contents().get(n);
        }
    }
}

