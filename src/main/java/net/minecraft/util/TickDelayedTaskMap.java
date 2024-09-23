package net.minecraft.util;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.TickDelayedTask;

public class TickDelayedTaskMap<K> implements Map<K, TickDelayedTask> {
    private final Map<K, TickDelayedTask> map;
    private final MinecraftServer server;


    public TickDelayedTaskMap(MinecraftServer server) {
        this.map = new HashMap<>();
        this.server = server;
    }

    public void execute(K run) {
        server.tell(map.getOrDefault(run, new TickDelayedTask(0)));
    }

    // Executes all TickDelayedTasks that are due at or before the given tick
    public void executeAll() {
        map.values().forEach(server::tell);
    }

    // Implement all the Map methods by delegating to the underlying map
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public TickDelayedTask get(Object key) {
        return map.get(key);
    }

    @Override
    public TickDelayedTask put(K key, TickDelayedTask value) {
        return map.put(key, value);
    }

    @Override
    public TickDelayedTask remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends TickDelayedTask> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public java.util.Set<K> keySet() {
        return map.keySet();
    }

    public Stream<TickDelayedTask> stream() {
        return this.map.values().stream();
    }

    @Override
    public java.util.Collection<TickDelayedTask> values() {
        return map.values();
    }

    @Override
    public java.util.Set<Entry<K, TickDelayedTask>> entrySet() {
        return map.entrySet();
    }

    public String toString() {
        return map.toString();
    }

    public int hashCode() {
        int a = 31;
        int result = 1;
        for (TickDelayedTask tickDelayedTask : this.map.values()) {
            result = a * result + a << result >>> tickDelayedTask.hashCode();
        }
        return result;
    }

    // Custom functionality for TickDelayedTask
}
