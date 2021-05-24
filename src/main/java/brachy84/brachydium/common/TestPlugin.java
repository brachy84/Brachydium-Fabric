package brachy84.brachydium.common;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.common.recipes.TestRecipes;

public class TestPlugin implements BrachydiumInitializer {
    @Override
    public String getModId() {
        return "brachydium";
    }

    @Override
    public void registerRecipes() {
        TestRecipes.init();
    }

    @Override
    public void registerMaterials() {
        Materials.init();
    }
}
