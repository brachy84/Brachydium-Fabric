package brachy84.brachydium.api.recipe;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.RandomString;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RecipeBuilder<R extends RecipeBuilder<R>> {

    private RecipeTable<R> recipeTable;

    private String name;

    private final List<CountableIngredient> inputs = new ArrayList<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private final List<FluidStack> fluidInputs = new ArrayList<>();
    private final List<FluidStack> fluidOutputs = new ArrayList<>();

    private int duration, EUt;
    private boolean hidden = false;

    protected RecipeBuilder() {
    }

    protected RecipeBuilder(MTRecipe recipe, RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
        this.inputs.addAll(recipe.getInputs());
        this.outputs.addAll(recipe.getOutputs());

        this.fluidInputs.addAll(recipe.getFluidInputs());
        this.fluidOutputs.addAll(recipe.getFluidOutputs());

        this.duration = recipe.getDuration();
        this.EUt = recipe.getEUt();
        this.hidden = recipe.isHidden();
    }

    protected RecipeBuilder(RecipeBuilder<R> recipeBuilder) {
        this.recipeTable = recipeBuilder.recipeTable;
        this.inputs.addAll(recipeBuilder.inputs);
        this.outputs.addAll(recipeBuilder.outputs);

        this.fluidInputs.addAll(recipeBuilder.fluidInputs);
        this.fluidOutputs.addAll(recipeBuilder.fluidOutputs);
        this.EUt = recipeBuilder.EUt;
        this.duration = recipeBuilder.duration;
        this.hidden = recipeBuilder.hidden;
    }

    public R inputs(CountableIngredient... inputs) {
        Collections.addAll(this.inputs, inputs);
        return (R) this;
    }

    public R input(String tag, int amount) {
        this.inputs.add(CountableIngredient.of(tag, amount));
        return (R) this;
    }

    public R input(String component, Material material, int amount) {
        if(!component.endsWith("s")) Brachydium.LOGGER.warn(component + " doesn't end with 's'. All oreDict tags should end with s (f.e. ingots)");
        this.inputs.add(CountableIngredient.of("c:" + material.getName() + "_" + component, amount));
        return (R) this;
    }

    public R input(Item item, int amount) {
        return input(new ItemStack(item, amount));
    }

    public R input(ItemStack... inputs) {
        for(ItemStack stack : inputs) {
            this.inputs.add(new CountableIngredient(stack));
        }
        return (R) this;
    }

    public R input(Ingredient input, int amount) {
        this.inputs.add(new CountableIngredient(input, amount));
        return (R) this;
    }

    public R output(Item item, int amount) {
        this.outputs.add(new ItemStack(item, amount));
        return (R) this;
    }

    public R outputs(ItemStack... outputs) {
        Collections.addAll(this.outputs, outputs);
        return (R) this;
    }

    public R fluidInputs(FluidStack... fluidInputs) {
        Collections.addAll(this.fluidInputs, fluidInputs);
        return (R) this;
    }

    public R fluidInput(Fluid fluid, int amount) {
        return fluidInputs(new FluidStack(fluid, amount));
    }

    public R fluidOutputs(FluidStack... fluidOutputs) {
        for(FluidStack fluidStack : fluidInputs) {
            this.fluidInputs.add(fluidStack);
        }
        return (R) this;
    }

    public R duration(int duration) {
        this.duration = duration;
        return (R) this;
    }

    public R EUt(int EUt) {
        this.EUt = EUt;
        return (R) this;
    }

    public R hidden() {
        this.hidden = true;
        return (R) this;
    }

    public R property(String key, String value) {

        return (R) this;
    }

    public R property(String key, int value) {

        return (R) this;
    }

    public MTRecipe buildAndRegister() {
        if(validate()) {
            MTRecipe recipe = new MTRecipe(null, name, inputs, outputs, fluidInputs, fluidOutputs, EUt, duration, hidden);
            recipeTable.addRecipe(recipe);
            if(name == null || name.trim().equals("")) {
                String output = "";
                if(outputs.size() > 0) {
                    output = outputs.get(0).toString().split(" ")[1];
                } else if(fluidOutputs.size() > 0){
                    output = fluidOutputs.get(0).toString().split(" ")[0];
                }
                String key;
                do {
                    key = output + "_" + RandomString.create(4);
                } while (recipeTable.hasRecipeKey(key));
                name = key;
            }
            return recipe;
        }
        return null;
    }

    public abstract R copy();

    public R copyWithName(String name) {
        R builder = copy();
        builder.setName(name);
        return builder;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected boolean validate() {
        boolean matchesII, matchesIO, matchesFI, matchesFO;
        matchesII = inputs.size() >= recipeTable.getMinInputs() && inputs.size() <= recipeTable.getMaxInputs();
        if(!matchesII) return false;
        matchesIO = outputs.size() >= recipeTable.getMinOutputs() && outputs.size() <= recipeTable.getMaxOutputs();
        if(!matchesIO) return false;
        matchesFI = fluidInputs.size() >= recipeTable.getMinFluidInputs() && fluidInputs.size() <= recipeTable.getMaxFluidInputs();
        if(!matchesFI) return false;
        matchesFO = fluidOutputs.size() >= recipeTable.getMaxFluidOutputs() && fluidOutputs.size() <= recipeTable.getMaxFluidOutputs();
        if(!matchesFO) return false;

        return EUt != 0 && duration > 0;
    }

    public void setRecipeTable(RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
    }
}
