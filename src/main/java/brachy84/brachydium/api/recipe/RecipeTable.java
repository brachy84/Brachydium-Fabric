package brachy84.brachydium.api.recipe;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.gui.FluidSlotWidget;
import brachy84.brachydium.api.gui.GuiTextures;
import brachy84.brachydium.api.handlers.InventoryHelper;
import brachy84.brachydium.api.handlers.storage.FluidInventory;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.handlers.storage.ItemInventory;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.recipe.builders.SimpleRecipeBuilder;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import brachy84.brachydium.api.util.MathUtil;
import brachy84.brachydium.api.util.TransferUtil;
import brachy84.brachydium.api.util.ValidationResult;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.ProgressBarWidget;
import brachy84.brachydium.gui.internal.Gui;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantCache;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantCache;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

public class RecipeTable<R extends RecipeBuilder<R>> {

    private static final Map<String, RecipeTable<?>> RECIPE_TABLES = new HashMap<>();

    public static final IChanceFunction chanceFunction = (chance, boostPerTier, tier) -> chance + (boostPerTier * tier);

    public static Builder<SimpleRecipeBuilder> simpleBuilder(String unlocalizedName) {
        return builder(unlocalizedName, new SimpleRecipeBuilder().duration(100).EUt(8));
    }

    public static <R extends RecipeBuilder<R>> Builder<R> builder(String unlocalizedName, R sample) {
        if(unlocalizedName == null)
            throw new NullPointerException("RecipeTable can not have a null name");
        if(sample == null)
            throw new NullPointerException("RecipeTable can not have a null sample");
        if(RECIPE_TABLES.containsKey(unlocalizedName))
            throw new IllegalStateException("A RecipeTable with name '" + unlocalizedName + "' already exists!");
        return new Builder<R>(unlocalizedName, sample);
    }

    private static boolean foundInvalidRecipe = false;

    public final String unlocalizedName;

    private final R recipeBuilderSample;

    private final BiConsumer<TileEntity, Gui.Builder> guiBuilder;
    private AABB jeiBounds;
    private float jeiTranslationX = 0F, jeiTranslationY = 0F;

    private final int minInputs, maxInputs, minOutputs, maxOutputs;
    private final int minFluidInputs, maxFluidInputs, minFluidOutputs, maxFluidOutputs;

    /**
     * This contains all block items of the tile that use this recipeMap
     * Only for REI
     */
    private final List<ItemStack> tileItems = new ArrayList<>();

    /**
     * The recipes that were registered on this table
     */
    //private final Map<String, Recipe> recipeMap = new HashMap<>();


    private final Object2ObjectOpenHashMap<FluidVariant, Set<Recipe>> recipeFluidMap = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<ItemVariant, Set<Recipe>> recipeItemMap = new Object2ObjectOpenHashMap<>();

    private static final Comparator<Recipe> RECIPE_DURATION_THEN_EU =
            Comparator.comparingInt(Recipe::getDuration)
                    .thenComparingInt(Recipe::getEUt);

    private final Set<Recipe> recipeSet = new HashSet<>();

    private TextureArea itemSlotOverlay;
    private TextureArea fluidSlotOverlay;

    private RecipeTable(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
                       int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
                       R defaultRecipe, BiConsumer<TileEntity, Gui.Builder> guiBuilder) {

        this.unlocalizedName = unlocalizedName;

        this.minInputs = minInputs;
        this.minFluidInputs = minFluidInputs;
        this.minOutputs = minOutputs;
        this.minFluidOutputs = minFluidOutputs;

        this.maxInputs = maxInputs;
        this.maxFluidInputs = maxFluidInputs;
        this.maxOutputs = maxOutputs;
        this.maxFluidOutputs = maxFluidOutputs;
        this.guiBuilder = guiBuilder;

        defaultRecipe.setRecipeTable(this);
        this.recipeBuilderSample = defaultRecipe;
        RECIPE_TABLES.put(this.unlocalizedName, this);
    }

    public static Collection<RecipeTable<?>> getRecipeTables() {
        return Collections.unmodifiableCollection(RECIPE_TABLES.values());
    }

    public static RecipeTable<?> getByName(String unlocalizedName) {
        return RECIPE_TABLES.get(unlocalizedName);
    }

    public static IChanceFunction getChanceFunction() {
        return chanceFunction;
    }

