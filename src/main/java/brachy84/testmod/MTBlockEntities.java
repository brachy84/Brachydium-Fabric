package brachy84.testmod;

import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.old.WorkableMetaBlockEntity;
import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.render.Textures;

import static brachy84.brachydium.Brachydium.*;

public class MTBlockEntities {

    public static WorkableMetaBlockEntity ALLOY_SMELTER;
    public static WorkableMetaBlockEntity MIXER;

    public static void init() {
        //MIXER = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(id("mixer"), Voltages.LV, Textures.MIXER, RecipeTables.MIXER_RECIPES));
        //ALLOY_SMELTER = BrachydiumApi.registerTileEntity(new WorkableMetaBlockEntity(id("alloy_smelter"), Voltages.LV, Textures.ALLOY_SMELTER, RecipeTables.ALLOYER_RECIPES));
    }
}
