package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.gui_v1.BrachydiumGui;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.handlers.astrarre.FluidHandler;
import brachy84.brachydium.api.handlers.astrarre.IFluidHandler;
import brachy84.brachydium.api.handlers.astrarre.ItemHandler;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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

    /*@Override
    public @Nullable ModularGui createUi(PlayerInventory inventory) {
        Brachydium.LOGGER.info("Creating UI (WorkableBlockEntity)");
        ModularGui.Builder builder = recipeTable.createUITemplate(() -> 0, ItemHandler.of(importItems), ItemHandler.of(exportItems), FluidHandler.of(importFluids), FluidHandler.of(exportFluids));
        builder.bindPlayInventory(inventory);
        return builder.build();
    }*/

    /*@Override
    public BrachydiumGui.Builder buildUi(BrachydiumGui.Builder builder) {
        return recipeTable.createUITemplate(builder, getImportItems(), getExportItems(), getImportFluids(), getExportFluids());
    }*/

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
