package brachy84.testmod;

import brachy84.brachydium.api.blockEntity.TieredWorkableTile;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.WorkableOverlayRenderer;
import org.jetbrains.annotations.NotNull;

public class SimpleMachine extends TieredWorkableTile {

    public SimpleMachine(RecipeTable<?> recipeTable, WorkableOverlayRenderer renderer, int tier) {
        super(recipeTable, renderer, tier);
    }

    public static SimpleMachine[] createForRange(RecipeTable<?> recipeTable, WorkableOverlayRenderer renderer, int min, int max) {
        min = Math.max(min, 0);
        max = Math.max(max, 0);
        if (max < min) throw new IllegalArgumentException("Max can't be smaller than Min");
        SimpleMachine[] machines = new SimpleMachine[max - min + 1];
        for (int i = 0; i < machines.length; i++)
            machines[i] = new SimpleMachine(recipeTable, renderer, i + min);
        return machines;
    }

    @Override
    public @NotNull TileEntity createNewTileEntity() {
        return new SimpleMachine(getRecipeTable(), overlayRenderer, getTier());
    }

}
