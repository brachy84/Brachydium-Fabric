package brachy84.testmod;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.BrachydiumApi;
import brachy84.brachydium.api.blockEntity.IntBlockEntityGroup;
import brachy84.brachydium.api.blockEntity.TieredBlockEntityGroup;
import brachy84.brachydium.api.blockEntity.TieredWorkableTile;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.recipe.RecipeTables;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class MTBlockEntities {

    public static TieredBlockEntityGroup ALLOY_SMELTER;
    public static TieredBlockEntityGroup MIXER;

    public static void init() {
        MIXER = createTieredWorkables(id("mixer"), RecipeTables.MIXER_RECIPES, 2, 2);
        ALLOY_SMELTER = createTieredWorkables(id("alloy_smelter"), RecipeTables.ALLOYER_RECIPES, 3, 5);
    }

    public static TieredBlockEntityGroup createTieredWorkables(Identifier id, RecipeTable<?> recipeTable, int min, int max) {
        return createTieredWorkables(id, recipeTable, IntStream.rangeClosed(min, max).toArray());
    }

    public static TieredBlockEntityGroup createTieredWorkables(Identifier id, RecipeTable<?> recipeTable, int... tiers) {
        Map<Integer, TileEntity> map = new HashMap<>();
        for (int i = 0; i < tiers.length; i++) {
            map.put(tiers[i], new TieredWorkableTile(recipeTable, tiers[i]));
        }
        return BrachydiumApi.registerBlockEntityGroup(new TieredBlockEntityGroup(id, map));
    }

    public static Identifier id(String path) {
        return Brachydium.id(path);
    }
}
