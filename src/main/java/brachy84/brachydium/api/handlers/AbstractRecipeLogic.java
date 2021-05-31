package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.IWorkable;
import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.blockEntity.MBETrait;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.network.Channels;
import brachy84.brachydium.api.recipe.MTRecipe;
import brachy84.brachydium.api.recipe.RecipeTable;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * This class handles finding and running recipes consuming inputs and energy and inserting outputs
 * Energy can be any form of energy like normal "Redstone Flux", but also something like steam or mana
 */
public abstract class AbstractRecipeLogic extends MBETrait implements IWorkable {

    public final RecipeTable<?> recipeTable;

    protected MTRecipe lastRecipe;

    /**
     * currently running recipe
     */
    private MTRecipe currentRecipe;

    /**
     * This is the recipe that gets stored when the ingredients are found but the output is blocked
     */
    private MTRecipe storedRecipe;

    /**
     * A collection of recipes which has items that are currently in the inventory
     */
    protected Collection<MTRecipe> possibleRecipes;
    protected boolean allowOverclocking = true;
    protected int progress, duration, recipeEUt;

    protected final Random random = new Random();

    protected boolean workingEnabled = true;

    private State state;

    public AbstractRecipeLogic(MetaBlockEntity mbe, RecipeTable<?> recipeTable) {
        super(mbe);
        this.recipeTable = recipeTable;
        possibleRecipes = recipeTable.getRecipeList();
        state = State.IDLING;
        metaBlockEntity.appendInitialiseListener(this::addListeners);
    }

    @Override
    public void update() {
        if (!metaBlockEntity.isClient()) {
            if (isActive()) {
                onRecipeTick();
            }
        }
    }

    private void addListeners() {

        if (metaBlockEntity.getImportFluids() instanceof InventoryListener) {
            ((InventoryListener) metaBlockEntity.getImportFluids()).addListener(this::onInventoryUpdate);
        }
        if (metaBlockEntity.getImportItems() instanceof InventoryListener) {
            ((InventoryListener) metaBlockEntity.getImportItems()).addListener(this::onInventoryUpdate);
        }
        if (metaBlockEntity.getExportItems() instanceof InventoryListener) {
            ((InventoryListener) metaBlockEntity.getExportItems()).addListener(this::onOutputChanged);
        }
        if (metaBlockEntity.getExportFluids() instanceof InventoryListener) {
            ((InventoryListener) metaBlockEntity.getExportFluids()).addListener(this::onOutputChanged);
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
        Transaction transaction = Transaction.create();
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

    public void setState(State state) {
        this.state = state;
        if (!metaBlockEntity.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(metaBlockEntity.getPos());
            buf.writeString(state.toString());
            for (PlayerEntity player : metaBlockEntity.getWorld().getPlayers()) {
                if (player instanceof ServerPlayerEntity) {
                    ServerPlayNetworking.send((ServerPlayerEntity) player, Channels.UPDATE_WORKING_STATE, buf);
                }
            }
        }
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
        if (lastRecipe != null && tryRecipe(lastRecipe)) {
            return;
        }
        for (MTRecipe recipe : possibleRecipes) {
            if (tryRecipe(recipe)) {
                return;
            }
        }
    }

    protected void setupRecipe(MTRecipe recipe) {
        Brachydium.LOGGER.info("Setting up recipe " + recipe.getName());
        currentRecipe = recipe;
        setState(State.RUNNING);
        try (Transaction transaction = Transaction.create()) {
            if (!canConsume(transaction, recipe, metaBlockEntity.getImportItems(), metaBlockEntity.getImportFluids())) {
                Brachydium.LOGGER.error("Could not consume ingredients! Something is really wrong");
                currentRecipe = null;
                return;
            }
            transaction.commit();
        }
        progress = 0;
        duration = recipe.getDuration();
        recipeEUt = recipe.getEUt();

    }

    private void onInventoryUpdate() {
        Brachydium.LOGGER.info("Inventory updated");
        if (state != State.RUNNING && state != State.OUTPUT_BLOCKED) {
            trySearchNewRecipe();
        }
    }

    private void onOutputChanged() {
        Brachydium.LOGGER.info("Output updated");
        if (state == State.OUTPUT_BLOCKED) {
            Brachydium.LOGGER.info("Trying recipe after output unblocked");
            setState(State.IDLING);
            tryRecipe(storedRecipe);
        }
    }

    /**
     * tries to start the recipe
     *
     * @param recipe to try
     * @return true if the ingredients are found, otherwise false
     */
    public boolean tryRecipe(MTRecipe recipe) {
        //FIXME: when output is full and then emptied, the recipe output will be inserted immediately
        Brachydium.LOGGER.info("Trying recipe " + recipe.getName());
        if (InventoryHelper.hasIngredientsAndFluids(metaBlockEntity.getImportItems(), metaBlockEntity.getImportFluids(), recipe.getInputs(), recipe.getFluidInputs())) {
            try (Transaction transaction = Transaction.create()) {
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

    public boolean tryInsertOutput(Transaction transaction, MTRecipe recipe) {
        try (Transaction transaction1 = transaction.nest()) {
            for (ItemStack stack : recipe.getOutputs()) {
                int inserted = metaBlockEntity.getExportItems().insert(transaction1, ItemKey.of(stack), stack.getCount());
                if (inserted != stack.getCount()) {
                    transaction1.abort();
                    return false;
                }
            }
            for (FluidStack stack : recipe.getFluidOutputs()) {
                int inserted = metaBlockEntity.getExportFluids().insert(transaction1, stack.getFluid(), stack.getAmount());
                if (inserted != stack.getAmount()) {
                    transaction1.abort();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canConsume(Transaction transaction, MTRecipe recipe, Participant<ItemKey> itemHandler, Participant<Fluid> fluidHandler) {
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
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
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
    public void deserializeTag(CompoundTag tag) {
        state = State.valueOf(tag.getString("state"));
        String recipe = tag.getString("recipe");
        if (!recipe.equals("null")) {
            currentRecipe = recipeTable.findRecipe(recipe);
            duration = currentRecipe.getDuration();
            recipeEUt = currentRecipe.getEUt();
            progress = tag.getInt("progress");
        }
    }

    public enum State {
        OUTPUT_BLOCKED,
        NOT_ENOUGH_POWER,
        RUNNING,
        IDLING;
    }
}
