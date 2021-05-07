package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityHolder;
import brachy84.brachydium.api.energy.IEnergyContainer;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

public class RecipeEnergyLogic extends AbstractRecipeLogic {

    Supplier<IEnergyContainer> energyContainer;

    public RecipeEnergyLogic(MetaBlockEntity mbe, RecipeTable<?> recipeTable, Supplier<IEnergyContainer> energyContainer) {
        super(mbe, recipeTable);
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
        return amount == energyContainer.get().removeEnergy(amount);
    }

    @Override
    public String getName() {
        return "RecipeEnergyLogic";
    }

    @Override
    public void addApis(BlockEntityType<MetaBlockEntityHolder> type) {
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Progress", progress);
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        progress = tag.getInt("Progress");
    }
}
