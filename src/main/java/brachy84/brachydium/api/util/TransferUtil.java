package brachy84.brachydium.api.util;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.recipe.RecipeItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TransferUtil {

    private TransferUtil() {
    }

    private static final Map<Fluid, Item> BUCKET_ITEMS = new HashMap<>();

    @Nullable
    public static Item getBucketItem(Fluid fluid) {
        return BUCKET_ITEMS.computeIfAbsent(fluid, key -> {
            for (Item item : Registry.ITEM) {
                if (item instanceof BucketItemAccessor bucketItem && bucketItem.fabric_getFluid() == fluid) {
                    return item;
                }
            }
            return null;
        });
    }

    public static ItemStack itemStackWith(ItemStack stack, int count) {
        count = Math.min(stack.getMaxCount(), Math.max(0, count));
        if (count == 0)
            return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    public static List<ItemStack> getItemsOf(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty())
                items.add(stack);
        }
        return items;
    }

    public static List<FluidStack> getFluidsOf(IFluidHandler fluidHandler) {
        List<FluidStack> fluids = new ArrayList<>();
        for (int i = 0; i < fluidHandler.size(); i++) {
            FluidStack stack = fluidHandler.getFluid(i);
            if (!stack.isEmpty())
                fluids.add(stack);
        }
        return fluids;
    }

    public static List<ItemStack> getItemsOf(Storage<ItemVariant> inventory) {
        List<ItemStack> items = new ArrayList<>();
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> storageView : inventory.iterable(transaction)) {
                if (storageView.isResourceBlank() || storageView.getAmount() <= 0)
                    continue;
                items.add(storageView.getResource().toStack((int) storageView.getAmount()));
            }
        }
        return items;
    }

    public static List<FluidStack> getFluidsOf(Storage<FluidVariant> fluidHandler) {
        List<FluidStack> fluids = new ArrayList<>();
        try (Transaction transaction = Transaction.openOuter()) {
            Iterator<StorageView<FluidVariant>> fluidIterator = fluidHandler.iterator(transaction);
            while (fluidIterator.hasNext()) {
                StorageView<FluidVariant> storageView = fluidIterator.next();
                FluidStack stack = new FluidStack(storageView.getResource(), storageView.getAmount());
                if (!stack.isEmpty()) {
                    fluids.add(stack);
                }
            }
        }
        return fluids;
    }

    public static List<ItemStack> copyStackList(List<ItemStack> itemStacks) {
        return itemStacks.stream().map(stack -> {
            if (stack.isEmpty())
                return ItemStack.EMPTY;
            return stack.copy();
        }).collect(Collectors.toList());
    }

    public static List<FluidStack> copyFluidList(List<FluidStack> fluidStacks) {
        return fluidStacks.stream().map(stack -> {
            if (stack.isEmpty())
                return FluidStack.EMPTY;
            return stack.copy();
        }).collect(Collectors.toList());
    }

    public static boolean putItems(Storage<ItemVariant> storage, Iterable<ItemStack> items, TransactionContext transaction, boolean abortIfFailed) {
        boolean success = true;
        for (ItemStack stack : items) {
            if (stack.getCount() != storage.insert(ItemVariant.of(stack), stack.getCount(), transaction)) {
                if (abortIfFailed) {
                    return false;
                }
                success = false;
            }
        }
        return success;
    }

    public static boolean putItems(Storage<ItemVariant> storage, Iterable<ItemStack> items, boolean abortIfFailed, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            boolean result = putItems(storage, items, transaction, abortIfFailed);
            if ((result || !abortIfFailed) && !simulate) {
                transaction.commit();
            } else {
                transaction.abort();
            }
            return result;
        }
    }

    public static boolean putFluids(Storage<FluidVariant> storage, Iterable<FluidStack> fluids, TransactionContext transaction, boolean abortIfFailed) {
        boolean success = true;
        for (FluidStack stack : fluids) {
            if (stack.getAmount() != storage.insert(stack.asFluidVariant(), stack.getAmount(), transaction)) {
                if (abortIfFailed) {
                    return false;
                }
                success = false;
            }
        }
        return success;
    }

    public static boolean putFluids(Storage<FluidVariant> storage, Iterable<FluidStack> fluids, boolean abortIfFailed, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            boolean result = putFluids(storage, fluids, transaction, abortIfFailed);
            if ((result || !abortIfFailed) && !simulate) {
                transaction.commit();
            } else {
                transaction.abort();
            }
            return result;
        }
    }

    public static boolean takeItems(Storage<ItemVariant> storage, Iterable<RecipeItem> ingredients, boolean simulate) {
        XSTR xstr = new XSTR();
        try (Transaction transaction = Transaction.openOuter()) {
            ingredient:
            for (RecipeItem item : ingredients) {
                boolean doTake = item.getChance() >= 1f || (item.getChance() > 0 && xstr.nextDouble() < item.getChance());
                try (Transaction transaction1 = transaction.openNested()) {
                    int toTake = item.getAmount();
                    for (ItemStack stack : item) {
                        toTake -= storage.extract(ItemVariant.of(stack), toTake, transaction1);
                        if (toTake == 0) {
                            if (doTake)
                                transaction1.commit();
                            else
                                transaction1.abort();
                            continue ingredient;
                        }
                    }
                    transaction1.abort();
                    transaction.abort();
                    return false;
                }
            }
            if (!simulate)
                transaction.commit();
            else
                transaction.abort();
        }
        return true;
    }

    public static boolean takeFluids(Storage<FluidVariant> storage, Iterable<FluidStack> fluids, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (FluidStack fluid : fluids) {
                if (fluid.getAmount() != storage.extract(fluid.asFluidVariant(), fluid.getAmount(), transaction)) {
                    transaction.abort();
                    return false;
                }
            }
            if (!simulate)
                transaction.commit();
        }
        return true;
    }

    public static void pack(Inventory inventory, PacketByteBuf buf) {
        for (int i = 0; i < inventory.size(); i++) {
            buf.writeItemStack(inventory.getStack(i));
        }
    }

    public static void unpack(Inventory inventory, PacketByteBuf buf) {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.setStack(i, buf.readItemStack());
        }
    }

    public static void pack(IFluidHandler inventory, PacketByteBuf buf) {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.getFluid(i).writeData(buf);
        }
    }

    public static void unpack(IFluidHandler inventory, PacketByteBuf buf) {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.setFluid(i, FluidStack.readData(buf));
        }
    }

    public static NbtElement inventoryToNbt(Inventory inventory) {
        NbtList list = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            list.add(inventory.getStack(i).writeNbt(new NbtCompound()));
        }
        return list;
    }

    public static void inventoryFromNbt(NbtList list, BiConsumer<Integer, ItemStack> setter) {
        for (int i = 0; i < list.size(); i++) {
            setter.accept(i, ItemStack.fromNbt(list.getCompound(i)));
        }
    }

    public static NbtElement fluidHandlerToNbt(IFluidHandler inventory) {
        NbtList list = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            list.add(inventory.getFluid(i).toNbt());
        }
        return list;
    }

    public static void fluidHandlerFromNbt(NbtList list, BiConsumer<Integer, FluidStack> setter) {
        for (int i = 0; i < list.size(); i++) {
            setter.accept(i, FluidStack.fromNbt(list.getCompound(i)));
        }
    }
}
