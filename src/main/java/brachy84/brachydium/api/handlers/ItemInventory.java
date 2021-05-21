package brachy84.brachydium.api.handlers;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemInventory implements ArrayParticipant<ItemKey> {

    private final List<Slot<ItemKey>> items;
    private final boolean extractable, insertable;

    public ItemInventory(int slots, boolean extractable, boolean insertable) {
        this.items = DefaultedList.ofSize(slots, new ItemSlot(extractable, insertable));
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public ItemInventory(boolean extractable, boolean insertable, ItemStack... stacks) {
        this.items = Arrays.stream(stacks).map(stack -> new ItemSlot(stack, extractable, insertable)).collect(Collectors.toList());
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public static ItemInventory fromTag(CompoundTag tag) {
        int size = tag.getInt("slots");
        ListTag list = (ListTag) tag.get("content");
        assert list != null;
        ItemStack[] stacks = new ItemStack[size];
        for(int i = 0; i < size; i++) {
            CompoundTag tag1 = (CompoundTag) list.get(i);
            stacks[i] = ItemStack.fromTag(tag1);
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
    public List<Slot<ItemKey>> getSlots() {
        return Collections.unmodifiableList(items);
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
        if(!supportsExtraction()) return;
        ArrayParticipant.super.extract(transaction, insertable);
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if(!supportsExtraction()) return 0;
        return ArrayParticipant.super.extract(transaction, type, quantity);
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        if(!supportsInsertion()) return 0;
        return ArrayParticipant.super.insert(transaction, type, quantity);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("slots", getSlots().size());
        tag.putBoolean("ins", insertable);
        tag.putBoolean("ext", extractable);
        ListTag list = new ListTag();
        for(Slot<ItemKey> slot : getSlots()) {
            ItemStack stack = slot.getKey(null).createItemStack(slot.getQuantity(null));
            list.add(stack.toTag(new CompoundTag()));
        }
        tag.put("content", list);
        return tag;
    }
}
