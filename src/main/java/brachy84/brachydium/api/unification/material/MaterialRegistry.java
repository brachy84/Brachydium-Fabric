package brachy84.brachydium.api.unification.material;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaterialRegistry {

    public static final Event<MaterialRegistryEvent> EVENT = EventFactory.createArrayBacked(MaterialRegistryEvent.class, listeners -> () -> {
        for(MaterialRegistryEvent event : listeners) {
            event.register();
        }
    });

    private MaterialRegistry() {
    }

    public static final BrachydiumRegistry<String, Material> MATERIAL_REGISTRY = new BrachydiumRegistry<>();

    private static List<Material> DEFERRED_REGISTRY = new ArrayList<>();

    public static void finalizeMaterials(boolean shouldFreeze) {
        if (shouldFreeze) Brachydium.LOGGER.info("Freezing material registry...");
        DEFERRED_REGISTRY.forEach(MaterialRegistry::finalizeRegistry);
        DEFERRED_REGISTRY.forEach(MaterialRegistry::postVerify);
        DEFERRED_REGISTRY = shouldFreeze ? null : new ArrayList<>(); // destroy the deferred registry
        if (shouldFreeze) MATERIAL_REGISTRY.freeze();
    }

    public static boolean isFrozen() {
        return MATERIAL_REGISTRY.isFrozen();
    }

    private static void finalizeRegistry(Material material) {
        material.verifyMaterial();
    }

    private static void postVerify(Material material) {
        material.postVerify();
        MATERIAL_REGISTRY.register(material.toString(), material);
    }

    public static void register(Material material) {
        DEFERRED_REGISTRY.add(material);
    }

    @Nullable
    public static Material get(String name) {
        return MATERIAL_REGISTRY.getEntry(name);
    }

    public static List<Material> getAllMaterials() {
        return Lists.newArrayList(MATERIAL_REGISTRY);
    }

    public interface MaterialRegistryEvent {
        void register();
    }
}
