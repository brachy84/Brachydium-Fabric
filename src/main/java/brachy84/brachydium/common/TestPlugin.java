package brachy84.brachydium.common;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.blockEntity.MTBlockEntities;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.tag.TagProcessors;
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

    @Override
    public void registerGeneral() {
        TagProcessors.init();
        MTBlockEntities.init();
        //RecipeTables.init();
        Tools.register();
    }
}
