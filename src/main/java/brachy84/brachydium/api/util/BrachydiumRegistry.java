package brachy84.brachydium.api.util;

import brachy84.brachydium.Brachydium;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class BrachydiumRegistry<K, V>  {

    private final Map<K, V> registries = new HashMap<>();
    private final Map<V, K> keyMap = new HashMap<>();

    public BrachydiumRegistry() {}

    public boolean hasKey(K k) {
        return registries.containsKey(k);
    }

    public V register(K k, V v) {
        Objects.requireNonNull(k);
        Objects.requireNonNull(v);
        registries.put(k, v);
        keyMap.put(v, k);
        return v;
    }

    public V getEntry(K k) {
        V v = registries.get(k);
        if(v == null) {
            throw new NullPointerException("No entry found for key " + k);
        }
        return v;
    }

    public K getKey(V v) {
        K k = keyMap.get(v);
        if(k == null) {
            throw new NullPointerException("No entry found for key " + v);
        }
        return k;
    }

    @Nullable
    public V tryGetEntry(K k) {
        try {
            return getEntry(k);
        } catch (NullPointerException e) {
            Brachydium.LOGGER.error("Could not find entry for key " + k);
            return null;
        }
    }

    @Nullable
    public K tryGetKey(V v) {
        try {
            return getKey(v);
        } catch (NullPointerException e) {
            Brachydium.LOGGER.error("Could not find key for entry " + v);
            return null;
        }
    }

    public void foreach(Consumer<V> consumer) {
        for(V v : registries.values()) {
            consumer.accept(v);
        }
    }
}
