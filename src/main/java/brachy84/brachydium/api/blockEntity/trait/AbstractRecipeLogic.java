package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.*;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.InventoryHelper;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.network.Channels;
import brachy84.brachydium.api.recipe.Recipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * This class handles finding and running recipes consuming inputs and energy and inserting outputs
 * Energy can be any form of energy like normal "Redstone Flux", but also something like steam or mana
 */
public abstract class AbstractRecipeLogic extends TileTrait implements IWorkable {

    public final RecipeTable<?> recipeTable;

    protected Recipe lastRecipe;

    /**
     * currently running recipe
     */
    private Recipe currentRecipe;

    /**
     * This is the recipe that gets stored when the ingredients are found but the output is blocked
     */
    private Recipe storedRecipe;

    /**
     * A collection of recipes which has items that are currently in the inventory
     */
    protected Collection<Recipe> possibleRecipes;
    protected boolean allowOverclocking = true;
    protected int progress, duration, recipeEUt;

    protected final Random random = new Random();

    protected boolean workingEnabled = true;

    private State state;

    public AbstractRecipeLogic(TileEntity tile, RecipeTable<?> recipeTable) {
        super(tile);
        this.recipeTable = recipeTable;
        possibleRecipes = recipeTable.getRecipeList();
        state = State.IDLING;
    }

    @Override
    public void tick() {
        if (!tile.isClient()) {
            if (isActive()) {
                onRecipeTick();
            }
        }
    }

    @Override
    public void init() {
        addListeners();
    }

    private void addListeners() {
        if (tile.getImportFluidHandler() instanceof InventoryListener) {
            ((InventoryListener) tile.getImportFluidHandler()).addListener(this::onInventoryUpdate);
        }
        if (tile.getImportInventory() instanceof InventoryListener) {
            ((InventoryListener) tile.getImportInventory()).addListener(this::onInventoryUpdate);
        }
        if (tile.getExportInventory() instanceof InventoryListener) {
            ((InventoryListener) tile.getExportInventory()).addListener(this::onOutputChanged);
        }
        if (tile.getExportFluidHandler() instanceof InventoryListener) {
            ((InventoryListener) tile.getExportFluidHandler()).addListener(this::onOutputChanged);
        }
    }

    protected void onRecipeTick() {
        if (!drawEnergy(recipeEUt)) {
            setState(State.NOT_ENOUGH_POWER);
            return;
        }
        if (state == State.NOT_ENOUGH_POWER) {
            setState(State.RUNNING);
        }
        if (++progress > duration) {
            recipeCompleted();
        }
    }

    protected void recipeCompleted() {
        Brachydium.LOGGER.info("Completing recipe");
        Transaction transaction = Transaction.openOuter();
        if (!tryInsertOutput(transaction, currentRecipe)) {
            Brachydium.LOGGER.error("Failed to insert recipe outputs");
        }
        transaction.commit();
        lastRecipe = currentRecipe;
        currentRecipe = null;
        duration = 0;
        progress = 0;
        recipeEUt = 0;
        setState(State.IDLING);
        trySearchNewRecipe();
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        if (workingEnabled)
            setState(State.IDLING); // FIXME: this might cause issues
        else
            setState(State.DISABLED);
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
        return state == State.RUNNING;
    }

    protected abstract long getEnergyStored();

    protected abstract long getEnergyCapacity();

    protected abstract boolean drawEnergy(long amount);

    protected abstract long getMachineVoltage();

