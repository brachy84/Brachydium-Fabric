package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.widgets.RootWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WorkableMetaBlockEntity extends TieredMetaBlockEntity {

    private final RecipeTable<?> recipeTable;
    private final AbstractRecipeLogic recipeLogic;

    public WorkableMetaBlockEntity(Identifier id, int tier, RecipeTable<?> recipeTable) {
        super(id, tier);
        Objects.requireNonNull(recipeTable);
        this.recipeTable = recipeTable;
        this.recipeLogic = new RecipeEnergyLogic(this, recipeTable, () -> energyContainer);
    }

    @Override
    public boolean hasUi() {
        return true;
    }

    @NotNull
    @Override
    public RootWidget.Builder createUITemplate(PlayerEntity player, RootWidget.Builder builder) {
        builder.bindPlayerInventory(player.inventory);
        return recipeTable.createUITemplate(builder, getImportItems(), getExportItems(), getImportFluids(), getExportFluids());
    }

    @Override
    public Inventory createImportItemHandler() {
        return new ItemInventory(recipeTable.getMaxInputs());
    }

    @Override
    public Inventory createExportItemHandler() {
        return new ItemInventory(recipeTable.getMaxOutputs());
    }

    @Override
    public IFluidInventory createImportFluidHandler() {
        return new FluidInventory(recipeTable.getMaxFluidInputs());
    }

    @Override
    public IFluidInventory createExportFluidHandler() {
        return new FluidInventory(recipeTable.getMaxFluidOutputs());
    }
}
