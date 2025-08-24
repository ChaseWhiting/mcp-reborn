package net.minecraft.util.registry;

import com.mojang.serialization.DynamicOps;


// A simple class to wrap DynamicOps with registry access.
public class RegistryOps<T> extends DelegatingOps<T> {

    private final DynamicOps<T> ops;
    private final DynamicRegistries registry;

    public RegistryOps(DynamicOps<T> ops, DynamicRegistries registry) {
        super(ops);
        this.ops = ops;
        this.registry = registry;
    }

    public DynamicOps<T> ops() {
        return this.ops;
    }

    public DynamicRegistries registry() {
        return this.registry;
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> ops, DynamicRegistries registry) {
        return new RegistryOps<>(ops, registry);
    }
}
