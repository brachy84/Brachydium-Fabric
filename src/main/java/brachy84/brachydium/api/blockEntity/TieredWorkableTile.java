package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.trait.AbstractRecipeLogic;
import brachy84.brachydium.api.blockEntity.trait.RecipeLogicEnergy;
import brachy84.brachydium.api.energy.IEnergyContainer;
import brachy84.brachydium.api.energy.Voltage;
import brachy84.brachydium.api.handlers.EnergyContainerHandler;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.render.TileRenderUtil;
import brachy84.brachydium.api.render.WorkableOverlayRenderer;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class TieredWorkableTile extends WorkableTileEntity implements ITiered {

    private final int tier;
    private final IEnergyContainer energyContainer;
    protected final WorkableOverlayRenderer overlayRenderer;

    public TieredWorkableTile(RecipeTable<?> recipeTable, WorkableOverlayRenderer renderer, int tier) {
        super(recipeTable);
        this.tier = tier;
        energyContainer = new EnergyContainerHandler(this, Voltage.get(tier) * 8, Voltage.get(tier), true);
        this.overlayRenderer = renderer;
    }

    @Override
    public @NotNull TileEntity createNewTileEntity() {
        return new TieredWorkableTile(getRecipeTable(), overlayRenderer, getTier());
    }

    @Override
    public void render(QuadEmitter emitter) {
        //Brachydium.LOGGER.info("render");
        TileRenderUtil.renderCube(emitter, Textures.MACHINECASING[tier].getSprite());
        overlayRenderer.render(emitter, getFrontFace(), isActive());
    }

    @Override
    protected @NotNull AbstractRecipeLogic createWorkable(RecipeTable<?> recipeTable) {
        return new RecipeLogicEnergy(this, recipeTable, () -> energyContainer);
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public @NotNull Gui createUi(PlayerEntity player) {
        Gui.Builder builder = Gui.defaultBuilder(player).bindPlayerInventory(EdgeInset.bottom(7), Alignment.BottomCenter);
        getRecipeTable().createUITemplate(() -> getWorkable().getProgressPercent(), builder, getImportInventory(), getExportInventory(), getImportFluidHandler(), getExportFluidHandler());
        return builder.build();
    }

    @Override
    public int getTier() {
        return tier;
    }
}
