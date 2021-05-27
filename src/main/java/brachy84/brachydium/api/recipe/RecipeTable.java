package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.GuiTextures;
import brachy84.brachydium.gui.api.MoveDirection;
import brachy84.brachydium.gui.api.SlotTags;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import brachy84.brachydium.gui.widgets.ProgressBarWidget;
import brachy84.brachydium.gui.widgets.RootWidget;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
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
    private final List<BlockItem> tileItems = new ArrayList<>();

    /**
     * The recipes that were registered on this table
     */
    private final Map<String, MTRecipe> recipeMap = new HashMap<>();

    private TextureArea itemSlotOverlay;
    private TextureArea fluidSlotOverlay;

    public static <R extends RecipeBuilder<R>> Builder<R> builder() {
        return new Builder<>();
    }

    public RecipeTable(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
                       int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
                       R defaultRecipe) {
        if(minInputs < 0 || minFluidInputs < 0 || minOutputs < 0 || minFluidOutputs < 0) {
            throw new IllegalArgumentException("inputs and outputs can't be smaller than 0");
        }
        if(minInputs > maxInputs || minOutputs > maxOutputs || minFluidInputs > maxFluidInputs || minFluidOutputs > maxFluidOutputs) {
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

    public Collection<MTRecipe> getRecipeList() {
        return recipeMap.values();
    }

    public Map<String, MTRecipe> getRecipeMap() {
        return recipeMap;
    }

    public MTRecipe findRecipe(List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        Objects.requireNonNull(inputs);
        Objects.requireNonNull(fluidInputs);

        return null;
    }

    public MTRecipe findRecipe(String name) {
        return recipeMap.get(name);
    }

    public void addRecipe(MTRecipe recipe) {
        //recipeList.add(recipe);
        recipeMap.put(recipe.getName(), recipe);
        System.out.println("---Recipe added to " + unlocalizedName);
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

    public void addTileItem(BlockItem item) {
        tileItems.add(item);
    }

    public List<BlockItem> getTileItems() {
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

    // = Gui Generation =============================================

    public RecipeTable<R> setItemSlotOverlay(TextureArea textureArea) {
        this.itemSlotOverlay = textureArea;
        return this;
    }

    public RecipeTable<R> setFluidSlotOverlay(TextureArea textureArea) {
        this.fluidSlotOverlay = textureArea;
        return this;
    }

    public RootWidget.Builder createUITemplate(DoubleSupplier progress, RootWidget.Builder builder, ArrayParticipant<ItemKey> importItems, ArrayParticipant<ItemKey> exportItems, ArrayParticipant<Fluid> importFluids, ArrayParticipant<Fluid> exportFluids) {
        if(builder == null || importItems == null || exportItems == null || importFluids == null || exportFluids == null) {
            throw new NullPointerException("Item and Fluid handlers must not be null!");
        }
        builder.widget(new ProgressBarWidget(progress, GuiTextures.ARROW, AABB.of(new Size(18, 18), new Point(builder.getBounds().width / 2 - 9, 22)), MoveDirection.RIGHT).name("Duration bar"));
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
        if (!isFluid) {
            Slot<ItemKey> itemSlot = itemHandler.getSlots().get(slotIndex);
            builder.itemSlot(itemSlot, isOutputs, new Point(x, y), isOutputs ? SlotTags.OUTPUT : SlotTags.INPUT, getSlotOverlays(false, isOutputs));
        } else {
            Slot<Fluid> fluidSlot = fluidHandler.getSlots().get(slotIndex);
            builder.fluidSlot(fluidSlot, new Point(x, y), isOutputs ? SlotTags.OUTPUT : SlotTags.INPUT, getSlotOverlays(true, isOutputs));
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
        if(overlay != null) {
            return new TextureArea[]{base, overlay};
        }
        return new TextureArea[]{base};
    }

    public static class Builder<R extends RecipeBuilder<R>> {
        public String unlocalizedName;

        private R recipeBuilderSample;

        private int minInputs, maxInputs, minOutputs, maxOutputs;
        private int minFluidInputs, maxFluidInputs, minFluidOutputs, maxFluidOutputs;

        private Builder() {

        }

        public Builder<R> name(String unlocalizedName) {
            this.unlocalizedName = unlocalizedName;
            return this;
        }

        public Builder<R> defaultRecipe(R defaultRecipe) {
            this.recipeBuilderSample = defaultRecipe;
            return this;
        }

        public Builder<R> setInputs(int min, int max) {
            this.minInputs = min;
            this.maxInputs = max;
            return this;
        }

        public Builder<R> setOutputs(int min, int max) {
            this.minOutputs = min;
            this.maxOutputs = max;
            return this;
        }

        public Builder<R> setFluidInputs(int min, int max) {
            this.minFluidInputs = min;
            this.maxFluidInputs = max;
            return this;
        }

        public Builder<R> setFluidOutputs(int min, int max) {
            this.minFluidOutputs = min;
            this.maxFluidOutputs = max;
            return this;
        }

        public RecipeTable<R> build() {
            Objects.requireNonNull(unlocalizedName);
            Objects.requireNonNull(recipeBuilderSample);
            return new RecipeTable<>(unlocalizedName, minInputs, maxInputs, minOutputs, maxOutputs, minFluidInputs, maxFluidInputs, minFluidOutputs, maxFluidOutputs, recipeBuilderSample);
        }
    }
}
