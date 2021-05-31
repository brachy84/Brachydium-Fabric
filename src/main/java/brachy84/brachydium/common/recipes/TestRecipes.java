package brachy84.brachydium.common.recipes;

import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.tag.Tags;
import brachy84.brachydium.common.Materials;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

import static brachy84.brachydium.api.tag.TagDictionary.*;

public class TestRecipes {

    public static void init() {
        RecipeTables.ALLOYER_RECIPES.recipeBuilder("special_iron")
                .input(Ingot, Materials.Aluminium, 1)
                .input(Dust, Materials.Copper, 1)
                .output(Items.IRON_INGOT, 2)
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        RecipeTables.ALLOYER_RECIPES.recipeBuilder("special_gold")
                .input(Materials.Aluminium.dust(1), Materials.Copper.ingot(1))
                .output(Items.GOLD_INGOT, 2)
                .duration(40)
                .EUt(28)
                .buildAndRegister();

        RecipeTables.MIXER_RECIPES.recipeBuilder()
                .input(Dust, Materials.Copper, 1)
                .input(Dust, Materials.Aluminium, 1)
                .fluidInput(Fluids.WATER, 81000)
                .output(Items.REDSTONE, 2)
                .duration(40)
                .EUt(28)
                .buildAndRegister();
    }
}