    public interface IChanceFunction {
        int chanceFor(int chance, int boostPerTier, int boostTier);
    }

    public Collection<Recipe> getRecipes() {
        return Collections.unmodifiableSet(recipeSet);
    }

    public Recipe findRecipe(List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        Objects.requireNonNull(inputs);
        Objects.requireNonNull(fluidInputs);

        return null;
    }

    public void addRecipe(ValidationResult<Recipe> validationResult) {
        validationResult = postValidateRecipe(validationResult);
        if(validationResult.doSkip())
            return;
        else if(validationResult.isInvalid()) {
            setFoundInvalidRecipe(true);
            return;
        }
        Recipe recipe = validationResult.getResult();
        if (recipeSet.add(recipe)) {
            for (CountableIngredient countableIngredient : recipe.getInputs()) {
                ItemStack[] stacks = countableIngredient.getIngredient().getMatchingStacks();
                for (ItemStack itemStack : stacks) {
                    ItemVariant stackKey = KeySharedStack.getRegisteredStack(itemStack);
                    recipeItemMap.computeIfPresent(stackKey, (k, v) -> {
                        v.add(recipe);
                        return v;
                    });
                    recipeItemMap.computeIfAbsent(stackKey, k -> new HashSet<>()).add(recipe);
                }
            }
            for (FluidStack fluid : recipe.getFluidInputs()) {
                FluidVariant fluidKey = fluid.asFluidVariant();
                recipeFluidMap.computeIfPresent(fluidKey, (k, v) -> {
                    v.add(recipe);
                    return v;
                });
                recipeFluidMap.computeIfAbsent(fluidKey, k -> new HashSet<>()).add(recipe);
            }
        } else /*if (ConfigHolder.debug) */{
            Brachydium.LOGGER.debug("Recipe: " + recipe.toString() + " is a duplicate and was not added");
        }
    }

