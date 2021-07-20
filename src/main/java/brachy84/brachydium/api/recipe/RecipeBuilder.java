package brachy84.brachydium.api.recipe;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.tag.TagDictionary;
import brachy84.brachydium.api.util.CrypticNumber;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;

import java.util.*;
import java.util.stream.Collectors;

public abstract class RecipeBuilder<R extends RecipeBuilder<R>> {

    private static final CrypticNumber nameGenerator = new CrypticNumber(4);

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

    protected RecipeBuilder(Recipe recipe, RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
        this.inputs.addAll(recipe.getInputs());
        this.outputs.addAll(recipe.getOutputs());

        this.fluidInputs.addAll(recipe.getFluidInputs());
        this.fluidOutputs.addAll(recipe.getFluidOutputs());

        this.duration = recipe.getDuration();
        this.EUt = recipe.getEUt();
        this.hidden = recipe.isHidden();
    }

    @SuppressWarnings("all")
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

    @SuppressWarnings("unchecked")
    public R inputs(Collection<CountableIngredient> inputs) {
        for (CountableIngredient ci : inputs) {
            if (ci != null) {
                this.inputs.add(ci);
            } else {
                Brachydium.LOGGER.fatal("Can't add null ingredient");
            }
        }
        return (R) this;
    }

    public R inputs(CountableIngredient... inputs) {
        return inputs(Arrays.asList(inputs));
    }

    public R input(CountableIngredient ingredient) {
        return inputs(ingredient);
    }

    public R input(String tag, int amount) {
        return inputs(CountableIngredient.of(tag, amount));
    }

    public R input(Tag<Item> tag, int amount) {
        return inputs(new CountableIngredient(Ingredient.fromTag(tag), amount));
    }

    public R input(TagDictionary.Entry tag, Material material, int amount) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(material);
        return input(tag.getStringTag(material), amount);
    }

    public R input(Item item, int amount) {
        return input(new ItemStack(item, amount));
    }

    public R input(ItemStack... inputs) {
        return inputs(Arrays.stream(inputs).map(CountableIngredient::new).collect(Collectors.toList()));
    }

    public R input(Ingredient input, int amount) {
        return inputs(new CountableIngredient(input, amount));
    }

    public R output(Item item, int amount) {
        return outputs(new ItemStack(item, amount));
    }

    @SuppressWarnings("unchecked")
    public R outputs(ItemStack... outputs) {
        Collections.addAll(this.outputs, outputs);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R fluidInputs(FluidStack... fluidInputs) {
        Collections.addAll(this.fluidInputs, fluidInputs);
        return (R) this;
    }

    public R fluidInput(Fluid fluid, int amount) {
        return fluidInputs(new FluidStack(fluid, amount));
    }

    @SuppressWarnings("unchecked")
    public R fluidOutputs(FluidStack... fluidOutputs) {
        this.fluidOutputs.addAll(fluidInputs);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R duration(int duration) {
        this.duration = duration;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R EUt(int EUt) {
        this.EUt = EUt;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R name(String name) {
        this.name = name;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R hidden() {
        this.hidden = true;
        return (R) this;
    }

    public R property(String key, String value) {
        throw new UnsupportedOperationException("Properties are not yet implemented");
    }

    public R property(String key, int value) {
        throw new UnsupportedOperationException("Properties are not yet implemented");
    }

    public Recipe buildAndRegister() {
        if(validate()) {
            if(name == null || name.trim().equals("")) {
                String output = "";
                if(outputs.size() > 0) {
                    output = outputs.get(0).toString().split(" ")[1];
                } else if(fluidOutputs.size() > 0){
                    output = fluidOutputs.get(0).toString().split(" ")[0];
                }
                name = String.format("%s_%s_%s", recipeTable.unlocalizedName, output, nameGenerator.next());
            }
            Recipe recipe = new Recipe(name, inputs, outputs, fluidInputs, fluidOutputs, EUt, duration, hidden);
            recipeTable.addRecipe(recipe);
            Brachydium.LOGGER.info("Registering recipe ({}) for {}", recipe.getName(), recipeTable.unlocalizedName);
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
        boolean matches;
        matches = inputs.size() >= recipeTable.getMinInputs() && inputs.size() <= recipeTable.getMaxInputs();
        if(!matches) return false;
        matches = outputs.size() >= recipeTable.getMinOutputs() && outputs.size() <= recipeTable.getMaxOutputs();
        if(!matches) return false;
        matches = fluidInputs.size() >= recipeTable.getMinFluidInputs() && fluidInputs.size() <= recipeTable.getMaxFluidInputs();
        if(!matches) return false;
        matches = fluidOutputs.size() >= recipeTable.getMinFluidOutputs() && fluidOutputs.size() <= recipeTable.getMaxFluidOutputs();
        if(!matches) return false;

        return EUt != 0 && duration > 0;
    }

    public void setRecipeTable(RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
    }
}
