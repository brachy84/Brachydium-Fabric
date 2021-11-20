package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.util.ItemStackHashStrategy;
import brachy84.brachydium.api.util.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Has to extend vanilla recipe for REI
 */
public class Recipe {

    public static int getMaxChancedValue() {
        return 10000;
    }

    private String name;
    private final List<RecipeItem> inputs;
    private final List<ItemStack> outputs;
    private final List<FluidStack> fluidInputs;
    private final List<FluidStack> fluidOutputs;
    private final List<ChanceEntry> chancedOutputs;
    private List<ChanceEntry> chancedInputs;

    private final int EUt, duration;
    private final boolean hidden;

    //private final RecipePropertyStorage recipePropertyStorage;

    private static final ItemStackHashStrategy hashStrategy = ItemStackHashStrategy.comparingAll();

    private final int hashCode;

    protected Recipe(String name, List<RecipeItem> inputs, List<ItemStack> outputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs, List<ChanceEntry> chancedOutputs, int eUt, int duration, boolean hidden) {
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
        this.chancedOutputs = chancedOutputs;
        EUt = eUt;
        this.duration = duration;
        this.hidden = hidden;
        this.hashCode = makeHashCode();
        if (this.name == null || this.name.isEmpty()) {
            this.name = makeName();
        }
    }

    private String makeName() {
        StringBuilder builder = new StringBuilder();
        for (ItemStack stack : outputs) {
            String[] parts = stack.getItem().getTranslationKey(stack).split("\\.");
            builder.append(parts[parts.length - 1], 0, 2);
        }
        for (FluidStack stack : fluidOutputs) {
            String[] parts = stack.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey().split("\\.");
            builder.append(parts[parts.length - 1], 0, 2);
        }
        builder.append(EUt)
                .append(duration);
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    public List<RecipeItem> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    public List<ItemStack> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    public List<FluidStack> getFluidInputs() {
        return Collections.unmodifiableList(fluidInputs);
    }

    public List<FluidStack> getFluidOutputs() {
        return Collections.unmodifiableList(fluidOutputs);
    }

    public int getEUt() {
        return EUt;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isHidden() {
        return hidden;
    }

    public final boolean matches(boolean consumeIfSuccessful, Inventory inputs, IFluidHandler fluidInputs, MatchingMode matchingMode) {
        return matches(consumeIfSuccessful, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), matchingMode);
    }

    public final boolean matches(boolean consumeIfSuccessful, Inventory inputs, IFluidHandler fluidInputs) {
        return matches(consumeIfSuccessful, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), MatchingMode.DEFAULT);
    }