    protected ValidationResult<Recipe> postValidateRecipe(ValidationResult<Recipe> validationResult) {
        ValidationResult.State recipeStatus = validationResult.getState();
        Recipe recipe = validationResult.getResult();
        if (!MathUtil.isInRange(recipe.getInputs().size(), getMinInputs(), getMaxInputs())) {
            Brachydium.LOGGER.error("Invalid amount of recipe inputs. Actual: {}. Should be between {} and {} inclusive.", recipe.getInputs().size(), getMinInputs(), getMaxInputs());
            Brachydium.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        if (!MathUtil.isInRange(recipe.getOutputs().size() + recipe.getChancedOutputs().size(), getMinOutputs(), getMaxOutputs())) {
            Brachydium.LOGGER.error("Invalid amount of recipe outputs. Actual: {}. Should be between {} and {} inclusive.", recipe.getOutputs().size() + recipe.getChancedOutputs().size(), getMinOutputs(), getMaxOutputs());
            Brachydium.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        if (!MathUtil.isInRange( recipe.getFluidInputs().size(), getMinFluidInputs(), getMaxFluidInputs())) {
            Brachydium.LOGGER.error("Invalid amount of recipe fluid inputs. Actual: {}. Should be between {} and {} inclusive.", recipe.getFluidInputs().size(), getMinFluidInputs(), getMaxFluidInputs());
            Brachydium.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        if (!MathUtil.isInRange(recipe.getFluidOutputs().size(), getMinFluidOutputs(), getMaxFluidOutputs())) {
            Brachydium.LOGGER.error("Invalid amount of recipe fluid outputs. Actual: {}. Should be between {} and {} inclusive.", recipe.getFluidOutputs().size(), getMinFluidOutputs(), getMaxFluidOutputs());
            Brachydium.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        return validationResult.withState(recipeStatus);
    }

    public static boolean isFoundInvalidRecipe() {
        return foundInvalidRecipe;
    }

    public static void setFoundInvalidRecipe(boolean foundInvalidRecipe) {
        foundInvalidRecipe |= foundInvalidRecipe;
        TagDictionary.Entry currentOrePrefix = TagDictionary.Entry.getCurrentProcessingPrefix();
        if (currentOrePrefix != null) {
            Material currentMaterial = TagDictionary.Entry.getCurrentMaterial();
            Brachydium.LOGGER.error("Error happened during processing ore registration of prefix {} and material {}. " +
                            "Seems like cross-mod compatibility issue. Report to GTCE github.",
                    currentOrePrefix, currentMaterial);
        }
    }

    public R recipeBuilder() {
        return recipeBuilderSample.copy();
    }

    public void addTileItem(ItemStack item) {
        tileItems.add(item);
    }

    public List<ItemStack> getTileItems() {
        return Collections.unmodifiableList(tileItems);
    }

    public int getMinInputs() {
        return minInputs;
    }

    public int getMaxInputs() {
        return maxInputs;
    }

    public int getMinOutputs() {
        return minOutputs;
    }

    public int getMaxOutputs() {
        return maxOutputs;
    }

    public int getMinFluidInputs() {
        return minFluidInputs;
    }

    public int getMaxFluidInputs() {
        return maxFluidInputs;
    }

    public int getMinFluidOutputs() {
        return minFluidOutputs;
    }

    public int getMaxFluidOutputs() {
        return maxFluidOutputs;
    }

    public Recipe findRecipe(Inventory inventory, IFluidHandler fluidHandler, long voltage) {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < inventory.size(); i++) {
            items.add(inventory.getStack(i));
        }
        List<FluidStack> fluids = new ArrayList<>();
        for(int i = 0; i < fluidHandler.getTanks(); i++) {
            fluids.add(fluidHandler.getStackAt(i));
        }
        return findRecipe(items, fluids, voltage);
    }

    public Recipe findRecipe(List<ItemStack> items, List<FluidStack> fluids, long voltage) {
        for(Recipe recipe : recipeSet) {
            if(tryRecipe(recipe, items, fluids, voltage))
                return recipe;
        }
        return null;
    }

    public boolean tryRecipe(Recipe recipe, List<ItemStack> items, List<FluidStack> fluids, long voltage) {
        if(recipe.getEUt() > voltage)
            return false;
        if(maxInputs > 0) {
            for(CountableIngredient ci : recipe.getInputs()) {
                if(!InventoryHelper.containsIngredient(items, ci))
                    return false;
            }
        }
        if(maxFluidInputs > 0) {
            for(FluidStack stack : recipe.getFluidInputs()) {
                if(!InventoryHelper.containsFluidStack(fluids, stack))
                    return false;
            }
        }
        return true;
    }


    @Nullable
    public Recipe findRecipe(long voltage, Storage<ItemVariant> inputs, Storage<FluidVariant> fluidInputs, int outputFluidTankCapacity, MatchingMode matchingMode) {
        return this.findRecipe(voltage, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), outputFluidTankCapacity, matchingMode);
    }

    @Nullable
    public Recipe findRecipe(long voltage, Inventory inputs, IFluidHandler fluidInputs, int outputFluidTankCapacity, MatchingMode matchingMode) {
        return this.findRecipe(voltage, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), outputFluidTankCapacity, matchingMode);
    }

    /**
     * Finds a Recipe matching the Fluid and/or ItemStack Inputs.
     *
     * @param voltage                 Voltage of the Machine or Long.MAX_VALUE if it has no Voltage
     * @param inputs                  the Item Inputs
     * @param fluidInputs             the Fluid Inputs
     * @param outputFluidTankCapacity minimal capacity of output fluid tank, used for fluid canner recipes for example
     * @param matchingMode            matching logic used for finding the recipe according to {@link MatchingMode}
     * @return the Recipe it has found or null for no matching Recipe
     */
    @Nullable
    public Recipe findRecipe(long voltage, List<ItemStack> inputs, List<FluidStack> fluidInputs, int outputFluidTankCapacity, MatchingMode matchingMode) {
        return findRecipe(voltage, inputs, fluidInputs, outputFluidTankCapacity, matchingMode, false);
    }

    /**
     * Finds a Recipe matching the Fluid and/or ItemStack Inputs.
     *
     * @param voltage                 Voltage of the Machine or Long.MAX_VALUE if it has no Voltage
     * @param inputs                  the Item Inputs
     * @param fluidInputs             the Fluid Inputs
     * @param outputFluidTankCapacity minimal capacity of output fluid tank, used for fluid canner recipes for example
     * @param matchingMode            matching logic used for finding the recipe according to {@link MatchingMode}
     * @param exactVoltage            should require exact voltage matching on recipe. used by craftweaker
     * @return the Recipe it has found or null for no matching Recipe
     */

    @Nullable
    public Recipe findRecipe(long voltage, List<ItemStack> inputs, List<FluidStack> fluidInputs, int outputFluidTankCapacity, MatchingMode matchingMode, boolean exactVoltage) {
        if (recipeSet.isEmpty())
            return null;
        if (minFluidInputs > 0 && fluidInputs.size() < minFluidInputs) {
            return null;
        }
        if (minInputs > 0 && inputs.size() < minInputs) {
            return null;
        }
        return findByInputsAndFluids(voltage, inputs, fluidInputs, matchingMode, exactVoltage);
    }

    @Nullable
    private Recipe findByInputsAndFluids(long voltage, List<ItemStack> inputs, List<FluidStack> fluidInputs, MatchingMode matchingMode, boolean exactVoltage) {
        HashSet<Recipe> iteratedRecipes = new HashSet<>();
        HashSet<ItemVariant> searchedItems = new HashSet<>();
        HashSet<FluidVariant> searchedFluids = new HashSet<>();
        HashMap<Integer, LinkedList<Recipe>> priorityRecipeMap = new HashMap<>();
        HashMap<Recipe, Integer> promotedTimes = new HashMap<>();

        if (matchingMode != MatchingMode.IGNORE_ITEMS) {
            for (ItemStack stack : inputs) {
                if (!stack.isEmpty()) {
                    ItemVariant itemStackKey = KeySharedStack.getRegisteredStack(stack);
                    if (!searchedItems.contains(itemStackKey) && recipeItemMap.containsKey(itemStackKey)) {
                        searchedItems.add(itemStackKey);
                        for (Recipe tmpRecipe : recipeItemMap.get(itemStackKey)) {
                            if (!exactVoltage && voltage < tmpRecipe.getEUt()) {
                                continue;
                            } else if (exactVoltage && voltage != tmpRecipe.getEUt()) {
                                continue;
                            }
                            calculateRecipePriority(tmpRecipe, promotedTimes, priorityRecipeMap);
                        }
                    }
                }
            }
        }

        if (matchingMode != MatchingMode.IGNORE_FLUIDS) {
            for (FluidStack fluidStack : fluidInputs) {
                if (fluidStack != null) {
                    FluidVariant fluidKey = fluidStack.asFluidVariant();
                    if (!searchedFluids.contains(fluidKey) && recipeFluidMap.containsKey(fluidKey)) {
                        searchedFluids.add(fluidKey);
                        for (Recipe tmpRecipe : recipeFluidMap.get(fluidKey)) {
                            if (!exactVoltage && voltage < tmpRecipe.getEUt()) {
                                continue;
                            } else if (exactVoltage && voltage != tmpRecipe.getEUt()) {
                                continue;
                            }
                            calculateRecipePriority(tmpRecipe, promotedTimes, priorityRecipeMap);
                        }
                    }
                }
            }
        }
        return prioritizedRecipe(priorityRecipeMap,iteratedRecipes,inputs,fluidInputs,matchingMode);
    }

    private Recipe prioritizedRecipe(Map<Integer, LinkedList<Recipe>> priorityRecipeMap, HashSet<Recipe> iteratedRecipes,List<ItemStack> inputs, List<FluidStack> fluidInputs, MatchingMode matchingMode) {
        for (int i = priorityRecipeMap.size(); i >= 0; i--) {
            if (priorityRecipeMap.containsKey(i)) {
                for (Recipe tmpRecipe : priorityRecipeMap.get(i)) {
                    if (iteratedRecipes.add(tmpRecipe)) {
                        if (tmpRecipe.matches(false, inputs, fluidInputs, matchingMode)) {
                            return tmpRecipe;
                        }
                    }
                }
            }
        }

        return null;
    }

    private void calculateRecipePriority(Recipe recipe, HashMap<Recipe, Integer> promotedTimes, Map<Integer, LinkedList<Recipe>> priorityRecipeMap ) {
        Integer p = promotedTimes.get(recipe);
        if (p == null) {
            p = 0;
        }
        promotedTimes.put(recipe, p + 1);
        if (priorityRecipeMap.get(p) == null) {
            priorityRecipeMap.put(p, new LinkedList<>());
        }
        priorityRecipeMap.get(p).add(recipe);
    }

    // = Gui Generation =============================================


    public BiConsumer<TileEntity, Gui.Builder> getGuiBuilder() {
        return guiBuilder;
    }

    public RecipeTable<R> setItemSlotOverlay(TextureArea textureArea) {
        this.itemSlotOverlay = textureArea;
        return this;
    }

    public RecipeTable<R> setFluidSlotOverlay(TextureArea textureArea) {
        this.fluidSlotOverlay = textureArea;
        return this;
    }

    public Gui.Builder createUITemplateOfTile(DoubleSupplier progress, Gui.Builder builder, TileEntity tile) {
        return createUITemplate(progress, builder, tile.getImportInventory(), tile.getExportInventory(), tile.getImportFluidHandler(), tile.getExportFluidHandler());
    }

    private float simulateProgress = 0f;

    private double jeiProgressBar() {
        if ((simulateProgress += 0.002f) > 1.0f)
            simulateProgress = 0.0f;
        return simulateProgress;
    }

    public Gui.Builder createJeiUiTemplate(Gui.Builder builder) {
        return createUITemplate(this::jeiProgressBar, builder, ItemInventory.importInventory(getMaxInputs()),
                ItemInventory.exportInventory(getMaxOutputs()),
                FluidInventory.importInventory(getMaxFluidInputs(), 64 * 81000),
                FluidInventory.exportInventory(getMaxFluidOutputs(), 64 * 81000));
    }

    public Gui.Builder createUITemplate(DoubleSupplier progress, Gui.Builder builder,
                                        Inventory importItems,
                                        Inventory exportItems,
                                        IFluidHandler importFluids,
                                        IFluidHandler exportFluids) {
        if (builder == null || importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        builder.widget(new ProgressBarWidget(progress, GuiTextures.ARROW).setSize(new Size(20, 20)).setPos(new Pos2d(78, 22)));
        addInventorySlotGroup(builder, importItems, importFluids, false);
        addInventorySlotGroup(builder, exportItems, exportFluids, true);
        return builder;
    }

    protected void addInventorySlotGroup(Gui.Builder builder,
                                         Inventory itemHandler,
                                         IFluidHandler fluidHandler, boolean isOutputs) {
        int itemInputsCount = itemHandler.size();
        int fluidInputsCount = fluidHandler.getTanks();
        boolean invertFluids = false;
        if (itemInputsCount == 0) {
            int tmp = itemInputsCount;
            itemInputsCount = fluidInputsCount;
            fluidInputsCount = tmp;
            invertFluids = true;
        }
        int[] inputSlotGrid = determineSlotsGrid(itemInputsCount);
        int itemSlotsToLeft = inputSlotGrid[0];
        int itemSlotsToDown = inputSlotGrid[1];
        int startInputsX = isOutputs ? 106 : 69 - itemSlotsToLeft * 18;
        int startInputsY = 32 - (int) (itemSlotsToDown / 2.0 * 18);
        for (int i = 0; i < itemSlotsToDown; i++) {
            for (int j = 0; j < itemSlotsToLeft; j++) {
                int slotIndex = i * itemSlotsToLeft + j;
                int x = startInputsX + 18 * j;
                int y = startInputsY + 18 * i;
                addSlot(builder, x, y, slotIndex, itemHandler, fluidHandler, invertFluids, isOutputs);
            }
        }
        if (fluidInputsCount > 0 || invertFluids) {
            if (itemSlotsToDown >= fluidInputsCount && itemSlotsToLeft < 3) {
                int startSpecX = isOutputs ? startInputsX + itemSlotsToLeft * 18 : startInputsX - 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int y = startInputsY + 18 * i;
                    addSlot(builder, startSpecX, y, i, itemHandler, fluidHandler, true, isOutputs);
                }
            } else {
                int startSpecY = startInputsY + itemSlotsToDown * 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int x = isOutputs ? startInputsX + 18 * (i % 3) : startInputsX + itemSlotsToLeft * 18 - 18 - 18 * (i % 3);
                    int y = startSpecY + (i / 3) * 18;
                    addSlot(builder, x, y, i, itemHandler, fluidHandler, true, isOutputs);
                }
            }
        }
    }

