package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.old.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.old.MetaBlockEntityHolder;
import brachy84.brachydium.api.energy.IEnergyContainer2;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class RecipeEnergyLogic extends AbstractRecipeLogic {

    Supplier<IEnergyContainer2> energyContainer;

    public RecipeEnergyLogic(TileEntity tile, RecipeTable<?> recipeTable, Supplier<IEnergyContainer2> energyContainer) {
        super(tile, recipeTable);
        this.energyContainer = energyContainer;
    }

    @Override
    protected long getEnergyStored() {
        return energyContainer.get().getStoredEnergy();
    }

    @Override
    protected long getEnergyCapacity() {
        return energyContainer.get().getEnergyCapacity();
    }

    @Override
    protected boolean drawEnergy(long amount) {
        return true;
        //return amount == energyContainer.get().removeEnergy(amount);
    }

    @Override
    public String getName() {
        return "RecipeEnergyLogic";
    }

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {
    }
}
