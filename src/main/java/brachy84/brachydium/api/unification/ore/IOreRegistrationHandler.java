package brachy84.brachydium.api.unification.ore;

import brachy84.brachydium.api.unification.material.Material;

@FunctionalInterface
public interface IOreRegistrationHandler {

    void processMaterial(TagDictionary.Entry tag, Material material);

}
