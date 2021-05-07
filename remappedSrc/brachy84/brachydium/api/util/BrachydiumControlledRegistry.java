package brachy84.brachydium.api.util;

import brachy84.brachydium.Brachydium;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BrachydiumControlledRegistry<K, V> {

    private final Map<K, V> registryMap = new HashMap<>();

    public void put(K k, V v) {
        registryMap.put(k, v);
    }

    /**
     * Throws an exception if result is null
     * @param k key
     * @return value
     */
    public V get(K k) throws NullPointerException {
        V v = registryMap.get(k);
        if(v == null) throw new NullPointerException("No Value in MTRegistry for Key " + k);
        return registryMap.get(k);
    }

    /**
     * Returns always, even if null
     * @param k key
     * @return value
     */
    public V tryGet(K k) {
        try {
            return get(k);
        } catch (Exception e) {
            Brachydium.LOGGER.error(e);
            return null;
        }
    }

    public void foreach(BiConsumer<K, V> consumer) {
        for(Map.Entry<K, V> entry : registryMap.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }
}
