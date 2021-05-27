package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.api.handlers.*;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.MachineOverlayRenderer;
import brachy84.brachydium.api.render.OrientedOverlayRenderer;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WorkableMetaBlockEntity extends TieredMetaBlockEntity {

    private final RecipeTable<?> recipeTable;
    private final AbstractRecipeLogic recipeLogic;
    private final OrientedOverlayRenderer overlayRenderer;

    public WorkableMetaBlockEntity(Identifier id, Voltages.Voltage tier, OrientedOverlayRenderer renderer, RecipeTable<?> recipeTable) {
        super(id, tier);
        Objects.requireNonNull(recipeTable);
        this.recipeTable = recipeTable;
        this.recipeLogic = createWorkable(recipeTable);
        this.overlayRenderer = renderer;
        reinitializeInventories();
    }

    @Override
    public MetaBlockEntity recreate() {
        return new WorkableMetaBlockEntity(id, getVoltage(), overlayRenderer, recipeTable);
    }

    public AbstractRecipeLogic createWorkable(RecipeTable<?> recipeTable) {
        return new RecipeEnergyLogic(this, recipeTable, () -> energyContainer);
    }

    @Override
    public void render(QuadEmitter emitter) {
        super.render(emitter);
        if(overlayRenderer instanceof MachineOverlayRenderer) {
            ((MachineOverlayRenderer) overlayRenderer).render(emitter, getFrontFacing(), recipeLogic.isActive());
        } else {
            overlayRenderer.render(emitter, getFrontFacing());
        }
    }

    @Override
    public void reinitializeInventories() {
        if(recipeLogic == null) return;
        super.reinitializeInventories();
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