    protected void addSlot(Gui.Builder builder, int x, int y, int slotIndex,
                           Inventory itemHandler,
                           IFluidHandler fluidHandler, boolean isFluid, boolean isOutputs) {
        if (!isFluid) {
            ItemSlotWidget slot = new ItemSlotWidget(itemHandler, slotIndex, new Pos2d(x, y));
            if (isOutputs) slot.markOutput();
            else slot.markInput();
            builder.widget(slot);
        } else {
            FluidSlotWidget slot = new FluidSlotWidget(fluidHandler, slotIndex, new Pos2d(x, y));
            if (isOutputs) slot.markOutput();
            else slot.markInput();
            builder.widget(slot);
        }
    }

    // copy pasted from GTCE
    protected static int[] determineSlotsGrid(int itemInputsCount) {
        int itemSlotsToLeft = 0;
        int itemSlotsToDown = 0;
        double sqrt = Math.sqrt(itemInputsCount);
        if (sqrt % 1 == 0) { //check if square root is integer
            //case for 1, 4, 9 slots - it's square inputs (the most common case)
            itemSlotsToLeft = itemSlotsToDown = (int) sqrt;
        } else if (itemInputsCount % 3 == 0) {
            //case for 3 and 6 slots - 3 by horizontal and i / 3 by vertical (common case too)
            itemSlotsToDown = itemInputsCount / 3;
            itemSlotsToLeft = 3;
        } else if (itemInputsCount % 2 == 0) {
            //case for 2 inputs - 2 by horizontal and i / 3 by vertical (for 2 slots)
            itemSlotsToDown = itemInputsCount / 2;
            itemSlotsToLeft = 2;
        }
        return new int[]{itemSlotsToLeft, itemSlotsToDown};
    }

