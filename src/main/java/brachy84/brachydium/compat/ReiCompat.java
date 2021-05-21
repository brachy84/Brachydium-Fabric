package brachy84.brachydium.compat;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.recipe.MTRecipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.wrapper.ModularGuiHandledScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ReiCompat implements REIPluginV0 {

    public static Identifier category(RecipeTable<?> recipeTable) {
        return Brachydium.id(recipeTable.unlocalizedName + "_recipes");
    }

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
            for(MTRecipe recipe : recipeTable.getRecipeList()) {
                recipeHelper.registerDisplay(new RecipeTableDisplay(recipe, category(recipeTable)));
            }
        }
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        for(RecipeTable<?> recipeTable : RecipeTable.getRecipeTables()) {
            Identifier id = Brachydium.id(recipeTable.unlocalizedName + "_recipes");
            for(BlockItem item : recipeTable.getTileItems()) {
                recipeHelper.registerWorkingStations(id, EntryStack.create(item));
            }
        }
    }

    @Override
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
    }
}
