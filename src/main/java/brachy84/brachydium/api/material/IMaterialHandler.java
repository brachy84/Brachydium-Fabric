package brachy84.brachydium.api.material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IMaterialHandler {
    default void init() {
        Material.register(getMaterials());
    }

    Material[] getMaterials();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MaterialRegistry {
    }
}
