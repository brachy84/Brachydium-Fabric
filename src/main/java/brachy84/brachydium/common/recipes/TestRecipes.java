package brachy84.brachydium.common.recipes;

import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.tag.Tags;
import brachy84.brachydium.api.util.Util;
import brachy84.brachydium.common.Materials;
import net.minecraft.item.Items;

public class TestRecipes {

    public static void init() {
        RecipeTables.ALLOYER_RECIPES.recipeBuilder("special_iron")
                .input(Tags.INGOT, Materials.Aluminium, 1)
                .input(Tags.DUST, Materials.Copper, 1)
                .outputs(Util.getStack(Items.IRON_INGOT, 2))
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        RecipeTables.ALLOYER_RECIPES.recipeBuilder("special_gold")
                .input(Materials.Aluminium.dust(1), Materials.Copper.ingot(1))
                .outputs(Util.getStack(Items.GOLD_INGOT, 2))
                .duration(40)
                .EUt(28)
                .buildAndRegister();
    }
}
