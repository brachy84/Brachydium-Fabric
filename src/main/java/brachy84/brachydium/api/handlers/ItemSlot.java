package brachy84.brachydium.api.handlers;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSlot implements Slot<ItemKey> {

    public final Key.Object<ItemStack> type;
    public int max = 64;
    private final boolean extractable, insertable;

    public ItemSlot() {
        this(true, true);
    }

    public ItemSlot(boolean extractable, boolean insertable) {
        this(ItemStack.EMPTY, extractable, insertable);
    }

    public ItemSlot(ItemStack stack, boolean extractable, boolean insertable) {
        this.type = new ObjectKeyImpl<>(stack);
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public ItemStack getStack(@Nullable Transaction transaction) {
        return this.type.get(transaction);
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if (!supportsInsertion() || quantity == 0) {
            return 0;
        }

        ItemStack stack = this.type.get(transaction);
        if (stack.isEmpty()) {
            int count = Math.min(quantity, type.getMaxStackSize());
            this.type.set(transaction, type.createItemStack(count));
            return count;
        } else if (type.isEqual(stack)) {
            quantity = Math.min(Math.min(this.getMax(type), this.max) - stack.getCount(), quantity);
            ItemStack copy = stack.copy();
            copy.setCount(stack.getCount() + quantity);
            this.type.set(transaction, copy);
            return quantity;
        }
        return 0;
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
        if(!supportsExtraction()) return;
        ItemStack stack = this.type.get(transaction);
        if(stack.isEmpty()) return;
        int oldLevel = stack.getCount();
        int amount = insertable.insert(transaction, ItemKey.of(stack), oldLevel);
        int newLevel = oldLevel - amount;

        ItemStack copy = stack.copy();
        copy.setCount(newLevel);
        this.type.set(transaction, copy);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if (!supportsExtraction() || quantity == 0) {
            return 0;
        }

        ItemStack stack = this.type.get(transaction);
        if (type.isEqual(stack)) {
            int oldLevel = stack.getCount();
            int toExtract = Math.min(oldLevel, quantity);
            int newLevel = oldLevel - toExtract;
            ItemStack copy = stack.copy();
            copy.setCount(newLevel);
            this.type.set(transaction, copy);
            return toExtract;
        }

        return 0;
    }

    @Override
    public ItemKey getKey(@Nullable Transaction transaction) {
        return ItemKey.of(this.getStack(transaction));
    }

    @Override
    public int getQuantity(@Nullable Transaction transaction) {
        return this.getStack(transaction).getCount();
    }

    @Override
    public boolean set(@Nullable Transaction transaction, ItemKey key, int quantity) {
        if(quantity <= key.getMaxStackSize()) {
            this.type.set(transaction, key.createItemStack(quantity));
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty(@Nullable Transaction transaction) {
        return this.type.get(transaction).isEmpty();
    }

    @Override
    public void clear(@Nullable Transaction transaction) {
        this.type.set(transaction, ItemStack.EMPTY);
    }

    public int getMax(ItemKey key) {
        return key.getMaxStackSize();
    }

    @Override
    public String toString() {
        ItemStack stack = this.type.get(null);
        StringBuilder builder = new StringBuilder("[");
        builder.append(stack.getItem().toString());
        CompoundTag tag = stack.getTag();
        if(tag != null && !tag.isEmpty()) {
            builder.append(' ').append(tag);
        }
        builder.append('x').append(stack.getCount()).append(']');
        return builder.toString();
    }

    @Override
    public boolean supportsInsertion() {
        return insertable;
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }
}
