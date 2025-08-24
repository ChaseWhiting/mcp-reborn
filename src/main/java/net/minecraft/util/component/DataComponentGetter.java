package net.minecraft.util.component;

import javax.annotation.Nullable;

public interface DataComponentGetter {
    @Nullable
    public <T> T get(DataComponentType<? extends T> var1);

    default public <T> T getOrDefault(DataComponentType<? extends T> dataComponentType, T t) {
        T t2 = this.get(dataComponentType);
        return t2 != null ? t2 : t;
    }

    @Nullable
    default public <T> TypedDataComponent<T> getTyped(DataComponentType<T> dataComponentType) {
        T t = this.get(dataComponentType);
        return t != null ? new TypedDataComponent<T>(dataComponentType, t) : null;
    }
}
