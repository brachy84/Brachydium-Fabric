package brachy84.brachydium.api.handlers;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleItemSlot implements Slot<ItemKey> {

    private final Key.Object<ItemStack> type;
    public int max = 64;
    private final boolean extractable, insertable;

    public SingleItemSlot() {
        this(true, true);
    }

    public SingleItemSlot(boolean extractable, boolean insertable) {
        this.extractable = extractable;
        this.insertable = insertable;
        this.type = new ObjectKeyImpl<>(ItemStack.EMPTY);
    }

    public SingleItemSlot(Key.Object<ItemStack> type) {
        this.type = type;
        this.extractable = true;
        this.insertable = true;
    }

    public ItemStack getStack(@Nullable Transaction transaction) {
        return type.get(transaction);
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if ((!supportsInsertion() && transaction == null) || quantity <= 0) {
            return 0;
        }

        ItemStack stack = getStack(transaction);
        if (stack.isEmpty()) {
            int count = Math.min(quantity, type.getMaxStackSize());
            set(transaction, type.createItemStack(count));
            return count;
        } else if (type.isEqual(stack)) {
            quantity = Math.min(Math.min(this.getMax(type), this.max) - stack.getCount(), quantity);
            ItemStack copy = stack.copy();
            copy.setCount(stack.getCount() + quantity);
            set(transaction, copy);
            return quantity;
        }
        return 0;
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
        if(!supportsExtraction() && transaction == null) return;
        ItemStack stack = getStack(transaction);
        if(stack.isEmpty()) return;
        int oldLevel = stack.getCount();
        int amount = insertable.insert(transaction, ItemKey.of(stack), oldLevel);
        int newLevel = oldLevel - amount;

        ItemStack copy = stack.copy();
        copy.setCount(newLevel);
        set(transaction, copy);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if ((!supportsExtraction() && transaction == null) || quantity <= 0) {
            return 0;
        }
        if (type.equals(this.getKey(transaction))) {
            return this.extract(transaction, quantity);
        }
        return 0;
    }

    @Override
    public int extract(@Nullable Transaction transaction, int quantity) {
        if((!supportsExtraction() && transaction == null) || quantity == 0) {
            return 0;
        }
        int toTake = Math.min(quantity, this.getQuantity(transaction));
        if(this.set(transaction, this.getKey(transaction), this.getQuantity(transaction) - toTake)) {
            return toTake;
        } else {
            return 0;
        }
    }

    @Override
    public ItemKey getKey(@Nullable Transaction transaction) {
        return ItemKey.of(this.getStack(transaction));
    }

    @Override
    public int getQuantity(@Nullable Transaction transaction) {
        return this.getStack(transaction).getCount();
    }

    public boolean set(Transaction transaction, ItemStack stack) {
        return set(transaction, ItemKey.of(stack), stack.getCount());
    }

    @Override
    public boolean set(@Nullable Transaction transaction, ItemKey key, int quantity) {
        if(quantity <= key.getMaxStackSize()) {
            type.set(transaction, key.createItemStack(quantity));
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty(@Nullable Transaction transaction) {
        return getStack(transaction).isEmpty();
    }

    @Override
    public void clear(@Nullable Transaction transaction) {
        set(transaction, ItemKey.EMPTY, 0);
    }

    public int getMax(ItemKey key) {
        return key.getMaxStackSize();
    }

    @Override
    public String toString() {
        ItemStack stack = getStack(null);
        StringBuilder builder = new StringBuilder("[");
        builder.append(stack.getItem().toString());
        NbtCompound tag = stack.getTag();
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
