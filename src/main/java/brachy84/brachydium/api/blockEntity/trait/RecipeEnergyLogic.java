package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.ITiered;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.energy.IEnergyContainer;
import brachy84.brachydium.api.energy.Voltage;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class RecipeEnergyLogic extends AbstractRecipeLogic {

    Supplier<IEnergyContainer> energyContainer;

    public RecipeEnergyLogic(TileEntity tile, RecipeTable<?> recipeTable, Supplier<IEnergyContainer> energyContainer) {
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
    protected long getMachineVoltage() {
        return tile instanceof ITiered ? Voltage.VALUES[((ITiered) tile).getTier()].voltage : 0;
    }

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {
    }
}
