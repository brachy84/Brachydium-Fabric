package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class InventoryHelper {

    public static boolean extractIngredient(Extractable<ItemKey> extractable, Transaction transaction, Ingredient i, int amount) {
        return extractIngredient(extractable, transaction, new CountableIngredient(i, amount));
    }

    public static boolean extractIngredient(Extractable<ItemKey> extractable, Transaction transaction, CountableIngredient ci) {
        int extracted = 0;
        for (ItemStack stack : ci.getIngredient().getMatchingStacksClient()) {
            int toExtract = ci.getAmount() - extracted;
            extracted += extractable.extract(transaction, ItemKey.of(stack), toExtract);
            if (extracted == ci.getAmount()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasIngredientsAndFluids(Extractable<ItemKey> itemExtractable, Extractable<Fluid> fluidExtractable, List<CountableIngredient> ingredients, List<FluidStack> fluidStacks) {
        try (Transaction transaction = Transaction.create()) {
            boolean success = hasIngredients(transaction, itemExtractable, ingredients);
            success &= hasFluids(transaction, fluidExtractable, fluidStacks);
            transaction.abort();
            return success;
        }
    }

    public static boolean hasIngredients(Transaction transaction, Extractable<ItemKey> extractable, List<CountableIngredient> stacks) {
        for (CountableIngredient ci : stacks) {
            try(Transaction transaction1 = transaction.nest()) {
                if (!extractIngredient(extractable, transaction1, ci)) {
                    transaction1.abort();
                    return false;
                }
                transaction1.abort();
            }
        }
        return true;
    }

    public static boolean hasFluids(Transaction transaction, Extractable<Fluid> extractable, List<FluidStack> stacks) {
        for (FluidStack stack : stacks) {
            if (extractable.extract(transaction, stack.getFluid(), stack.getAmount()) != stack.getAmount()) {
                return false;
            }
        }
        return true;
    }
}
