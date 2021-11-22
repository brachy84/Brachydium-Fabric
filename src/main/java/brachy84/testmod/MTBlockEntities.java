package brachy84.testmod;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.TileEntityGroup;
import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.render.WorkableOverlayRenderer;
import brachy84.brachydium.api.util.Face;
import net.minecraft.util.Identifier;

public class MTBlockEntities {

    public static TileEntityGroup ALLOY_SMELTER;
    public static TileEntityGroup MIXER;

    public static WorkableOverlayRenderer ALLOY_SMELTER_OVERLAY;
    public static WorkableOverlayRenderer MIXER_OVERLAY;

    public static void init() {
        ALLOY_SMELTER_OVERLAY = new WorkableOverlayRenderer("alloy_smelter", Face.FRONT);
        MIXER_OVERLAY = new WorkableOverlayRenderer("mixer", Face.FRONT, Face.SIDE);

        ALLOY_SMELTER = BrachydiumApi.registerTileEntityGroup(new TileEntityGroup(id("alloy_smelter"), SimpleMachine.createForRange(RecipeTables.ALLOYER_RECIPES, ALLOY_SMELTER_OVERLAY, 0, 8)));
        MIXER = BrachydiumApi.registerTileEntityGroup(new TileEntityGroup(id("mixer"), SimpleMachine.createForRange(RecipeTables.MIXER_RECIPES, MIXER_OVERLAY, 0, 8)));
    }

    public static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
