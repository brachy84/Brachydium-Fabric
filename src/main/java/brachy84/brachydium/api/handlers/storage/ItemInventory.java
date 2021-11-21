package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.blockEntity.TileEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class ItemInventory implements IItemHandler {

    private final DefaultedList<ItemStack> items;
    private final boolean extractable, insertable;
    private final List<Runnable> listeners = new ArrayList<>();
    private TileEntity notifiable;

    public ItemInventory(int size) {
        this(size, true, true);
    }

    public ItemInventory(int size, boolean extractable, boolean insertable) {
        this.items = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public ItemInventory(boolean extractable, boolean insertable, ItemStack... stacks) {
        this.items = DefaultedList.copyOf(ItemStack.EMPTY, stacks);
        this.extractable = extractable;
        this.insertable = insertable;
    }

    public static ItemInventory importInventory(int size) {
        return new ItemInventory(size, false, true);
    }

    public static ItemInventory exportInventory(int size) {
        return new ItemInventory(size, true, false);
    }

    public Storage<ItemVariant> asStorage() {
        return InventoryStorage.of(this, null);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = getStack(slot);
        amount = Math.min(stack.getCount(), amount);
        stack.decrement(amount);
        if (stack.getCount() == 0)
            return ItemStack.EMPTY;
        ItemStack removed = stack.copy();
        removed.setCount(amount);
        if (amount > 0)
            markDirty();
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = getStack(slot);
        setStack(slot, ItemStack.EMPTY);
        if (!stack.isEmpty())
            markDirty();
        return stack;
    }

    @Override
    public void setStackSilently(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public void markDirty() {
        addToNotifiedList(notifiable, this, extractable && !insertable);
        listeners.forEach(Runnable::run);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void addListener(Runnable runnable) {
        listeners.add(runnable);
    }

    @Override
    public void setNotifiableMetaTileEntity(TileEntity metaTileEntity) {
        this.notifiable = metaTileEntity;
    }
}
