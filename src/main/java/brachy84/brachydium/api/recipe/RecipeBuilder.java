package brachy84.brachydium.api.recipe;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import brachy84.brachydium.api.util.CrypticNumber;
import brachy84.brachydium.api.util.ValidationResult;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public abstract class RecipeBuilder<R extends RecipeBuilder<R>> {

    private static final CrypticNumber nameGenerator = new CrypticNumber(4);

    private RecipeTable<R> recipeTable;

    private String name;
    private final List<RecipeItem> inputs = new ArrayList<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private final List<FluidStack> fluidInputs = new ArrayList<>();
    private final List<FluidStack> fluidOutputs = new ArrayList<>();
    protected final List<Recipe.ChanceEntry> chancedOutputs = new ArrayList<>();

    private int duration, EUt;
    private boolean hidden = false;

    protected ValidationResult.State recipeStatus = ValidationResult.State.VALID;

    protected RecipeBuilder() {
    }

    protected RecipeBuilder(Recipe recipe, RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
        this.name = recipe.getName();
        this.inputs.clear();
        this.inputs.addAll(recipe.getInputs());
        this.outputs.clear();
        this.outputs.addAll(recipe.getOutputs());
        this.fluidInputs.clear();
        this.fluidInputs.addAll(recipe.getFluidInputs());
        this.fluidOutputs.clear();
        this.fluidOutputs.addAll(recipe.getFluidOutputs());
        this.chancedOutputs.clear();
        this.chancedOutputs.addAll(recipe.getChancedOutputs());

        this.duration = recipe.getDuration();
        this.EUt = recipe.getEUt();
        this.hidden = recipe.isHidden();
    }

    @SuppressWarnings("all")
    protected RecipeBuilder(RecipeBuilder<R> recipeBuilder) {
        this.recipeTable = recipeBuilder.recipeTable;
        this.name = recipeBuilder.name;
        this.inputs.clear();
        this.inputs.addAll(recipeBuilder.inputs);
        this.outputs.clear();
        this.outputs.addAll(recipeBuilder.outputs);
        this.chancedOutputs.clear();
        this.chancedOutputs.addAll(recipeBuilder.chancedOutputs);
        this.fluidInputs.clear();
        this.fluidInputs.addAll(recipeBuilder.fluidInputs);
        this.fluidOutputs.clear();
        this.fluidOutputs.addAll(recipeBuilder.fluidOutputs);
        this.EUt = recipeBuilder.EUt;
        this.duration = recipeBuilder.duration;
        this.hidden = recipeBuilder.hidden;
    }

    @SuppressWarnings("unchecked")
    public R name(String name) {
        this.name = name;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R inputs(Collection<RecipeItem> inputs) {
        for (RecipeItem ci : inputs) {
            if (ci != null) {
                this.inputs.add(ci);
            } else {
                Brachydium.LOGGER.fatal("Can't add null ingredient");
            }
        }
        return (R) this;
    }

    public R inputs(RecipeItem... inputs) {
        return inputs(Arrays.asList(inputs));
    }

    public R input(RecipeItem ingredient) {
        return inputs(ingredient);
    }

    public R input(String tag, int amount) {
        return inputs(RecipeItem.ofTagId(new Identifier(tag), amount, 1f));
    }

    public R input(Tag<Item> tag, int amount) {
        return inputs(new RecipeItem(tag, amount, 1f));
    }

    public R input(TagDictionary.Entry tag, Material material, int amount) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(material);
        return input(RecipeItem.ofTagId(tag.createTagId(material), amount, 1f));
    }

    public R input(Item item, int amount) {
        return input(new ItemStack(item, amount));
    }

    public R input(ItemStack... inputs) {
        return inputs(Arrays.stream(inputs).map(RecipeItem::new).collect(Collectors.toList()));
    }

    public R input(Ingredient input, int amount) {
        return inputs(RecipeItem.of(input, amount, 1f));
    }

    public R notConsumable(ItemStack itemStack) {
        return inputs(new RecipeItem(itemStack, 0f));
    }

    public R notConsumable(TagDictionary.Entry prefix, Material material) {
        return input(prefix, material, 0);
    }

    public R notConsumable(Ingredient ingredient) {
        return inputs(RecipeItem.of(ingredient, 1, 0f));
    }

    public R notConsumable(Fluid fluid) {
        return fluidInputs(new FluidStack(fluid, 0));
    }

    public R notConsumable(FluidStack fluidStack) {
        return fluidInputs(fluidStack.copyWith(0));
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

    public R chancedOutput(ItemStack stack, int chance, int tierChanceBoost) {
        if (stack == null || stack.isEmpty()) {
            return (R) this;
        }
        if (0 >= chance || chance > Recipe.getMaxChancedValue()) {
            Brachydium.LOGGER.error("Chance cannot be less or equal to 0 or more than {}. Actual: {}.", Recipe.getMaxChancedValue(), chance);
            Brachydium.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            //recipeStatus = EnumValidationResult.INVALID;
            return (R) this;
        }
        this.chancedOutputs.add(new Recipe.ChanceEntry(stack.copy(), chance, tierChanceBoost));
        return (R) this;
    }

    /*public R chancedOutput(TagDictionary.Entry tag, Material material, int count, int chance, int tierChanceBoost) {
        return chancedOutput(OreDictUnifier.get(tag, material, count), chance, tierChanceBoost);
    }

    public R chancedOutput(TagDictionary.Entry prefix, Material material, int chance, int tierChanceBoost) {
        return chancedOutput(prefix, material, 1, chance, tierChanceBoost);
    }

    public R chancedOutput(MetaItem<?>.MetaValueItem item, int count, int chance, int tierChanceBoost) {
        return chancedOutput(item.getStackForm(count), chance, tierChanceBoost);
    }

    public R chancedOutput(MetaItem<?>.MetaValueItem item, int chance, int tierChanceBoost) {
        return chancedOutput(item, 1, chance, tierChanceBoost);
    }*/

    public R chancedOutputs(List<Recipe.ChanceEntry> chancedOutputs) {
        chancedOutputs.stream().map(Recipe.ChanceEntry::copy).forEach(this.chancedOutputs::add);
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
        Recipe recipe = new Recipe(name, inputs, outputs, fluidInputs, fluidOutputs, chancedOutputs, EUt, duration, hidden);
        ValidationResult<Recipe> validationResult = build(recipe);
        recipeTable.addRecipe(validationResult);
        Brachydium.LOGGER.info("Registering recipe for {}", recipeTable.unlocalizedName);
        return recipe;
    }

    public abstract R copy();

    public ValidationResult<Recipe> build(Recipe recipe) {
        return new ValidationResult<>(validate(), recipe);
    }

    protected ValidationResult.State validate() {
        if (EUt == 0) {
            Brachydium.LOGGER.error("EU/t cannot be equal to 0", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        if (duration <= 0) {
            Brachydium.LOGGER.error("Duration cannot be less or equal to 0", new IllegalArgumentException());
            recipeStatus = ValidationResult.State.INVALID;
        }
        if (recipeStatus == ValidationResult.State.INVALID) {
            Brachydium.LOGGER.error("Invalid recipe, read the errors above: {}", this);
        }
        return recipeStatus;
    }

    public void setRecipeTable(RecipeTable<R> recipeTable) {
        this.recipeTable = recipeTable;
    }
}
