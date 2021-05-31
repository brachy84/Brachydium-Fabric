package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.recipe.builders.SimpleRecipeBuilder;

public class RecipeTables {

    public static final RecipeTable<SimpleRecipeBuilder> ALLOYER_RECIPES = new RecipeTable<>("alloy_smelter", 1, 2, 1, 1, 0, 0, 0, 0, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> MIXER_RECIPES = new RecipeTable<>("mixer", 1, 4, 0, 1, 0, 2, 0, 1, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> MACERATOR_RECIPES = new RecipeTable<>("macerator", 1, 1, 1, 3, 0, 0, 0, 0, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> CHEMICAL_REACTOR_RECIPES = new RecipeTable<>("chemical_reactor", 0, 2, 0, 2, 0, 3, 0, 3, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> CLUSTER_MILL = new RecipeTable<>("cluster_mill", 1, 1, 1, 1, 0, 0, 0, 0, new SimpleRecipeBuilder().duration(20).EUt(2));

    public static void init() {
    }
}
