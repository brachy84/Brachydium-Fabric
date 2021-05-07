package brachy84.brachydium.api.util;

import brachy84.brachydium.Brachydium;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class BrachydiumRegistry<K, V>  {

    private final Map<K, V> registries = new HashMap<>();

    public BrachydiumRegistry() {}

    public V register(K k, V v) {
        Objects.requireNonNull(k);
        Objects.requireNonNull(v);
        registries.put(k, v);
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
        for(Map.Entry<K, V> entrySet : registries.entrySet()) {
            if(v.equals(entrySet.getValue())) {
                return entrySet.getKey();
            }
        }
        throw new NullPointerException("No key found for entry " + v);
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

}
