package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.gui_v1.BrachydiumGui;
import brachy84.brachydium.api.gui_v1.widgets.AItemSlot;
import brachy84.brachydium.api.handlers.FluidStack;
import brachy84.brachydium.api.handlers.IFluidInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private final List<BlockItem> tileItems = new ArrayList<>();

    /**
     * The recipes that were registered on this table
     */
    private final Collection<MTRecipe> recipeList = new ArrayList<>();

    public RecipeTable(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
                       int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
                       R defaultRecipe) {
        if(minInputs < 0 || minFluidInputs < 0 || minOutputs < 0 || minFluidOutputs < 0) {
            throw new IllegalArgumentException("minInputs or mayOutoutputs can't be smaller than 0");
        }
        if(minInputs > maxInputs || minOutputs > maxOutputs || minFluidInputs > maxFluidInputs || minFluidOutputs > maxFluidOutputs) {
            throw new IllegalArgumentException("Max can't be smaller than Min in RecipeTable");
        }
        if(minInputs == 0 && minFluidInputs == 0) {
            throw new IllegalArgumentException("minInputs and minFluidInputs can not be both null");
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

    public MTRecipe findRecipe(List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        if(inputs == null || fluidInputs == null) {
            return null;
        }
        for(MTRecipe recipe : recipeList) {

        }
        return null;
    }

    public MTRecipe findRecipe(String name) {
        for(MTRecipe recipe : recipeList) {
            if(recipe.getName().equals(name)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Finds the first recipe that matches for processing
     * This is different to the other find methods, because
     * the other methods try to find the exact recipe, while this one
     * checks if the machine has AT LEAST the required inputs
     * // TODO: implement current machine tier
     * @param inputs
     * @param fluidInputs
     * @return
     */
    /*public MTRecipe findRecipeForProcessing(List<ItemStack> inputs, List<FluidVolume> fluidInputs) {
        if(inputs == null || fluidInputs == null) {
            System.out.println("one list is null");
            return null;
        }
        //System.out.println("length: " + recipeList.size());
        for(MTRecipe recipe : recipeList) {
            boolean matches = false;
            if(minInputs > 0) {
                //System.out.println("testing items");
                matches = Util.contains(recipe.getInputs(), inputs);
            }
            if(minFluidInputs > 0) {
                //System.out.println("testing fluids");
                //matches = Util.equalsFluidList(recipe.getFluidInputs(), fluidInputs);
                matches = fluidInputs
            }
            if(matches) {
                return recipe;
            }
        }
        //System.out.println("returning null");
        return null;
    }*/

    public void addRecipe(MTRecipe recipe) {
        recipeList.add(recipe);
        System.out.println("---Recipe added to " + unlocalizedName);
    }

    public R recipeBuilder(String name) {
        R builder = recipeBuilderSample.copyWithName(name);
        builder.setRecipeTable(this);
        return builder;
    }

    public void addTileItem(BlockItem item) {
        tileItems.add(item);
    }

    public List<BlockItem> getTileItems() {
        return Collections.unmodifiableList(tileItems);
    }

    public R recipeBuilder() {
        return recipeBuilder("");
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

    public Collection<MTRecipe> getRecipes() {
        return recipeList;
    }

    public BrachydiumGui.Builder createUITemplate(BrachydiumGui.Builder builder, Inventory importItems, Inventory exportItems, IFluidInventory importFluids, IFluidInventory exportFluids) {
        if(builder == null || importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        builder.bindInventory();
        //TODO: duration bar
        addInventorySlotGroup(builder, importItems, importFluids, false);
        addInventorySlotGroup(builder, exportItems, exportFluids, true);

        return builder;
    }

    // copy pasted from GTCE
    /*public ModularGui.Builder createUITemplate(DoubleSupplier progressSupplier, ItemHandler importItems, ItemHandler exportItems, FluidHandler importFluids, FluidHandler exportFluids) {
        Brachydium.LOGGER.info("Creating UI (RecipeTable)");
        ModularGui.Builder builder = ModularGui.defaultBuilder();
        if(importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        //builder.widget(new ProgressWidget(progressSupplier, 77, 22, 21, 20, progressBarTexture, moveType));
        builder.widget(new DurationBar(GuiTextures.ARROW_BAR, 77, 22));
        addInventorySlotGroup(builder, importItems, importFluids, false);
        addInventorySlotGroup(builder, exportItems, exportFluids, true);
        return builder;
    }*/

    protected void addInventorySlotGroup(BrachydiumGui.Builder builder, Inventory itemHandler, IFluidInventory fluidHandler, boolean isOutputs) {
        int itemInputsCount = itemHandler.size();
        int fluidInputsCount = fluidHandler.size();
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
                    addSlot(builder, startSpecX, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            } else {
                int startSpecY = startInputsY + itemSlotsToDown * 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int x = isOutputs ? startInputsX + 18 * (i % 3) : startInputsX + itemSlotsToLeft * 18 - 18 - 18 * (i % 3);
                    int y = startSpecY + (i / 3) * 18;
                    addSlot(builder, x, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            }
        }
    }

    protected void addSlot(BrachydiumGui.Builder builder, int x, int y, int slotIndex, Inventory itemHandler, IFluidInventory fluidHandler, boolean isFluid, boolean isOutputs) {
        AItemSlot.Type type = isOutputs ? AItemSlot.Type.EXPORT : AItemSlot.Type.IMPORT;
        if (!isFluid) {

            builder.slot(type, fluidHandler, slotIndex, x, y);
            //builder.slot(new Slot(itemHandler.getMCInventory(), slotIndex, x, y, isOutputs));
            //.setBackgroundTexture(getOverlaysForSlot(isOutputs, false, slotIndex == itemHandler.getSlots() - 1)));
        } else {
            builder.slot(type, itemHandler, slotIndex, x, y);
            //builder.slot(new FluidSlot(fluidHandler, slotIndex, x, y, isOutputs));
            //.setAlwaysShowFull(true)
            //.setBackgroundTexture(getOverlaysForSlot(isOutputs, true, slotIndex == fluidHandler.getTanks() - 1))
            //.setContainerClicking(true, !isOutputs));
        }
    }

    // copy pasted from GTCE
    /*protected void addInventorySlotGroup(ModularGui.Builder builder, ItemHandler itemHandler, FluidHandler fluidHandler, boolean isOutputs) {
        int itemInputsCount = itemHandler.getSlotCount();
        int fluidInputsCount = fluidHandler.getTankCount();
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
                    addSlot(builder, startSpecX, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            } else {
                int startSpecY = startInputsY + itemSlotsToDown * 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int x = isOutputs ? startInputsX + 18 * (i % 3) : startInputsX + itemSlotsToLeft * 18 - 18 - 18 * (i % 3);
                    int y = startSpecY + (i / 3) * 18;
                    addSlot(builder, x, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            }
        }
    }

    // copy pasted from GTCE
    protected void addSlot(ModularGui.Builder builder, int x, int y, int slotIndex, ItemHandler itemHandler, FluidHandler fluidHandler, boolean isFluid, boolean isOutputs) {
        if (!isFluid) {
            builder.slot(new Slot(itemHandler.getMCInventory(), slotIndex, x, y, isOutputs));
                    //.setBackgroundTexture(getOverlaysForSlot(isOutputs, false, slotIndex == itemHandler.getSlots() - 1)));
        } else {
            builder.slot(new FluidSlot(fluidHandler, slotIndex, x, y, isOutputs));
                    //.setAlwaysShowFull(true)
                    //.setBackgroundTexture(getOverlaysForSlot(isOutputs, true, slotIndex == fluidHandler.getTanks() - 1))
                    //.setContainerClicking(true, !isOutputs));
        }
    }*/

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
}
