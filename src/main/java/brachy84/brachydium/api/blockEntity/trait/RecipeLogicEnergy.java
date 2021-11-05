package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.energy.IEnergyContainer;
import brachy84.brachydium.api.recipe.RecipeTable;

import java.util.function.Supplier;

public class RecipeLogicEnergy extends AbstractRecipeLogic {

    private final Supplier<IEnergyContainer> energyContainer;

    public RecipeLogicEnergy(TileEntity tileEntity, RecipeTable<?> recipeTable, Supplier<IEnergyContainer> energyContainer) {
        super(tileEntity, recipeTable);
        this.energyContainer = energyContainer;
    }

    @Override
    protected long getEnergyStored() {
        return getEnergyCapacity();//energyContainer.get().getStoredEnergy();
    }

    @Override
    protected long getEnergyCapacity() {
        return energyContainer.get().getEnergyCapacity();
    }

    @Override
    protected boolean drawEnergy(int recipeEUt) {
        return true;
        /* TODO implement energy transfer
        long resultEnergy = getEnergyStored() - recipeEUt;
        if (resultEnergy >= 0L && resultEnergy <= getEnergyCapacity()) {
            energyContainer.get().changeEnergy(-recipeEUt);
            return true;
        } else return false;*/
    }

    @Override
    protected long getMaxVoltage() {
        return Math.max(energyContainer.get().getInputVoltage(),
                energyContainer.get().getOutputVoltage());
    }

}
