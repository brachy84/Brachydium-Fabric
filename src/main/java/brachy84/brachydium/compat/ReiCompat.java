package brachy84.brachydium.compat;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.MTRecipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.wrapper.ModularGuiHandledScreen;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.OverlayDecider;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ReiCompat implements REIPluginV0 {

    @Override
    public Identifier getPluginIdentifier() {
        return Brachydium.id("default_plugin");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            recipeHelper.registerCategory(new RecipeTableCategory(recipeTable));
        }
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            Identifier id = Brachydium.id(recipeTable.unlocalizedName + "_recipes");
            for(MTRecipe recipe : recipeTable.getRecipeList()) {
                recipeHelper.registerDisplay(new RecipeTableDisplay(recipe, id));
            }
        }
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            Identifier id = Brachydium.id(recipeTable.unlocalizedName + "_recipes");
            recipeHelper.registerWorkingStations(id, recipeTable.getTileItems().stream().map(EntryStack::create).collect(Collectors.toList()));
        }
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        displayHelper.registerHandler(new OverlayDecider() {
            @Override
            public boolean isHandingScreen(Class<?> screen) {
                return screen.isAssignableFrom(ModularGuiHandledScreen.class);
            }

            @Override
            public ActionResult shouldScreenBeOverlayed(Class<?> screen) {
                if(screen.isAssignableFrom(ModularGuiHandledScreen.class)) {
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }
        });
    }
}
