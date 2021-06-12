package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.handlers.AbstractRecipeLogic;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class WorkableTileEntity extends TileEntity {

    private final AbstractRecipeLogic workable;

    public WorkableTileEntity(@NotNull Identifier id) {
        super(id);
        this.workable = createWorkable();
    }

    @NotNull
    protected abstract AbstractRecipeLogic createWorkable();

    public AbstractRecipeLogic getWorkable() {
        return workable;
    }

    public RecipeTable<?> getRecipeTable() {
        return workable.recipeTable;
    }

}