    public final boolean matches(boolean consumeIfSuccessful, Storage<ItemVariant> inputs, Storage<FluidVariant> fluidInputs, MatchingMode matchingMode) {
        return matches(consumeIfSuccessful, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), matchingMode);
    }

    public final boolean matches(boolean consumeIfSuccessful, Storage<ItemVariant> inputs, Storage<FluidVariant> fluidInputs) {
        return matches(consumeIfSuccessful, TransferUtil.getItemsOf(inputs), TransferUtil.getFluidsOf(fluidInputs), MatchingMode.DEFAULT);
    }

    public boolean matches(boolean consumeIfSuccessful, List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        return matches(consumeIfSuccessful, inputs, fluidInputs, MatchingMode.DEFAULT);
    }

    /**
     * This methods aim to verify if the current recipe matches the given inputs according to matchingMode mode.
     *
     * @param consumeIfSuccessful if true and matchingMode is equal to {@link MatchingMode#DEFAULT} will consume the inputs of the recipe.
     * @param inputs              Items input or Collections.emptyList() if none.
     * @param fluidInputs         Fluids input or Collections.emptyList() if none.
     * @param matchingMode        How this method should check if inputs matches according to {@link MatchingMode} description.
     * @return true if the recipe matches the given inputs false otherwise.
     */
    public boolean matches(boolean consumeIfSuccessful, List<ItemStack> inputs, List<FluidStack> fluidInputs, MatchingMode matchingMode) {
        Pair<Boolean, Long[]> fluids = null;
        Pair<Boolean, Integer[]> items = null;

        if (matchingMode == MatchingMode.IGNORE_FLUIDS) {
            if (getInputs().isEmpty()) {
                return false;
            }
        } else {
            fluids = matchesFluid(fluidInputs);
            if (!fluids.getKey()) {
                return false;
            }
        }

        if (matchingMode == MatchingMode.IGNORE_ITEMS) {
            if (getFluidInputs().isEmpty()) {
                return false;
            }
        } else {
            items = matchesItems(inputs);
            if (!items.getKey()) {
                return false;
            }
        }

        if (consumeIfSuccessful && matchingMode == MatchingMode.DEFAULT) {
            Long[] fluidAmountInTank = fluids.getValue();
            Integer[] itemAmountInSlot = items.getValue();
            for (int i = 0; i < fluidAmountInTank.length; i++) {
                FluidStack fluidStack = fluidInputs.get(i);
                long fluidAmount = fluidAmountInTank[i];
                if (fluidStack == null || fluidStack.getAmount() == fluidAmount)
                    continue;
                fluidStack.setAmount(fluidAmount);
                if (fluidStack.getAmount() == 0)
                    fluidInputs.set(i, null);
            }
            for (int i = 0; i < itemAmountInSlot.length; i++) {
                ItemStack itemInSlot = inputs.get(i);
                int itemAmount = itemAmountInSlot[i];
                if (itemInSlot.isEmpty() || itemInSlot.getCount() == itemAmount)
                    continue;
                itemInSlot.setCount(itemAmountInSlot[i]);
            }
        }

        return true;
    }

    private Pair<Boolean, Integer[]> matchesItems(List<ItemStack> inputs) {
        Integer[] itemAmountInSlot = new Integer[inputs.size()];

        for (int i = 0; i < itemAmountInSlot.length; i++) {
            ItemStack itemInSlot = inputs.get(i);
            itemAmountInSlot[i] = itemInSlot.isEmpty() ? 0 : itemInSlot.getCount();
        }

        for (RecipeItem ingredient : this.inputs) {
            int ingredientAmount = ingredient.getAmount();
            boolean isNotConsumed = false;
            if (ingredientAmount == 0) {
                ingredientAmount = 1;
                isNotConsumed = true;
            }
            for (int i = 0; i < inputs.size(); i++) {
                ItemStack inputStack = inputs.get(i);
                if (inputStack.isEmpty() || !ingredient.test(inputStack))
                    continue;
                int itemAmountToConsume = Math.min(itemAmountInSlot[i], ingredientAmount);
                ingredientAmount -= itemAmountToConsume;
                if (!isNotConsumed) itemAmountInSlot[i] -= itemAmountToConsume;
                if (ingredientAmount == 0) break;
            }
            if (ingredientAmount > 0)
                return Pair.of(false, itemAmountInSlot);
        }

        return Pair.of(true, itemAmountInSlot);
    }

    private Pair<Boolean, Long[]> matchesFluid(List<FluidStack> fluidInputs) {
        Long[] fluidAmountInTank = new Long[fluidInputs.size()];

        for (int i = 0; i < fluidAmountInTank.length; i++) {
            FluidStack fluidInTank = fluidInputs.get(i);
            fluidAmountInTank[i] = fluidInTank == null ? 0 : fluidInTank.getAmount();
        }

        for (FluidStack fluid : this.fluidInputs) {
            long fluidAmount = fluid.getAmount();
            boolean isNotConsumed = false;
            if (fluidAmount == 0) {
                fluidAmount = 1;
                isNotConsumed = true;
            }
            for (int i = 0; i < fluidInputs.size(); i++) {
                FluidStack tankFluid = fluidInputs.get(i);
                if (tankFluid == null || !FluidStack.matchesStack(tankFluid, fluid))
                    continue;
                long fluidAmountToConsume = Math.min(fluidAmountInTank[i], fluidAmount);
                fluidAmount -= fluidAmountToConsume;
                if (!isNotConsumed) fluidAmountInTank[i] -= fluidAmountToConsume;
                if (fluidAmount == 0) break;
            }
            if (fluidAmount > 0)
                return Pair.of(false, fluidAmountInTank);
        }
        return Pair.of(true, fluidAmountInTank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return this.EUt == recipe.EUt &&
                this.duration == recipe.duration &&
                hasSameInputs(recipe) &&
                hasSameOutputs(recipe) &&
                hasSameChancedOutputs(recipe) &&
                hasSameFluidInputs(recipe) &&
                hasSameFluidOutputs(recipe);// &&
        //hasSameRecipeProperties(recipe);
    }

    private int makeHashCode() {
        int hash = Objects.hash(EUt, duration);
        hash += hashInputs() * 7;
        hash += hashOutputs() * 11;
        hash += hashChancedOutputs() * 13;
        hash += hashFluidList(this.fluidInputs) * 17;
        hash += hashFluidList(this.fluidOutputs) * 19;
        //hash += hashRecipeProperties() * 23;
        return hash;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    private int hashInputs() {
        int hash = 0;
        for (RecipeItem countableIngredient : this.inputs) {
            for (ItemStack is : countableIngredient.getAllValid()) {
                hash += ItemStackHashStrategy.comparingAllButCount().hashCode(is);
                hash += countableIngredient.getAmount();
            }
        }
        return hash;
    }

    private boolean hasSameInputs(Recipe otherRecipe) {
        if (this.inputs.size() != otherRecipe.inputs.size()) return false;
        for (int i = 0; i < inputs.size(); i++) {
            for (int j = 0; j < this.inputs.get(i).getAllValid().size(); j++) {
                if (!hashStrategy.equals(this.inputs.get(i).getAllValid().get(j),
                        otherRecipe.inputs.get(i).getAllValid().get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private int hashOutputs() {
        int hash = 0;
        for (ItemStack is : this.outputs) {
            hash += hashStrategy.hashCode(is);
        }
        return hash;
    }

    private boolean hasSameOutputs(Recipe otherRecipe) {
        if (this.outputs.size() != otherRecipe.outputs.size()) return false;
        for (int i = 0; i < outputs.size(); i++) {
            if (!hashStrategy.equals(this.outputs.get(i), otherRecipe.outputs.get(i))) {
                return false;
            }
        }
        return true;
    }

    private int hashChancedOutputs() {
        int hash = 0;
        for (ChanceEntry chanceEntry : this.chancedOutputs) {
            hash += hashStrategy.hashCode(chanceEntry.itemStack);
            hash += chanceEntry.chance;
            hash += chanceEntry.boostPerTier;
        }
        return hash;
    }

    private boolean hasSameChancedOutputs(Recipe otherRecipe) {
        if (this.chancedOutputs.size() != otherRecipe.chancedOutputs.size()) return false;
        for (int i = 0; i < chancedOutputs.size(); i++) {
            if (!hashStrategy.equals(this.chancedOutputs.get(i).itemStack, otherRecipe.chancedOutputs.get(i).itemStack)) {
                return false;
            }
        }
        return true;
    }

    public int hashFluidList(List<FluidStack> fluids) {
        int hash = 0;
        for (FluidStack fluidStack : fluids) {
            hash += fluidStack.asFluidVariant().hashCode();
        }
        return hash;
    }

    private boolean hasSameFluidInputs(Recipe otherRecipe) {
        if (this.fluidInputs.size() != otherRecipe.fluidInputs.size()) return false;
        for (int i = 0; i < fluidInputs.size(); i++) {
            if (!fluidInputs.get(i).isEqual(otherRecipe.fluidInputs.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSameFluidOutputs(Recipe otherRecipe) {
        if (this.fluidOutputs.size() != otherRecipe.fluidOutputs.size()) return false;
        for (int i = 0; i < fluidOutputs.size(); i++) {
            if (!fluidOutputs.get(i).isEqual(otherRecipe.fluidOutputs.get(i))) {
                return false;
            }
        }
        return true;
    }

    /*private int hashRecipeProperties() {
        int hash = 0;
        for (Map.Entry<RecipeProperty<?>, Object> propertyObjectEntry : this.recipePropertyStorage.getRecipeProperties()) {
            hash += propertyObjectEntry.getKey().hashCode();
        }
        return hash;
    }

    private boolean hasSameRecipeProperties(Recipe otherRecipe) {
        if (this.getPropertyCount() != otherRecipe.getPropertyCount()) return false;
        return this.recipePropertyStorage.getRecipeProperties().containsAll(otherRecipe.recipePropertyStorage.getRecipeProperties());
    }*/

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("inputs", inputs)
                .append("outputs", outputs)
                .append("chancedOutputs", chancedOutputs)
                .append("fluidInputs", fluidInputs)
                .append("fluidOutputs", fluidOutputs)
                .append("duration", duration)
                .append("EUt", EUt)
                .append("hidden", hidden)
                .toString();
    }

    ///////////////////
    //    Getters    //
    ///////////////////

    public List<ItemStack> getResultItemOutputs(int maxOutputSlots, Random random, int tier) {
        List<ItemStack> outputs = TransferUtil.copyStackList(getOutputs());
        List<ChanceEntry> chancedOutputsList = getChancedOutputs();
        int maxChancedSlots = maxOutputSlots - outputs.size();
        if (chancedOutputsList.size() > maxChancedSlots) {
            chancedOutputsList = chancedOutputsList.subList(0, Math.max(0, maxChancedSlots));
        }
        for (ChanceEntry chancedOutput : chancedOutputsList) {
            int outputChance = RecipeTable.getChanceFunction().chanceFor(chancedOutput.getChance(), chancedOutput.getBoostPerTier(), tier);
            if (random.nextInt(Recipe.getMaxChancedValue()) <= outputChance) {
                outputs.add(chancedOutput.getItemStack().copy());
            }
        }
        return outputs;
    }

    public List<ItemStack> getAllItemOutputs(int maxOutputSlots) {
        List<ItemStack> outputs = TransferUtil.copyStackList(getOutputs());
        outputs.addAll(getChancedOutputs().stream().map(ChanceEntry::getItemStack).collect(Collectors.toList()));
        if (outputs.size() > maxOutputSlots) {
            outputs = outputs.subList(0, maxOutputSlots);
        }
        return outputs;
    }

    public List<ChanceEntry> getChancedOutputs() {
        return chancedOutputs;
    }

    public boolean hasInputFluid(FluidStack fluid) {
        for (FluidStack fluidStack : fluidInputs) {
            if (FluidStack.matchesStack(fluidStack, fluid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotConsumedInput(Object stack) {
        if (stack instanceof FluidStack) {
            if (fluidInputs.contains(stack)) {
                return fluidInputs.get(fluidInputs.indexOf(stack)).getAmount() == 0;
            } else return false;
        } else if (stack instanceof ItemStack) {
            for (RecipeItem ing : inputs) {
                if (ing.getAmount() != 0) continue;
                for (ItemStack inputStack : ing.getAllValid()) {
                    if (inputStack.getItem() == ((ItemStack) stack).getItem()
                            //&& inputStack.getItemDamage() == ((ItemStack) stack).getItemDamage()
                            && Objects.equals(inputStack.getNbt(), ((ItemStack) stack).getNbt())) {
                        return true;
                    }
                }
            }
            return false;
        } else return false;
    }

    public boolean hasValidInputsForDisplay() {
        boolean hasValidInputs = true;
        for (RecipeItem ingredient : inputs) {
            List<ItemStack> matchingItems = ingredient.getAllValid();
            hasValidInputs &= matchingItems.stream().anyMatch(s -> !s.isEmpty());
        }
        return hasValidInputs;
    }

    ///////////////////////////////////////////////////////////
    //               Property Helper Methods                 //
    ///////////////////////////////////////////////////////////
    /*public <T> T getProperty(RecipeProperty<T> property, T defaultValue) {
        return recipePropertyStorage.getRecipePropertyValue(property, defaultValue);
    }

    public Object getPropertyRaw(String key) {
        return recipePropertyStorage.getRawRecipePropertyValue(key);
    }

    public boolean setProperty(RecipeProperty<?> property, Object value) {
        return recipePropertyStorage.store(property, value);
    }

    public Set<Map.Entry<RecipeProperty<?>, Object>> getPropertyValues() {
        return recipePropertyStorage.getRecipeProperties();
    }

    public Set<String> getPropertyKeys() {
        return recipePropertyStorage.getRecipePropertyKeys();
    }

    public boolean hasProperty(RecipeProperty<?> property) {
        return recipePropertyStorage.hasRecipeProperty(property);
    }

    public int getPropertyCount() {
        return recipePropertyStorage.getSize();
    }*/

    ///////////////////////////////////////////////////////////
    //                   Chanced Output                      //
    ///////////////////////////////////////////////////////////
    public static class ChanceEntry {
        private final ItemStack itemStack;
        private final int chance;
        private final int boostPerTier;

        public ChanceEntry(ItemStack itemStack, int chance, int boostPerTier) {
            this.itemStack = itemStack.copy();
            this.chance = chance;
            this.boostPerTier = boostPerTier;
        }

        public ItemStack getItemStack() {
            return itemStack.copy();
        }

        public int getChance() {
            return chance;
        }

        public int getBoostPerTier() {
            return boostPerTier;
        }

        public ChanceEntry copy() {
            return new ChanceEntry(itemStack, chance, boostPerTier);
        }
    }
}
