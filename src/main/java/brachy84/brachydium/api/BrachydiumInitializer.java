package brachy84.brachydium.api;

public interface BrachydiumInitializer {

    /**
     * @return the id of the implementing mod
     */
    String getModId();

    /**
     * @return the name that will appear in tooltips of items registered by this mod
     */
    String getModName();

    /**
     * register brachydium materials here
     */
    default void registerMaterials() {}

    /**
     * Use this to register MC stuff like blocks, items and block entities
     */
    default void registerGeneral() {}

    /**
     * register brachydium machine recipes here
     * use {@link #registerGeneral()} for vanilla recipes
     */
    default void registerRecipes() {}
}
