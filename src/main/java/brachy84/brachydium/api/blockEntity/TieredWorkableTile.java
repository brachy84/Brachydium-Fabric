package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.IEnergyContainer2;
import brachy84.brachydium.api.handlers.AbstractRecipeLogic;
import brachy84.brachydium.api.handlers.EnergyContainer2Handler;
import brachy84.brachydium.api.handlers.RecipeEnergyLogic;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.widgets.RootWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TieredWorkableTile extends  WorkableTileEntity implements ITiered {

    private final int tier;
    private IEnergyContainer2 energyContainer;

    public TieredWorkableTile(RecipeTable<?> recipeTable, int tier) {
        super(recipeTable);
        this.tier = tier;
        energyContainer = new EnergyContainer2Handler(this, (long) Math.pow(2, tier) * 8, (long) Math.pow(2, tier), true);
    }

    @Override
    public RenderTrait createBaseRenderer() {
        return new RenderTrait(this, Textures.MACHINECASING[tier]);
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
    public @NotNull ModularGui createUi(PlayerEntity player) {
        RootWidget.Builder builder = RootWidget.builder();
        getRecipeTable().createUITemplate(() -> getWorkable().getProgressPercent(), builder, getInventories().getImportItems(), getInventories().getExportItems(), getInventories().getImportFluids(), getInventories().getExportFluids());
        return new ModularGui(builder.build(), getHolder(), player);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public ItemStack asStack() {
        ItemStack item = new ItemStack(asItem());
        ((IntBlockEntityGroup)getGroup()).writeNbt(item.getOrCreateTag(), tier);
        return item;
    }
}
