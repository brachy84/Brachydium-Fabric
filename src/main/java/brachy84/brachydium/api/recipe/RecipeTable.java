package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.gui.FluidSlotWidget;
import brachy84.brachydium.api.gui.GuiTextures;
import brachy84.brachydium.api.handlers.InventoryHelper;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.internal.Gui;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.DoubleSupplier;

public class RecipeTable<R extends RecipeBuilder<R>> {

    private static final List<RecipeTable<?>> RECIPE_TABLES = new ArrayList<>();

    public final String unlocalizedName;

    private final R recipeBuilderSample;

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
    private final Map<String, Recipe> recipeMap = new HashMap<>();

    private TextureArea itemSlotOverlay;
    private TextureArea fluidSlotOverlay;

    public RecipeTable(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
                       int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
                       R defaultRecipe) {
        if (minInputs < 0 || minFluidInputs < 0 || minOutputs < 0 || minFluidOutputs < 0) {
            throw new IllegalArgumentException("inputs and outputs can't be smaller than 0");
        }
        if (minInputs > maxInputs || minOutputs > maxOutputs || minFluidInputs > maxFluidInputs || minFluidOutputs > maxFluidOutputs) {
            throw new IllegalArgumentException("Max can't be smaller than Min in RecipeTable");
        }
        this.unlocalizedName = unlocalizedName;

        this.minInputs = minInputs;
        this.minFluidInputs = minFluidInputs;
        this.minOutputs = minOutputs;
        this.minFluidOutputs = minFluidOutputs;

        this.maxInputs = maxInputs;
        this.maxFluidInputs = maxFluidInputs;
        this.maxOutputs = maxOutputs;
        this.maxFluidOutputs = maxFluidOutputs;

        defaultRecipe.setRecipeTable(this);
        this.recipeBuilderSample = defaultRecipe;
        RECIPE_TABLES.add(this);
    }

    public static List<RecipeTable<?>> getRecipeTables() {
        return Collections.unmodifiableList(RECIPE_TABLES);
    }

    public static RecipeTable<?> getByName(String unlocalizedName) {
        return RECIPE_TABLES.stream()
                .filter(map -> map.unlocalizedName.equals(unlocalizedName))
                .findFirst().orElse(null);
    }

    public Collection<Recipe> getRecipeList() {
        return recipeMap.values();
    }

    public Map<String, Recipe> getRecipeMap() {
        return recipeMap;
    }

    public Recipe findRecipe(List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        Objects.requireNonNull(inputs);
        Objects.requireNonNull(fluidInputs);

        return null;
    }

    public Recipe findRecipe(String name) {
        return recipeMap.get(name);
    }

    public void addRecipe(Recipe recipe) {
        recipeMap.put(recipe.getName(), recipe);
    }

    public boolean hasRecipeKey(String key) {
        return recipeMap.containsKey(key);
    }

    public R recipeBuilder(String name) {
        return recipeBuilderSample.copyWithName(name);
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
        for(int i = 0; i < fluidHandler.getSlots(); i++) {
            fluids.add(fluidHandler.getStackAt(i));
        }
        return findRecipe(items, fluids, voltage);
    }

    public Recipe findRecipe(List<ItemStack> items, List<FluidStack> fluids, long voltage) {
        for(Recipe recipe : recipeMap.values()) {
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

    // = Gui Generation =============================================

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

    public Gui.Builder createUITemplate(DoubleSupplier progress, Gui.Builder builder,
                                        Inventory importItems,
                                        Inventory exportItems,
                                        IFluidHandler importFluids,
                                        IFluidHandler exportFluids) {
        if (builder == null || importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        //builder.widget(new ProgressBarWidget(progress, GuiTextures.ARROW, AABB.of(new Size(18, 18), new Pos2d(builder.getBounds().width / 2 - 9, 22)), MoveDirection.RIGHT).name("Duration bar"));
        addInventorySlotGroup(builder, importItems, importFluids, false);
        addInventorySlotGroup(builder, exportItems, exportFluids, true);
        return builder;
    }

    protected void addInventorySlotGroup(Gui.Builder builder,
                                         Inventory itemHandler,
                                         IFluidHandler fluidHandler, boolean isOutputs) {
        int itemInputsCount = itemHandler.size();
        int fluidInputsCount = fluidHandler.getSlots();
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

    @Override
    public String toString() {
        return unlocalizedName;
    }
}
