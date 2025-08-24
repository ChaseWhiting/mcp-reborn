package net.minecraft.util;

import java.util.Collection;
import java.util.function.Predicate;

public class Holder<T> {
    private final T value;

    private Holder(T t) {
        this.value = t;
    }

    public static <T> Holder<T> of(T t) {
        return new Holder<T>(t);
    }


    public T value() {
        return this.value;
    }

    public boolean is(Collection<T> collection) {
        return Util.contains(collection, this.value());
    }

    public boolean test(Predicate<T> tPredicate) {
        return tPredicate.test(this.value());
    }

    @Override
    public String toString() {
        return "Value:{" + this.value + "}";
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.equals(this.value)) return true;
        if (!(obj instanceof Holder)) return false;
        Holder<?> holder = (Holder<?>) obj;
        if (holder.value.getClass().equals(this.value.getClass())) {
            return this.value.equals(holder.value);
        }

        return super.equals(obj);
    }
}
