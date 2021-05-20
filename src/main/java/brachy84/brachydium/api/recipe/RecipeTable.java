package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.*;

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
    //private final Collection<MTRecipe> recipeList = new ArrayList<>();
    private final Map<String, MTRecipe> recipeMap = new HashMap<>();

    public RecipeTable(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
                       int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
                       R defaultRecipe) {
        if(minInputs < 0 || minFluidInputs < 0 || minOutputs < 0 || minFluidOutputs < 0) {
            throw new IllegalArgumentException("inputs and outputs can't be smaller than 0");
        }
        if(minInputs > maxInputs || minOutputs > maxOutputs || minFluidInputs > maxFluidInputs || minFluidOutputs > maxFluidOutputs) {
            throw new IllegalArgumentException("Max can't be smaller than Min in RecipeTable");
        }
        /*if(minInputs == 0 && minFluidInputs == 0) {
            throw new IllegalArgumentException("minInputs and minFluidInputs can not be both null");
        }*/
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

    public Collection<MTRecipe> getRecipeList() {
        return recipeMap.values();
    }

    public Map<String, MTRecipe> getRecipeMap() {
        return recipeMap;
    }

    public MTRecipe findRecipe(List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        if(inputs == null || fluidInputs == null) {
            return null;
        }
        for(MTRecipe recipe : getRecipeList()) {

        }
        return null;
    }

    public MTRecipe findRecipe(String name) {
        return recipeMap.get(name);
    }

    /**
     * Finds the first recipe that matches for processing
     * This is different to the other find methods, because
     * the other methods try to find the exact recipe, while this one
     * checks if the machine has AT LEAST the required inputs
     * // TODO: implement current machine tier*/
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
        //recipeList.add(recipe);
        recipeMap.put(recipe.getName(), recipe);
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

    public RootWidget.Builder createUITemplate(RootWidget.Builder builder, ArrayParticipant<ItemKey> importItems, ArrayParticipant<ItemKey> exportItems, ArrayParticipant<Fluid> importFluids, ArrayParticipant<Fluid> exportFluids) {
        if(builder == null || importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        //TODO: duration bar
        addInventorySlotGroup(builder, importItems, importFluids, false);
        addInventorySlotGroup(builder, exportItems, exportFluids, true);

        return builder;
    }

    protected void addInventorySlotGroup(RootWidget.Builder builder, ArrayParticipant<ItemKey> itemHandler, ArrayParticipant<Fluid> fluidHandler, boolean isOutputs) {
        int itemInputsCount = itemHandler.getSlots().size();
        int fluidInputsCount = fluidHandler.getSlots().size();
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

    protected void addSlot(RootWidget.Builder builder, int x, int y, int slotIndex, ArrayParticipant<ItemKey> itemHandler, ArrayParticipant<Fluid> fluidHandler, boolean isFluid, boolean isOutputs) {
        // AItemSlot.Type type = isOutputs ? AItemSlot.Type.EXPORT : AItemSlot.Type.IMPORT;
        if (!isFluid) {
            Slot<ItemKey> itemSlot = itemHandler.getSlots().get(slotIndex);
            builder.itemSlot(itemSlot, new Point(x, y));
            //builder.slot(new Slot(itemHandler.getMCInventory(), slotIndex, x, y, isOutputs));
            //.setBackgroundTexture(getOverlaysForSlot(isOutputs, false, slotIndex == itemHandler.getSlots() - 1)));
        } else {
            Slot<Fluid> fluidSlot = fluidHandler.getSlots().get(slotIndex);
            builder.fluidSlot(fluidSlot, new Point(x, y));
            //builder.slot(new FluidSlot(fluidHandler, slotIndex, x, y, isOutputs));
            //.setAlwaysShowFull(true)
            //.setBackgroundTexture(getOverlaysForSlot(isOutputs, true, slotIndex == fluidHandler.getTanks() - 1))
            //.setContainerClicking(true, !isOutputs));
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

    public static class Builder {

        private Builder() {

        }
    }
}
