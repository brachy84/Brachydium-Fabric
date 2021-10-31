package brachy84.testmod;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.TieredWorkableTile;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.trait.TileEntityRenderer;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.CycableTexture;
import brachy84.brachydium.api.render.Textures;
import org.jetbrains.annotations.NotNull;

public class SimpleMachine extends TieredWorkableTile {

    public static SimpleMachine[] createForRange(RecipeTable<?> recipeTable, int min, int max) {
        min = Math.max(min, 0);
        max = Math.max(max, 0);
        if(max < min) throw new IllegalArgumentException("Max can't be smaller than Min");
        SimpleMachine[] machines = new SimpleMachine[max - min + 1];
        for(int i = 0; i < machines.length; i++)
            machines[i] = new SimpleMachine(recipeTable, i + min);
        return machines;
    }

    public SimpleMachine(RecipeTable<?> recipeTable, int tier) {
        super(recipeTable, tier);
    }

    @Override
    public @NotNull TileEntity createNewTileEntity() {
        return new SimpleMachine(getRecipeTable(), getTier());
    }

    @Override
    public TileEntityRenderer createBaseRenderer() {
        return TileEntityRenderer.create(this, Textures.MACHINECASING[getTier()])
                .addFrontOverlay(CycableTexture.createWorkableTexture(this::isActive, "front", Brachydium.id("block/machines/" + getRecipeTable().unlocalizedName)));
    }
}
