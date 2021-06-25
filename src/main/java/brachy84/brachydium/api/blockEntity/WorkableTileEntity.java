package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.handlers.AbstractRecipeLogic;
import brachy84.brachydium.api.handlers.RecipeEnergyLogic;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class WorkableTileEntity extends TileEntity {

    private final AbstractRecipeLogic workable;

    public WorkableTileEntity(RecipeTable<?> recipeTable) {
        this.workable = createWorkable(recipeTable);
    }

    @Override
    public InventoryHolder createInventories() {
        return new InventoryHolder(this, getRecipeTable().getMaxInputs(), getRecipeTable().getMaxOutputs(), getRecipeTable().getMaxFluidInputs(), getRecipeTable().getMaxFluidOutputs());
    }

    @NotNull
    protected abstract AbstractRecipeLogic createWorkable(RecipeTable<?> recipeTable);

    public AbstractRecipeLogic getWorkable() {
        return workable;
    }

    public RecipeTable<?> getRecipeTable() {
        return workable.recipeTable;
    }

    @Override
    public boolean isActive() {
        return workable.isActive();
    }
}
