package brachy84.brachydium.api;

public interface BrachydiumInitializer {

    /**
     * @return the id of the implementing mod
     */
    String getModId();

    default void registerMaterials() {}

    /**
     * Use this to register MC stuff like blocks, items and block entities
     */
    default void registerGeneral() {}

    default void registerRecipes() {}
}
