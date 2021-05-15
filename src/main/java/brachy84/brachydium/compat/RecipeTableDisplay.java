package brachy84.brachydium.compat;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.recipe.MTRecipe;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeTableDisplay implements RecipeDisplay {

    private Identifier category;
    private MTRecipe recipe;

    public RecipeTableDisplay(MTRecipe recipe, Identifier category) {
        this.category = category;
        this.recipe = recipe;
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return Stream.concat(
                recipe.getInputs().stream().map(CountableIngredient::toEntryStack),
                recipe.getFluidInputs().stream().map(FluidStack::toEntryStack)
        ).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return Stream.concat(
                recipe.getOutputs().stream().map(RecipeTableDisplay::toEntryStack),
                recipe.getFluidOutputs().stream().map(FluidStack::toEntryStack)
        ).collect(Collectors.toList());
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return category;
    }

    public static List<EntryStack> toEntryStack(ItemStack stack) {
        return Lists.newArrayList(EntryStack.create(stack));
    }

    public int getEUt() {
        return recipe.getEUt();
    }

    public int getDuration() {
        return recipe.getDuration();
    }
}
