package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.recipe.builders.SimpleRecipeBuilder;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;

public class RecipeTables {

    public static final RecipeTable<SimpleRecipeBuilder> ALLOYER_RECIPES = RecipeTable.simpleBuilder("alloy_smelter")
            .itemInputs(1, 2)
            .itemOutputs(1, 1)
            .fluidInputs(0, 0)
            .fluidOutputs(0, 0)
            /*.setGuiBuilder((tile, builder) -> {
                builder.widget(new SlotGroup(tile.getImportInventory(), tile.getImportFluidHandler())
                            .setAlignment(Alignment.CenterLeft).setMargin(EdgeInset.left(8)))
                        .widget(new SlotGroup(tile.getExportInventory(), tile.getExportFluidHandler())
                                .setAlignment(Alignment.CenterRight).setMargin(EdgeInset.right(8)));
            })*/
            .build();

    public static final RecipeTable<SimpleRecipeBuilder> MIXER_RECIPES = RecipeTable.simpleBuilder("mixer")
            .itemInputs(0, 4)
            .itemOutputs(0, 1)
            .fluidInputs(0, 2)
            .fluidOutputs(0, 1)
            /*.setGuiBuilder((tile, builder) -> {
                builder.widget(new SlotGroup(tile.getImportInventory(), tile.getImportFluidHandler())
                        .setAlignment(Alignment.CenterLeft).setMargin(EdgeInset.left(8)))
                        .widget(new SlotGroup(tile.getExportInventory(), tile.getExportFluidHandler())
                                .setAlignment(Alignment.CenterRight).setMargin(EdgeInset.right(8)));
            })*/
            .build();

    /*public static final RecipeTable<SimpleRecipeBuilder> MACERATOR_RECIPES = new RecipeTable<>("macerator", 1, 1, 1, 3, 0, 0, 0, 0, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> CHEMICAL_REACTOR_RECIPES = new RecipeTable<>("chemical_reactor", 0, 2, 0, 2, 0, 3, 0, 3, new SimpleRecipeBuilder().duration(20).EUt(2));
    public static final RecipeTable<SimpleRecipeBuilder> CLUSTER_MILL = new RecipeTable<>("cluster_mill", 1, 1, 1, 1, 0, 0, 0, 0, new SimpleRecipeBuilder().duration(20).EUt(2));
*/
    public static void init() {
    }
}
