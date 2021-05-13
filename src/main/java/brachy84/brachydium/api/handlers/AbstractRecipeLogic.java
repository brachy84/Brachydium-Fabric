package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.IWorkable;
import brachy84.brachydium.api.blockEntity.MBETrait;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.recipe.MTRecipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.minecraft.item.ItemStack;

import java.util.*;

public abstract class AbstractRecipeLogic extends MBETrait implements IWorkable {

    public final RecipeTable<?> recipeTable;

    protected MTRecipe lastRecipe;
    private MTRecipe currentRecipe;
    /**
     * A list of recipes which has items that are currently in the inventory
     */
    protected List<MTRecipe> possibleRecipes;
    protected boolean allowOverclocking = true;
    protected int progress, duration, recipeEUt;

    protected final Random random = new Random();

    protected boolean isActive = false;
    protected boolean workingEnabled = true;
    protected boolean hasNotEnoughEnergy = false;
    protected boolean outputBlocked = false;

    //private Transaction transaction;

    public AbstractRecipeLogic(MetaBlockEntity mbe, RecipeTable<?> recipeTable) {
        super(mbe);
        this.recipeTable = recipeTable;
    }

    @Override
    public void update() {
        super.update();
        if (isActive) {
            onRecipeTick();
        }
    }

    protected void onRecipeTick() {
        boolean energyDrawn = drawEnergy(recipeEUt);
        if (!energyDrawn) return;
        if (++progress > duration) {
            recipeCompleted();
        }
    }

    protected void recipeCompleted() {
        if (!insertOutput(currentRecipe.getOutputs(), currentRecipe.getFluidOutputs())) {
            Brachydium.LOGGER.error("Failed to insert recipe outputs");
        }
        currentRecipe = null;
        duration = 0;
        progress = 0;
        recipeEUt = 0;
        isActive = false;
        //transaction.commit(); // insert output
        //transaction = null;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    protected abstract long getEnergyStored();

    protected abstract long getEnergyCapacity();

    protected abstract boolean drawEnergy(long amount);

    /**
     * Called when machine inventory changes
     */
    protected void filterRecipes(ItemStack newStack) {

        //TODO: implement
    }

    protected void trySearchNewRecipe() {
        if (isActive) return;
        MTRecipe nextRecipe = null;

        //TODO: implement

        if (nextRecipe != null) {
            setupRecipe(nextRecipe);
        }
    }

    protected void setupRecipe(MTRecipe recipe) {
        //transaction = Transaction.openOuter();
        if (canInsertOutput(recipe.getOutputs(), recipe.getFluidOutputs())) {
            // check other stuff
            startRecipe(recipe);
        }
    }

    protected void startRecipe(MTRecipe recipe) {
        currentRecipe = recipe;
        progress = 0;
        duration = recipe.getDuration();
        recipeEUt = recipe.getEUt();
        isActive = true;
    }

    protected boolean canInsertOutput(List<ItemStack> outputItems, List<FluidStack> outputFluids) {
        //TODO: implement
        return true;
    }

    protected boolean insertOutput(List<ItemStack> outputItems, List<FluidStack> outputFluids) {
        int fails = 0;

        //TODO: implement

        return fails == 0;
    }
}
