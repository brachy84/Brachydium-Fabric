package brachy84.brachydium.api.material;

import brachy84.brachydium.api.util.BrachydiumRegistry;

import java.util.HashSet;
import java.util.Set;

public interface IMaterialFlag<T extends IMaterialFlag<T>> {

    static BrachydiumRegistry<String, IMaterialFlag<?>> REGISTRY = new BrachydiumRegistry<>();

    default void register() {
        REGISTRY.register(getIdentifier(), this);
    }

    Set<Material> materials = new HashSet<>();

    default void addMaterial(Material material) {
        materials.add(material);
    }

    String getIdentifier();

    @Deprecated
    void addProcessor(IFlagRegistrationHandler<T> processor);

    @Deprecated
    void runProcessors(Material material);

    void addResourceProvider(IFlagRegistrationHandler<T> processor);

    void runResourceProviders(Material material);

    IMaterialFlag<?>[] getRequiredFlags();

    @FunctionalInterface
    interface IFlagRegistrationHandler<T> {

        void processMaterial(Material material, T t);
    }
}
