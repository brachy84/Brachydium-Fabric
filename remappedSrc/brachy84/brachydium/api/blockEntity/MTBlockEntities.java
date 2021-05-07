package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.recipe.RecipeTables;


public class MTBlockEntities {

    public static WorkableMetaBlockEntity TEST_TILE;
    public static WorkableMetaBlockEntity ANOTHER_TILE;

    public static void init() {
        TEST_TILE = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(Brachydium.id("test_tile"), 2, RecipeTables.ALLOYER_RECIPES));
        ANOTHER_TILE = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(Brachydium.id("another_machine"), 6, RecipeTables.ALLOYER_RECIPES));
    }
}
