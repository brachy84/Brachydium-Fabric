package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WorkableMetaBlockEntity extends TieredMetaBlockEntity {

    private final RecipeTable<?> recipeTable;
    private final AbstractRecipeLogic recipeLogic;

    public WorkableMetaBlockEntity(Identifier id, Voltages.Voltage tier, RecipeTable<?> recipeTable) {
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
        return recipeTable.createUITemplate(recipeLogic::getProgressPercent, builder, getImportItems(), getExportItems(), getImportFluids(), getExportFluids());
    }

    @Override
    public ArrayParticipant<ItemKey> createImportItemHandler() {
        return ItemInventory.importInventory(recipeTable.getMaxInputs());
    }

    @Override
    public ArrayParticipant<ItemKey> createExportItemHandler() {
        return ItemInventory.exportInventory(recipeTable.getMaxOutputs());
    }

    @Override
    public ArrayParticipant<Fluid> createImportFluidHandler() {
        return FluidTankList.importTanks(recipeTable.getMaxFluidInputs());
    }

    @Override
    public ArrayParticipant<Fluid> createExportFluidHandler() {
        return FluidTankList.exportTanks(recipeTable.getMaxFluidOutputs());
    }
}