    public TextureArea[] getSlotOverlays(boolean isFluid, boolean isOutput) {
        TextureArea base = isFluid ? GuiTextures.FLUID_SLOT : GuiTextures.SLOT;
        TextureArea overlay = isOutput ? (isFluid ? fluidSlotOverlay : itemSlotOverlay) : null;
        if (overlay != null) {
            return new TextureArea[]{base, overlay};
        }
        return new TextureArea[]{base};
    }

    public static class Builder<R extends RecipeBuilder<R>> {
        private final String unlocalizedName;

        private final R recipeBuilderSample;

        private BiConsumer<TileEntity, Gui.Builder> guiBuilder;
        private AABB jeiBounds;
        private float jeiTranslationX = 0F, jeiTranslationY = 0F;

        private int minInputs = 0, maxInputs = 0, minOutputs = 0, maxOutputs = 0;
        private int minFluidInputs = 0, maxFluidInputs = 0, minFluidOutputs = 0, maxFluidOutputs = 0;

        private Builder(String name, R sample) {
             this.unlocalizedName = name;
             this.recipeBuilderSample = sample;
         }

        public Builder<R> itemInputs(int min, int max) {
            this.minInputs = min;
            this.maxInputs = max;
            return this;
        }

        public Builder<R> itemOutputs(int min, int max) {
            this.minOutputs = min;
            this.maxOutputs = max;
            return this;
        }

