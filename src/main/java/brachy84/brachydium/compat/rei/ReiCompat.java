package brachy84.brachydium.compat.rei;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.Recipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ReiCompat implements REIClientPlugin {

    public static CategoryIdentifier<RecipeTableDisplay> category(RecipeTable<?> recipeTable) {
        return CategoryIdentifier.of(Brachydium.id(recipeTable.unlocalizedName + "_recipes"));
    }

    public Identifier getPluginIdentifier() {
        return Brachydium.id("default_plugin");
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            registry.addWorkstations(category(recipeTable), EntryIngredients.ofItemStacks(recipeTable.getTileItems()));
            registry.add(new RecipeTableCategory(recipeTable));
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            for(Recipe recipe : recipeTable.getRecipes()) {
                registry.add(new RecipeTableDisplay(recipe, category(recipeTable)));
            }
        }
    }

    /*@Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler boundsHandler = BaseBoundsHandler.getInstance();
        boundsHandler.registerExclusionZones(ModularGuiHandledScreen.class, () -> {
            List<Rectangle> zones = new ArrayList<>();
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if(screen instanceof ModularGuiHandledScreen) {
                zones.add(((ModularGuiHandledScreen) screen).getGui().getBounds().toReiRectangle());
            }
            return zones;
        });
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
    }*/
}
