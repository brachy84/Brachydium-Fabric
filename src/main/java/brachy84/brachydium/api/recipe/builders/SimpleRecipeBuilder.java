package brachy84.brachydium.api.recipe.builders;

import brachy84.brachydium.api.recipe.Recipe;
import brachy84.brachydium.api.recipe.RecipeBuilder;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.util.ValidationResult;

public class SimpleRecipeBuilder extends RecipeBuilder<SimpleRecipeBuilder> {

    public SimpleRecipeBuilder() {}

    public SimpleRecipeBuilder(Recipe recipe, RecipeTable<SimpleRecipeBuilder> recipeTable) {
        super(recipe, recipeTable);
    }

    public SimpleRecipeBuilder(RecipeBuilder<SimpleRecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public SimpleRecipeBuilder copy() {
        return new SimpleRecipeBuilder(this);
    }
}
