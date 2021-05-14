package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.api.recipe.RecipeTables;

import static brachy84.brachydium.Brachydium.*;

public class MTBlockEntities {

    public static WorkableMetaBlockEntity TEST_TILE;
    public static WorkableMetaBlockEntity ANOTHER_TILE;
    public static GuiTestBlockEntity GUI_TEST_TILE;

    public static void init() {
        TEST_TILE = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(id("test_tile"), Voltages.LV, RecipeTables.ALLOYER_RECIPES));
        ANOTHER_TILE = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(id("another_machine"), Voltages.LuV, RecipeTables.ALLOYER_RECIPES));
        GUI_TEST_TILE = BrachydiumApi.registerTileEntity(new GuiTestBlockEntity(id("gui_test_tile")));
    }
}
