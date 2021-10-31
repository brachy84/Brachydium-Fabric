package brachy84.testmod;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.recipe.RecipeTables;
import net.minecraft.util.Identifier;

public class MTBlockEntities {

    public static TileEntityGroup ALLOY_SMELTER;
    public static TileEntityGroup MIXER;

    public static void init() {
        ALLOY_SMELTER = BrachydiumApi.registerTileEntityGroup(new TileEntityGroup(id("alloy_smelter"),  SimpleMachine.createForRange(RecipeTables.ALLOYER_RECIPES,  0, 8)));
        MIXER =         BrachydiumApi.registerTileEntityGroup(new TileEntityGroup(id("mixer"),          SimpleMachine.createForRange(RecipeTables.MIXER_RECIPES,    0, 8)));
    }

    public static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
