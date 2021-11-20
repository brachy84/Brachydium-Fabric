package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.ByValues;
import brachy84.brachydium.api.blockEntity.IWorkable;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.energy.Voltage;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.BrachydiumLookups;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.recipe.MatchingMode;
import brachy84.brachydium.api.recipe.Recipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import brachy84.brachydium.api.util.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.LongSupplier;

public abstract class AbstractRecipeLogic extends TileTrait implements IWorkable {

    private static final String ALLOW_OVERCLOCKING = "AllowOverclocking";
    private static final String OVERCLOCK_VOLTAGE = "OverclockVoltage";

    public final RecipeTable<?> recipeTable;

    protected Recipe previousRecipe;
    protected boolean allowOverclocking = false;
    private long overclockVoltage = 0;
    private LongSupplier overclockPolicy = this::getMaxVoltage;

    protected int progressTime;
    protected int maxProgressTime;
    protected int recipeEUt;
    protected List<FluidStack> fluidOutputs;
    protected List<ItemStack> itemOutputs;
    protected final Random random = new Random();

    protected boolean isActive;
    protected boolean workingEnabled = true;
    protected boolean hasNotEnoughEnergy;
    protected boolean wasActiveAndNeedsUpdate;
    protected boolean isOutputsFull;
    protected boolean invalidInputsForRecipes;

    protected boolean hasPerfectOC = false;

    public AbstractRecipeLogic(TileEntity tileEntity, RecipeTable<?> recipeTable) {
        super(tileEntity);
        this.recipeTable = recipeTable;
    }

    public AbstractRecipeLogic(TileEntity tileEntity, RecipeTable<?> recipeTable, boolean hasPerfectOC) {
        super(tileEntity);
        this.recipeTable = recipeTable;
        this.hasPerfectOC = hasPerfectOC;
    }

    protected abstract long getEnergyStored();

    protected abstract long getEnergyCapacity();

    protected abstract boolean drawEnergy(int recipeEUt);

    protected abstract long getMaxVoltage();

    protected Storage<ItemVariant> getInputInventory() {
        return tile.getImportItemStorage();
    }

    protected Storage<ItemVariant> getOutputInventory() {
        return tile.getExportItemStorage();
    }

    protected Storage<FluidVariant> getInputTank() {
        return tile.getImportFluidStorage();
    }

    protected Storage<FluidVariant> getOutputTank() {
        return tile.getExportFluidStorage();
    }

    @Override
    public void tick() {
        World world = tile.getWorld();
        if (world != null) {
            if(!world.isClient) {
                if (workingEnabled) {
                    if (progressTime > 0) {
                        updateRecipeProgress();
                    }
                    //check everything that would make a recipe never start here.
                    if (progressTime == 0 && shouldSearchForRecipes()) {
                        Brachydium.LOGGER.info("Searching new recipe");
                        trySearchNewRecipe();
                    }
                }
                if (wasActiveAndNeedsUpdate) {
                    this.wasActiveAndNeedsUpdate = false;
                    setActive(false);
                }
            } else {

            }
        }
    }

    @Override
    public void registerApis() {
        registerApi(BrachydiumLookups.WORKABLE, this);
        registerApi(BrachydiumLookups.CONTROLLABLE, this);
    }

    protected boolean shouldSearchForRecipes() {
        return canWorkWithInputs() && canFitNewOutputs();
    }

    protected boolean hasNotifiedInputs() {
        return (tile.getNotifiedItemInputList().size() > 0 ||
                tile.getNotifiedFluidInputList().size() > 0);
    }

    protected boolean hasNotifiedOutputs() {
        return (tile.getNotifiedItemOutputList().size() > 0 ||
                tile.getNotifiedFluidOutputList().size() > 0);
    }

    protected boolean canFitNewOutputs() {
        // if the output is full check if the output changed so we can process recipes results again.
        if (this.isOutputsFull && !hasNotifiedOutputs()) return false;
        else {
            this.isOutputsFull = false;
            tile.getNotifiedItemOutputList().clear();
            tile.getNotifiedFluidOutputList().clear();
        }
        return true;
    }

    protected boolean canWorkWithInputs() {
        // if the inputs were bad last time, check if they've changed before trying to find a new recipe.
        if (this.invalidInputsForRecipes && !hasNotifiedInputs())
            return false;
        else {
            this.invalidInputsForRecipes = false;
        }
        return true;
    }

