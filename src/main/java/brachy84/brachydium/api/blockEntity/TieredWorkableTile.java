package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.blockEntity.trait.AbstractRecipeLogic;
import brachy84.brachydium.api.blockEntity.trait.RecipeEnergyLogic;
import brachy84.brachydium.api.blockEntity.trait.TileEntityRenderer;
import brachy84.brachydium.api.energy.IEnergyContainer2;
import brachy84.brachydium.api.handlers.EnergyContainer2Handler;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.internal.Gui;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class TieredWorkableTile extends WorkableTileEntity implements ITiered {

    private final int tier;
    private final IEnergyContainer2 energyContainer;

    public TieredWorkableTile(RecipeTable<?> recipeTable, int tier) {
        super(recipeTable);
        this.tier = tier;
        energyContainer = new EnergyContainer2Handler(this, (long) Math.pow(2, tier) * 8, (long) Math.pow(2, tier), true);
    }

    /*@Override
    public TileEntity createCopy(BlockEntityHolder holder) {
        return new TieredWorkableTile(getRecipeTable(), tier);
    }*/

    @Override
    public @NotNull TileEntityFactory<?> createFactory() {
        return new TileEntityFactory<>(this, tile -> new TieredWorkableTile(tile.getRecipeTable(), tile.getTier()));
    }

    @Override
    public TileEntityRenderer createBaseRenderer() {
        return TileEntityRenderer.create(this, Textures.MACHINECASING[tier]);
    }

    @Override
    protected @NotNull AbstractRecipeLogic createWorkable(RecipeTable<?> recipeTable) {
        return new RecipeEnergyLogic(this, recipeTable, () -> energyContainer);
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
