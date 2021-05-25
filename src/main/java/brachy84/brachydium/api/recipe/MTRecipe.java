package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.Brachydium;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Has to extend vanilla recipe for REI
 */
public class MTRecipe implements Recipe<Inventory> {

    final MTRecipeType type;

    private final Identifier id;
    private final List<CountableIngredient> inputs;
    private final List<ItemStack> outputs;
    private final List<FluidStack> fluidInputs;
    private final List<FluidStack> fluidOutputs;

    private final int EUt, duration;
    private final boolean hidden;

    protected MTRecipe(MTRecipeType type, String name, List<CountableIngredient> inputs, List<ItemStack> outputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs, int eUt, int duration, boolean hidden) {
        this.type = type;
        this.id = new Identifier(Brachydium.MOD_ID, name);
        this.inputs = inputs;
        this.outputs = outputs;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
        EUt = eUt;
        this.duration = duration;
        this.hidden = hidden;
    }

    public String getName() {
        return id.getPath();
    }

    public List<CountableIngredient> getInputs() {
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

    /**
     * This will throw an UnsupportedOperationException !
     * Don't use this!!!
     */
    @Deprecated
    @Override
    public boolean matches(Inventory inv, World world) {
        throw new UnsupportedOperationException();
    }

    /**
     * This will throw an UnsupportedOperationException !
     * Don't use this!!!
     */
    @Deprecated
    @Override
    public ItemStack craft(Inventory inv) {
        throw new UnsupportedOperationException();
    }

    /**
     * This will throw an UnsupportedOperationException !
     * Don't use this!!!
     */
    @Deprecated
    @Override
    public boolean fits(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> ingredients = (DefaultedList<Ingredient>) inputs.stream().map(i -> {
            for(ItemStack stack : i.getIngredient().getMatchingStacksClient()) {
                stack.setCount(i.getAmount());
            }
            return i.getIngredient();
        }).collect(Collectors.toList());

        return ingredients;
    }

    /**
     * This is the method from vanilla Recipe
     * Use {@Link #getOutputs()}
     * @return only the first output
     */
    @Override
    public ItemStack getOutput() {
        if(outputs != null && outputs.size() > 0 && outputs.get(0) != ItemStack.EMPTY) {
            return outputs.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return type;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }
}
