package brachy84.brachydium.api.handlers.oldAstrarre;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class ItemInventory implements ArrayParticipant<ItemKey>, InventoryListener {

    private final DiffKey.Array<ItemStack> items;

    private final boolean extractable, insertable;

    public ItemInventory(int slots, boolean extractable, boolean insertable) {
        this.items = new DiffKey.Array<>(DefaultedList.ofSize(slots, ItemStack.EMPTY));
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public ItemInventory(boolean extractable, boolean insertable, ItemStack... stacks) {
        this.items = new DiffKey.Array<>(Arrays.asList(stacks));
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public static ItemInventory fromTag(NbtCompound tag) {
        int size = tag.getInt("slots");
        NbtList list = (NbtList) tag.get("content");
        assert list != null;
        ItemStack[] stacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            NbtCompound tag1 = (NbtCompound) list.get(i);
            stacks[i] = ItemStack.fromNbt(tag1);
        }
        return new ItemInventory(tag.getBoolean("ext"), tag.getBoolean("ins"), stacks);
    }

    public static ItemInventory importInventory(int tanks) {
        return new ItemInventory(tanks, false, true);
    }

    public static ItemInventory exportInventory(int tanks) {
        return new ItemInventory(tanks, true, false);
    }

    @Override
    public void addListener(Runnable runnable) {
        items.onApply(runnable);
    }

    @Override
    public List<Slot<ItemKey>> getSlots() {
        return new AbstractList<Slot<ItemKey>>() {
            @Override
            public Slot<ItemKey> get(int index) {
                return new ItemSlot(items, index, extractable, insertable);
            }

            @Override
            public int size() {
                return items.get(null).size();
            }
        };
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }

    @Override
    public boolean supportsInsertion() {
        return insertable;
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
        ArrayParticipant.super.extract(transaction, insertable);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        int count = 0;
        for (Slot<ItemKey> slot : this.getSlots()) {
            int extracted = slot.extract(transaction, type, quantity);
            count += extracted;
            quantity -= extracted;
            if(quantity == 0) {
                break;
            }
        }
        return count;
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        return ArrayParticipant.super.insert(transaction, type, quantity);
    }

    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("slots", getSlots().size());
        tag.putBoolean("ins", insertable);
        tag.putBoolean("ext", extractable);
        NbtList list = new NbtList();
        for (Slot<ItemKey> slot : getSlots()) {
            ItemStack stack = slot.getKey(null).createItemStack(slot.getQuantity(null));
            list.add(stack.writeNbt(new NbtCompound()));
        }
        tag.put("content", list);
        return tag;
    }
}
