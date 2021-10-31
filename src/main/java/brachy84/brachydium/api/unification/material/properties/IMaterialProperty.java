package brachy84.brachydium.api.unification.material.properties;

@FunctionalInterface
public interface IMaterialProperty<T> {

    void verifyProperty(MaterialProperties properties);
}
