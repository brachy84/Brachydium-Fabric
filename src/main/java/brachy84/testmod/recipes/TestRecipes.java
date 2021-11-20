package brachy84.testmod.recipes;

import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.unification.material.Materials;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

import static brachy84.brachydium.api.unification.material.Materials.*;
import static brachy84.brachydium.api.unification.ore.TagDictionary.*;

public class TestRecipes {

    public static void init() {
        RecipeTables.ALLOYER_RECIPES.recipeBuilder()
                //.input(ingot, Aluminium, 1)
                .input(Items.GOLD_INGOT, 1)
                .input(Items.IRON_INGOT, 1)
                .output(Items.NETHER_STAR, 1)
                .duration(100)
                .EUt(20)
                .buildAndRegister();
        RecipeTables.ALLOYER_RECIPES.recipeBuilder("test_iron")
                .input(ingot, Materials.Aluminium, 1)
                .input(dust, Materials.Copper, 1)
                .output(Items.IRON_INGOT, 2)
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        RecipeTables.MIXER_RECIPES.recipeBuilder()
                .input(plate, Bronze, 1)
                .fluidInput(Fluids.WATER, 81000)
                .fluidInput(Fluids.LAVA, 81000)
                .output(Blocks.OBSIDIAN.asItem(), 2)
                .duration(60)
                .EUt(512)
                .buildAndRegister();

        /*RecipeTables.ALLOYER_RECIPES.recipeBuilder("special_gold")
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
                .buildAndRegister();*/
    }
}
