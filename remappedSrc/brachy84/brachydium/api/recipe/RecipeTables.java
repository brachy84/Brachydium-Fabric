package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.recipe.builders.SimpleRecipeBuilder;

public class RecipeTables {

    public static final RecipeTable<SimpleRecipeBuilder> ALLOYER_RECIPES;

    static {
        ALLOYER_RECIPES = new RecipeTable<>("alloy_smelter", 2, 6, 1, 2, 0, 3, 0, 0, new SimpleRecipeBuilder().duration(400).EUt(2));
    }

    public static void init() {}
}
