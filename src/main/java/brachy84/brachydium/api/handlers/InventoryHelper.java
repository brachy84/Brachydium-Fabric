package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.util.MatchingType;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Extractable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Iterator;
import java.util.List;

public class InventoryHelper {

    public static boolean containsIngredient(List<ItemStack> items, CountableIngredient ci) {
        int count = 0;
        for (ItemStack item : items) {
            if (ci.matches(item, MatchingType.IGNORE_AMOUNT)) {
                count += item.getCount();
            }
            if(count >= ci.getAmount())
                return true;
        }
        return false;
    }

    public static boolean containsFluidStack(List<FluidStack> fluids, FluidStack fluid) {
        int count = 0;
        for (FluidStack stack : fluids) {
            if (FluidStack.matchesStack(fluid, stack)) {
                count += stack.getAmount();
            }
            if(count >= fluid.getAmount())
                return true;
        }
        return false;
    }

    public static int getStorageSize(Storage<?> storage) {
        if (storage instanceof CombinedStorage<?, ?> combinedStorage)
            return combinedStorage.parts.size();
        if (storage instanceof SingleSlotStorage)
            return 1;

        int i = 0;
        Transaction transaction = Transaction.openOuter();
        Iterator<? extends StorageView<?>> iterator = storage.iterator(transaction);
        for (; iterator.hasNext(); ++i)
            iterator.next();
        transaction.close();
        return i;
    }

    /*public static boolean extractIngredient(Extractable<ItemKey> extractable, Transaction transaction, Ingredient i, int amount) {
        return extractIngredient(extractable, transaction, new CountableIngredient(i, amount));
    }

    public static boolean extractIngredient(Extractable<ItemKey> extractable, Transaction transaction, CountableIngredient ci) {
        int extracted = 0;
        for (ItemStack stack : ci.getIngredient().getMatchingStacks()) {
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
    }*/
}