    protected void updateRecipeProgress() {
        boolean drawEnergy = drawEnergy(recipeEUt);
        if (drawEnergy || (recipeEUt < 0)) {
            //as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                Brachydium.LOGGER.info("Recipe done");
                completeRecipe();
            }
        } else if (recipeEUt > 0) {
            //only set hasNotEnoughEnergy if this recipe is consuming recipe
            //generators always have enough energy
            this.hasNotEnoughEnergy = true;
            //if current progress value is greater than 2, decrement it by 2
            if (progressTime >= 2) {
                if (false/*TODO ConfigHolder.insufficientEnergySupplyWipesRecipeProgress*/) {
                    this.progressTime = 1;
                } else {
                    this.progressTime = Math.max(1, progressTime - 2);
                }
            }
        }
    }

    protected void trySearchNewRecipe() {
        long maxVoltage = getMaxVoltage();
        Recipe currentRecipe = null;
        Storage<ItemVariant> importInventory = getInputInventory();
        Storage<FluidVariant> importFluids = getInputTank();

        // see if the last recipe we used still works
        if (this.previousRecipe != null && canTakeInputs(previousRecipe, true))//this.previousRecipe.matches(false, importInventory, importFluids))
            currentRecipe = this.previousRecipe;
            // If there is no active recipe, then we need to find one.
        else {
            currentRecipe = findRecipe(maxVoltage, importInventory, importFluids, MatchingMode.DEFAULT);
        }
        // If a recipe was found, then inputs were valid. Cache found recipe.
        if (currentRecipe != null) {
            this.previousRecipe = currentRecipe;
        }
        this.invalidInputsForRecipes = (currentRecipe == null);

        // proceed if we have a usable recipe.
        if (currentRecipe != null && setupAndConsumeRecipeInputs(currentRecipe, importInventory)) {
            Brachydium.LOGGER.info(" - found recipe");
            setupRecipe(currentRecipe);
        }
        // Inputs have been inspected.
        tile.getNotifiedItemInputList().clear();
        tile.getNotifiedFluidInputList().clear();
    }

    protected static int getMinTankCapacity(IFluidHandler tanks) {
        if (tanks.size() == 0) {
            return 0;
        }
        int result = Integer.MAX_VALUE;
        for (int i = 0; i < tanks.size(); i++) {
            result = (int) Math.min(tanks.getCapacityAt(i), result);
        }
        return result;
    }

    protected Recipe findRecipe(long maxVoltage, Storage<ItemVariant> inputs, Storage<FluidVariant> fluidInputs, MatchingMode mode) {
        return recipeTable.findRecipe(maxVoltage, inputs, fluidInputs, getMinTankCapacity(tile.getExportFluidHandler()), mode);
    }

    protected static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return (stackA.isEmpty() && stackB.isEmpty()) ||
                (ItemStack.areItemsEqual(stackA, stackB) &&
                        ItemStack.areNbtEqual(stackA, stackB));
    }

    /**
     * Determines if the provided recipe is possible to run from the provided inventory, or if there is anything preventing
     * the Recipe from being completed.
     * <p>
     * Will consume the inputs of the Recipe if it is possible to run.
     *
     * @param recipe          - The Recipe that will be consumed from the inputs and ran in the machine
     * @param importInventory - The inventory that the recipe should be consumed from.
     *                        Used mainly for Distinct bus implementation for multiblocks to specify
     *                        a specific bus
     * @return - true if the recipe is successful, false if the recipe is not successful
     */
    protected boolean setupAndConsumeRecipeInputs(Recipe recipe, Storage<ItemVariant> importInventory) {
        int[] resultOverclock = calculateOverclock(recipe.getEUt(), recipe.getDuration());
        int totalEUt = resultOverclock[0] * resultOverclock[1];
        Storage<ItemVariant> exportInventory = getOutputInventory();
        Storage<FluidVariant> importFluids = getInputTank();
        Storage<FluidVariant> exportFluids = getOutputTank();
        long energyStored = getEnergyStored();
        long capacity = getEnergyCapacity();
        if (!(totalEUt >= 0 ? energyStored >= (totalEUt > capacity / 2 ? resultOverclock[0] : totalEUt) :
                (energyStored - resultOverclock[0] <= capacity))) {
            return false;
        }
        if (!TransferUtil.putItems(exportInventory, recipe.getAllItemOutputs(tile.getExportInventory().size()), true, true)) {
            this.isOutputsFull = true;
            return false;
        }
        if (!TransferUtil.putFluids(exportFluids, recipe.getFluidOutputs(), true, true)) {
            this.isOutputsFull = true;
            return false;
        }
        this.isOutputsFull = false;
        return canTakeInputs(recipe, false);//recipe.matches(true, importInventory, importFluids);
    }

    protected boolean canTakeInputs(Recipe recipe, boolean simulate) {
        if(recipe.getInputs().size() > 0) {
            Storage<ItemVariant> itemStorage = getInputInventory();
            if(!TransferUtil.takeItems(itemStorage, recipe.getInputs(), simulate))
                return false;
        }
        if(recipe.getFluidInputs().size() > 0) {
            Storage<FluidVariant> fluidStorage = getInputTank();
            return TransferUtil.takeFluids(fluidStorage, recipe.getFluidInputs(), simulate);
        }
        return true;
    }

    protected int[] calculateOverclock(int EUt, int duration) {
        return calculateOverclock(EUt, this.overclockPolicy.getAsLong(), duration);
    }

    protected int[] calculateOverclock(int EUt, long voltage, int duration) {
        if (!allowOverclocking) {
            return new int[]{EUt, duration};
        }
        boolean negativeEU = EUt < 0;
        int tier = getOverclockingTier(voltage);

        // Cannot overclock
        if (ByValues.V[tier] <= EUt || tier == 0)
            return new int[]{EUt, duration};

        if (negativeEU)
            EUt = -EUt;

        int resultEUt = EUt;
        double resultDuration = duration;
        double divisor = hasPerfectOC ? 4.0 : 2.5; //TODO ConfigHolder.U.overclockDivisor;
        int maxOverclocks = tier - 1; // exclude ULV overclocking

        //do not overclock further if duration is already too small
        while (resultDuration >= 3 && resultEUt <= ByValues.V[tier - 1] && maxOverclocks != 0) {
            resultEUt *= 4;
            resultDuration /= divisor;
            maxOverclocks--;
        }
        return new int[]{negativeEU ? -resultEUt : resultEUt, (int) Math.ceil(resultDuration)};
    }

    protected int getOverclockingTier(long voltage) {
        return Voltage.getByVoltage(voltage).tier;
    }

    protected long getVoltageByTier(final int tier) {
        return ByValues.V[tier];
    }

    public String[] getAvailableOverclockingTiers() {
        final int maxTier = getOverclockingTier(getMaxVoltage());
        final String[] result = new String[maxTier + 1];
        result[0] = "gregtech.gui.overclock.off";
        if (maxTier >= 0) System.arraycopy(ByValues.VN, 1, result, 1, maxTier);
        return result;
    }

    protected void setupRecipe(Recipe recipe) {
        int[] resultOverclock = calculateOverclock(recipe.getEUt(), recipe.getDuration());
        this.progressTime = 1;
        setMaxProgress(resultOverclock[1]);
        this.recipeEUt = resultOverclock[0];
        this.fluidOutputs = TransferUtil.copyFluidList(recipe.getFluidOutputs());
        int tier = getMachineTierForRecipe(recipe);
        this.itemOutputs = TransferUtil.copyStackList(recipe.getResultItemOutputs(tile.getExportInventory().size(), random, tier));
        if (this.wasActiveAndNeedsUpdate) {
            this.wasActiveAndNeedsUpdate = false;
        } else {
            this.setActive(true);
        }
    }

    protected int getMachineTierForRecipe(Recipe recipe) {
        return Voltage.getByVoltage(getMaxVoltage()).tier;
    }

    protected void completeRecipe() {
        TransferUtil.putItems(getOutputInventory(), itemOutputs, false, false);
        TransferUtil.putFluids(getOutputTank(), fluidOutputs, false, false);
        this.progressTime = 0;
        setMaxProgress(0);
        this.recipeEUt = 0;
        this.fluidOutputs = null;
        this.itemOutputs = null;
        this.hasNotEnoughEnergy = false;
        this.wasActiveAndNeedsUpdate = true;
    }

    public double getProgressPercent() {
        return getDuration() == 0 ? 0.0 : getProgress() / (getDuration() * 1.0);
    }

    public int getTicksTimeLeft() {
        return maxProgressTime == 0 ? 0 : (maxProgressTime - progressTime);
    }

    @Override
    public int getProgress() {
        return progressTime;
    }

    @Override
    public int getDuration() {
        return maxProgressTime;
    }

    public int getRecipeEUt() {
        return recipeEUt;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgressTime = maxProgress;
        tile.markDirty();
    }

    protected void setActive(boolean active) {
        this.isActive = active;
        tile.markDirty();
        World world = tile.getWorld();
        if (world != null && !world.isClient) {
            syncCustomData(1, buf -> buf.writeBoolean(active));
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        tile.markDirty();
    }

    public void setAllowOverclocking(boolean allowOverclocking) {
        this.allowOverclocking = allowOverclocking;
        this.overclockVoltage = allowOverclocking ? getMaxVoltage() : 0;
        tile.markDirty();
    }

    public boolean isHasNotEnoughEnergy() {
        return hasNotEnoughEnergy;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public boolean isAllowOverclocking() {
        return allowOverclocking;
    }

    public long getOverclockVoltage() {
        return overclockVoltage;
    }

    public void setOverclockVoltage(final long overclockVoltage) {
        this.overclockPolicy = this::getOverclockVoltage;
        this.overclockVoltage = overclockVoltage;
        this.allowOverclocking = (overclockVoltage != 0);
        tile.markDirty();
    }

    /**
     * Sets the overclocking policy to use getOverclockVoltage() instead of getMaxVoltage()
     * and initialises the overclock voltage to max voltage.
     * The actual value will come from the saved tag when the tile is loaded for pre-existing machines.
     * <p>
     * NOTE: This should only be used directly after construction of the workable.
     * Use setOverclockVoltage() or setOverclockTier() for a more dynamic use case.
     */
    public void enableOverclockVoltage() {
        setOverclockVoltage(getMaxVoltage());
    }

    public int getOverclockTier() {
        if (this.overclockVoltage == 0) {
            return 0;
        }
        return getOverclockingTier(this.overclockVoltage);
    }

    public void setOverclockTier(final int tier) {
        if (tier == 0) {
            setOverclockVoltage(0);
            return;
        }
        setOverclockVoltage(getVoltageByTier(tier));
    }

    @Override
    public void readCustomData(int id, PacketByteBuf buf) {
        if (id == 1) {
            this.isActive = buf.readBoolean();
            tile.scheduleRenderUpdate();
        }
    }

    @Override
    public void writeInitialData(PacketByteBuf buf) {
        buf.writeBoolean(this.isActive);
    }

    @Override
    public void receiveInitialData(PacketByteBuf buf) {
        this.isActive = buf.readBoolean();
    }

    @Override
    public NbtCompound serializeNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("WorkEnabled", workingEnabled);
        compound.putBoolean(ALLOW_OVERCLOCKING, allowOverclocking);
        compound.putLong(OVERCLOCK_VOLTAGE, this.overclockVoltage);
        if (progressTime > 0) {
            compound.putInt("Progress", progressTime);
            compound.putInt("MaxProgress", maxProgressTime);
            compound.putInt("RecipeEUt", this.recipeEUt);
            NbtList itemOutputsList = new NbtList();
            for (ItemStack itemOutput : itemOutputs) {
                itemOutputsList.add(itemOutput.writeNbt(new NbtCompound()));
            }
            NbtList fluidOutputsList = new NbtList();
            for (FluidStack fluidOutput : fluidOutputs) {
                fluidOutputsList.add(fluidOutput.writeNbt(new NbtCompound()));
            }
            compound.put("ItemOutputs", itemOutputsList);
            compound.put("FluidOutputs", fluidOutputsList);
        }
        return compound;
    }

    @Override
    public void deserializeNbt(NbtCompound compound) {
        this.workingEnabled = compound.getBoolean("WorkEnabled");
        this.progressTime = compound.getInt("Progress");
        if (compound.contains(ALLOW_OVERCLOCKING)) {
            this.allowOverclocking = compound.getBoolean(ALLOW_OVERCLOCKING);
        }
        if (compound.contains(OVERCLOCK_VOLTAGE)) {
            this.overclockVoltage = compound.getLong(OVERCLOCK_VOLTAGE);
        } else {
            // Calculate overclock voltage based on old allow flag
            this.overclockVoltage = this.allowOverclocking ? getMaxVoltage() : 0;
        }
        this.isActive = false;
        if (progressTime > 0) {
            this.isActive = true;
            this.maxProgressTime = compound.getInt("MaxProgress");
            this.recipeEUt = compound.getInt("RecipeEUt");
            NbtList itemOutputsList = compound.getList("ItemOutputs", NbtElement.COMPOUND_TYPE);
            this.itemOutputs = new ArrayList<>();
            for (int i = 0; i < itemOutputsList.size(); i++) {
                this.itemOutputs.add(ItemStack.fromNbt(itemOutputsList.getCompound(i)));
            }
            NbtList fluidOutputsList = compound.getList("FluidOutputs", NbtElement.COMPOUND_TYPE);
            this.fluidOutputs = new ArrayList<>();
            for (int i = 0; i < fluidOutputsList.size(); i++) {
                this.fluidOutputs.add(FluidStack.fromNbt(fluidOutputsList.getCompound(i)));
            }
        }
    }

}
