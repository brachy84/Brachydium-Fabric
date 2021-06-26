package brachy84.testmod;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.tag.TagProcessors;
import brachy84.testmod.recipes.TestRecipes;

public class BrachydiumPlugin implements BrachydiumInitializer {
    @Override
    public String getModId() {
        return "testmod";
    }

    @Override
    public void registerRecipes() {
        TestRecipes.init();
    }

    @Override
    public void registerMaterials() {
        Materials.init();
    }

    @Override
    public void registerGeneral() {
        TagProcessors.init();
        MTBlockEntities.init();
        //RecipeTables.init();
        Tools.register();
    }
}