        public Builder<R> fluidInputs(int min, int max) {
            this.minFluidInputs = min;
            this.maxFluidInputs = max;
            return this;
        }

        public Builder<R> fluidOutputs(int min, int max) {
            this.minFluidOutputs = min;
            this.maxFluidOutputs = max;
            return this;
        }

        public Builder<R> setGuiBuilder(BiConsumer<TileEntity, Gui.Builder> builder) {
            this.guiBuilder = builder;
            return this;
        }

        public Builder<R> setJeiBounds(AABB jeiBounds) {
            this.jeiBounds = jeiBounds;
            return this;
        }

        public Builder<R> setJeiTranslation(float x, float y) {
            this.jeiTranslationX = x;
            this.jeiTranslationY = y;
            return this;
        }

        public RecipeTable<R> build() {
            if (minInputs < 0 || minFluidInputs < 0 || minOutputs < 0 || minFluidOutputs < 0) {
                throw new IllegalArgumentException("inputs and outputs can't be smaller than 0");
            }
            if (minInputs > maxInputs || minOutputs > maxOutputs || minFluidInputs > maxFluidInputs || minFluidOutputs > maxFluidOutputs) {
                throw new IllegalArgumentException("Max can't be smaller than Min in RecipeTable");
            }
            RecipeTable<R> table = new RecipeTable<>(unlocalizedName, minInputs, maxInputs, minOutputs, maxOutputs, minFluidInputs, maxFluidInputs, minFluidOutputs, maxFluidOutputs, recipeBuilderSample, guiBuilder);
            table.jeiBounds = jeiBounds;
            table.jeiTranslationX = jeiTranslationX;
            table.jeiTranslationY = jeiTranslationY;
            return table;
        }
    }

    @Override
    public String toString() {
        return unlocalizedName;
    }
}
