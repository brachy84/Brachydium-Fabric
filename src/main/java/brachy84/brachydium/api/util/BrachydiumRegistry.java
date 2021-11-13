package brachy84.brachydium.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class BrachydiumRegistry<K, V> implements Iterable<V> {

    private final Map<K, V> registries = new HashMap<>();
    private final Map<V, K> keyMap = new HashMap<>();

    private boolean frozen;

    public BrachydiumRegistry() {
        this.frozen = false;
    }

    public boolean hasKey(K k) {
        return registries.containsKey(k);
    }

    public V register(K k, V v) {
        if (isFrozen()) {
            throw new IllegalStateException("Can't register when registry is frozen");
        }
        Objects.requireNonNull(k);
        Objects.requireNonNull(v);
        registries.put(k, v);
        keyMap.put(v, k);
        return v;
    }

    public V getEntry(K k) {
        V v = registries.get(k);
        if (v == null) {
            throw new NullPointerException("No entry found for key " + k);
        }
        return v;
    }

    public K getKey(V v) {
        K k = keyMap.get(v);
        if (k == null) {
            throw new NullPointerException("No entry found for key " + v);
        }
        return k;
    }

    @Nullable
    public V tryGetEntry(K k) {
        return registries.get(k);
    }

    @Nullable
    public K tryGetKey(V v) {
        return keyMap.get(v);
    }

    public void freeze() {
        this.frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return registries.values().iterator();
    }

    public Iterable<Map.Entry<K, V>> getEntryIterable() {
        return this::getEntryIterator;
    }

    @NotNull
    public Iterator<Map.Entry<K, V>> getEntryIterator() {
        return registries.entrySet().iterator();
    }
}
