package net.minecraft.util.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class PatchedDataComponentMap
implements DataComponentMap {
    private final DataComponentMap prototype;
    private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;
    private boolean copyOnWrite;

    public PatchedDataComponentMap(DataComponentMap dataComponentMap) {
        this(dataComponentMap, Reference2ObjectMaps.emptyMap(), true);
    }

    private PatchedDataComponentMap(DataComponentMap dataComponentMap, Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap, boolean bl) {
        this.prototype = dataComponentMap;
        this.patch = reference2ObjectMap;
        this.copyOnWrite = bl;
    }

    public static PatchedDataComponentMap fromPatch(DataComponentMap dataComponentMap, DataComponentPatch dataComponentPatch) {
        if (PatchedDataComponentMap.isPatchSanitized(dataComponentMap, dataComponentPatch.map)) {
            return new PatchedDataComponentMap(dataComponentMap, dataComponentPatch.map, true);
        }
        PatchedDataComponentMap patchedDataComponentMap = new PatchedDataComponentMap(dataComponentMap);
        patchedDataComponentMap.applyPatch(dataComponentPatch);
        return patchedDataComponentMap;
    }

    private static boolean isPatchSanitized(DataComponentMap dataComponentMap, Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap) {
        for (Map.Entry entry : Reference2ObjectMaps.fastIterable(reference2ObjectMap)) {
            Object t = dataComponentMap.get((DataComponentType)entry.getKey());
            Optional optional = (Optional)entry.getValue();
            if (optional.isPresent() && optional.get().equals(t)) {
                return false;
            }
            if (!optional.isEmpty() || t != null) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public <T> T get(DataComponentType<? extends T> dataComponentType) {
        Optional<?> optional = this.patch.get(dataComponentType);
        if (optional != null) {
            @SuppressWarnings("unchecked")
            T value = (T) optional.orElse(null);
            return value;
        }
        return this.prototype.get(dataComponentType);
    }


    public boolean hasNonDefault(DataComponentType<?> dataComponentType) {
        return this.patch.containsKey(dataComponentType);
    }

    @Nullable
    public <T> T set(DataComponentType<T> dataComponentType, @Nullable T value) {
        this.ensureMapOwnership();
        T oldValue = this.prototype.get(dataComponentType);

        Optional<T> optional;
        if (Objects.equals(value, oldValue)) {
            optional = (Optional<T>) this.patch.remove(dataComponentType);
        } else {
            optional = (Optional<T>) this.patch.put(dataComponentType, Optional.ofNullable(value));
        }

        return optional != null ? optional.orElse(oldValue) : oldValue;
    }


    @Nullable
    public <T> T remove(DataComponentType<? extends T> dataComponentType) {
        this.ensureMapOwnership();
        T oldValue = this.prototype.get(dataComponentType);

        Optional<T> optional;
        if (oldValue != null) {
            optional = (Optional<T>) this.patch.put(dataComponentType, Optional.empty());
        } else {
            optional = (Optional<T>) this.patch.remove(dataComponentType);
        }

        return optional != null ? optional.orElse(null) : oldValue;
    }


    public void applyPatch(DataComponentPatch dataComponentPatch) {
        this.ensureMapOwnership();
        for (Map.Entry entry : Reference2ObjectMaps.fastIterable(dataComponentPatch.map)) {
            this.applyPatch((DataComponentType)entry.getKey(), (Optional)entry.getValue());
        }
    }

    private void applyPatch(DataComponentType<?> dataComponentType, Optional<?> optional) {
        Object obj = this.prototype.get(dataComponentType);
        if (optional.isPresent()) {
            if (optional.get().equals(obj)) {
                this.patch.remove(dataComponentType);
            } else {
                this.patch.put(dataComponentType, optional);
            }
        } else if (obj != null) {
            this.patch.put(dataComponentType, Optional.empty());
        } else {
            this.patch.remove(dataComponentType);
        }
    }

    public void restorePatch(DataComponentPatch dataComponentPatch) {
        this.ensureMapOwnership();
        this.patch.clear();
        this.patch.putAll(dataComponentPatch.map);
    }

    public void clearPatch() {
        this.ensureMapOwnership();
        this.patch.clear();
    }

    public void setAll(DataComponentMap dataComponentMap) {
        for (TypedDataComponent<?> typedDataComponent : dataComponentMap) {
            typedDataComponent.applyTo(this);
        }
    }

    private void ensureMapOwnership() {
        if (this.copyOnWrite) {
            this.patch = new Reference2ObjectArrayMap(this.patch);
            this.copyOnWrite = false;
        }
    }

    @Override
    public Set<DataComponentType<?>> keySet() {
        if (this.patch.isEmpty()) {
            return this.prototype.keySet();
        }
        ReferenceArraySet referenceArraySet = new ReferenceArraySet(Collections.singleton(this.prototype.keySet()));
        for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(this.patch)) {
            Optional optional = (Optional)entry.getValue();
            if (optional.isPresent()) {
                referenceArraySet.add((DataComponentType)entry.getKey());
                continue;
            }
            referenceArraySet.remove(entry.getKey());
        }
        return referenceArraySet;
    }

    @Override
    public Iterator<TypedDataComponent<?>> iterator() {
        if (this.patch.isEmpty()) {
            return this.prototype.iterator();
        }
        ArrayList<TypedDataComponent<?>> arrayList = new ArrayList<>(this.patch.size() + this.prototype.size());

        // Iterate over the entries in patch with proper generics
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(this.patch)) {
            Optional<?> optionalValue = entry.getValue();
            if (!optionalValue.isPresent()) {
                continue;
            }
            TypedDataComponent<?> component = TypedDataComponent.createUnchecked(entry.getKey(), optionalValue.get());
            arrayList.add(component);
        }

        // Add prototype elements if their type is not present in patch
        for (TypedDataComponent<?> component : this.prototype) {
            if (this.patch.containsKey(component.type())) {
                continue;
            }
            arrayList.add(component);
        }
        return arrayList.iterator();
    }


    @Override
    public int size() {
        int n = this.prototype.size();
        for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(this.patch)) {
            boolean bl;
            boolean bl2 = ((Optional)entry.getValue()).isPresent();
            if (bl2 == (bl = this.prototype.has((DataComponentType)entry.getKey()))) continue;
            n += bl2 ? 1 : -1;
        }
        return n;
    }

    public DataComponentPatch asPatch() {
        if (this.patch.isEmpty()) {
            return DataComponentPatch.EMPTY;
        }
        this.copyOnWrite = true;
        return new DataComponentPatch(this.patch);
    }

    public PatchedDataComponentMap copy() {
        this.copyOnWrite = true;
        return new PatchedDataComponentMap(this.prototype, this.patch, true);
    }

    public DataComponentMap toImmutableMap() {
        if (this.patch.isEmpty()) {
            return this.prototype;
        }
        return this.copy();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PatchedDataComponentMap)) return false;
        PatchedDataComponentMap patchedDataComponentMap = (PatchedDataComponentMap)object;
        if (!this.prototype.equals(patchedDataComponentMap.prototype)) return false;
        if (!this.patch.equals(patchedDataComponentMap.patch)) return false;
        return true;
    }

    public int hashCode() {
        return this.prototype.hashCode() + this.patch.hashCode() * 31;
    }

    public String toString() {
        return "{" + this.stream().map(TypedDataComponent::toString).collect(Collectors.joining(", ")) + "}";
    }
}

