package brachy84.brachydium.compat.rei;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.recipe.Recipe;
import brachy84.brachydium.api.recipe.RecipeItem;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeTableDisplay implements Display {

    private final CategoryIdentifier<RecipeTableDisplay> category;
    private final Recipe recipe;

    public RecipeTableDisplay(Recipe recipe, CategoryIdentifier<RecipeTableDisplay> category) {
        this.category = Objects.requireNonNull(category);
        this.recipe = Objects.requireNonNull(recipe);
    }

    public static EntryIngredient toEntryStack(ItemStack stack) {
        return EntryIngredients.of(stack);
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return Stream.concat(
                getItemInputs(),
                getFluidInputs()
        ).collect(Collectors.toList());
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Stream.concat(
                getItemOutputs(),
                getFluidOutputs()
        ).collect(Collectors.toList());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return category;
    }

    public Stream<EntryIngredient> getItemInputs() {
        return recipe.getInputs().stream().map(RecipeItem::toEntryStack);
    }

    public Stream<EntryIngredient> getFluidInputs() {
        return recipe.getFluidInputs().stream().map(FluidStack::toEntryStack);
    }

    public Stream<EntryIngredient> getItemOutputs() {
        return recipe.getOutputs().stream().map(RecipeTableDisplay::toEntryStack);
    }

    public Stream<EntryIngredient> getFluidOutputs() {
        return recipe.getFluidOutputs().stream().map(FluidStack::toEntryStack);
    }

    public int getEUt() {
        return recipe.getEUt();
    }

    public int getDuration() {
        return recipe.getDuration();
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