    public void setState(State state) {
        this.state = state;
        if (!tile.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(tile.getPos());
            buf.writeString(state.toString());
            for (PlayerEntity player : tile.getWorld().getPlayers()) {
                BlockPos pos = tile.getPos();
                if (player instanceof ServerPlayerEntity && tile.getWorld().isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 64)) {
                    ServerPlayNetworking.send((ServerPlayerEntity) player, Channels.UPDATE_WORKING_STATE, buf);
                }
            }
        }
    }

    public void setState(String state) {
        setState(State.valueOf(state));
    }

    public State getState() {
        return state;
    }

    /**
     * @return a double between 0 and 1
     */
    public double getProgressPercent() {
        if (!isActive()) return 0;
        return progress / (double) duration;
    }

    protected void trySearchNewRecipe() {
        Brachydium.LOGGER.info("searching recipe");
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < tile.getImportInventory().size(); i++) {
            items.add(tile.getImportInventory().getStack(i));
        }
        List<FluidStack> fluids = new ArrayList<>();
        for(int i = 0; i < tile.getImportFluidHandler().getSlots(); i++) {
            fluids.add(tile.getImportFluidHandler().getStackAt(i));
        }
        if (lastRecipe != null && tryRecipe(lastRecipe, items, fluids)) {
            return;
        }
        for (Recipe recipe : possibleRecipes) {
            if (tryRecipe(recipe, items, fluids)) {
                return;
            }
        }
    }

    protected void setupRecipe(Recipe recipe) {
        Brachydium.LOGGER.info("Setting up recipe " + recipe.getName());
        currentRecipe = recipe;
        setState(State.RUNNING);
        progress = 0;
        duration = recipe.getDuration();
        recipeEUt = recipe.getEUt();

    }

    private void onInventoryUpdate() {
        Brachydium.LOGGER.info("Inventory updated");
        if (isNotState(State.DISABLED, State.RUNNING, State.OUTPUT_BLOCKED)) {
            trySearchNewRecipe();
        }
    }

    private void onOutputChanged() {
        Brachydium.LOGGER.info("Output updated");
        if (isState(State.OUTPUT_BLOCKED)) {
            Brachydium.LOGGER.info("Trying recipe after output unblocked");
            setState(State.IDLING);
            List<ItemStack> items = new ArrayList<>();
            for(int i = 0; i < tile.getImportInventory().size(); i++) {
                items.add(tile.getImportInventory().getStack(i));
            }
            List<FluidStack> fluids = new ArrayList<>();
            for(int i = 0; i < tile.getImportFluidHandler().getSlots(); i++) {
                fluids.add(tile.getImportFluidHandler().getStackAt(i));
            }
            tryRecipe(storedRecipe, items, fluids);
        }
    }

    /**
     * tries to start the recipe
     *
     * @param recipe to try
     * @return true if the ingredients are found, otherwise false
     */
    public boolean tryRecipe(Recipe recipe, List<ItemStack> items, List<FluidStack> fluids) {
        //FIXME: when output is full and then emptied, the recipe output will be inserted immediately
        Brachydium.LOGGER.info("Trying recipe " + recipe.getName());
        if (recipeTable.tryRecipe(recipe, items, fluids, getMachineVoltage())) {
            try (Transaction transaction = Transaction.openOuter()) {
                if (!tryInsertOutput(transaction, recipe)) {
                    Brachydium.LOGGER.info("output blocked");
                    setState(State.OUTPUT_BLOCKED);
                    storedRecipe = recipe;
                    transaction.abort();
                    return true;
                }
                transaction.abort();
            }
            setupRecipe(recipe);
            return true;
        }
        return false;
    }

    public boolean tryInsertOutput(Transaction transaction, Recipe recipe) {
        try (Transaction transaction1 = transaction.openNested()) {
            for (ItemStack stack : recipe.getOutputs()) {
                long inserted = tile.getExportItemStorage().insert(ItemVariant.of(stack), stack.getCount(), transaction1);
                if (inserted != stack.getCount()) {
                    transaction1.abort();
                    return false;
                }
            }
            for (FluidStack stack : recipe.getFluidOutputs()) {
                long inserted = tile.getExportFluidStorage().insert(stack.asFluidVariant(), stack.getAmount(), transaction1);
                if (inserted != stack.getAmount()) {
                    transaction1.abort();
                    return false;
                }
            }
        }
        return true;
    }

    /*public boolean canConsume(Transaction transaction, Recipe recipe, Participant<ItemKey> itemHandler, Participant<Fluid> fluidHandler) {
        for (CountableIngredient ci : recipe.getInputs()) {
            if (!InventoryHelper.extractIngredient(itemHandler, transaction, ci)) {
                transaction.abort();
                return false;
            }
        }
        for (FluidStack stack : recipe.getFluidInputs()) {
            if (fluidHandler.extract(transaction, stack.getFluid(), stack.getAmount()) != stack.getAmount()) {
                transaction.abort();
                return false;
            }
        }
        return true;
    }*/

    @Override
    public NbtCompound serializeTag() {
        NbtCompound tag = new NbtCompound();
        tag.putString("state", state.toString());
        if (currentRecipe == null) {
            tag.putString("recipe", "null");
        } else {
            tag.putString("recipe", currentRecipe.getName());
            tag.putInt("progress", progress);
        }
        return tag;
    }

    @Override
    public void deserializeTag(NbtCompound tag) {
        state = State.valueOf(tag.getString("state"));
        String recipe = tag.getString("recipe");
        if (!recipe.equals("null")) {
            currentRecipe = recipeTable.findRecipe(recipe);
            duration = currentRecipe.getDuration();
            recipeEUt = currentRecipe.getEUt();
            progress = tag.getInt("progress");
        }
    }

    /**
     * @param states to chek
     * @return if any of the given states is the current state
     */
    public boolean isState(State... states) {
        for (State state : states) {
            if (isState(state)) {
                return true;
            }
        }
        return false;
    }

    public boolean isState(State state) {
        return this.state == state;
    }

    /**
     * @param states to check
     * @return if all of the given states are NOT the current state
     */
    public boolean isNotState(State... states) {
        return !isState(states);
    }

    public enum State {
        OUTPUT_BLOCKED,
        NOT_ENOUGH_POWER,
        RUNNING,
        DISABLED,
        IDLING;
    }
}
