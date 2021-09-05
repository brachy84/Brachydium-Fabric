package brachy84.brachydium.api.handlers.oldAstrarre;

import brachy84.brachydium.Brachydium;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class McInventory implements Inventory {

    public static ArrayParticipant<ItemKey> toParticipant(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < inventory.size(); i++) {
            items.add(inventory.getStack(i));
        }
        return new ItemInventory(true, true, (ItemStack[]) items.toArray());
    }

    private final ArrayParticipant<ItemKey> inv;

    public McInventory(ArrayParticipant<ItemKey> inv) {
        this.inv = inv;
    }

    @Override
    public int size() {
        return inv.getSlots().size();
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty(null);
    }

    @Override
    public ItemStack getStack(int slot) {
        Slot<ItemKey> slot1 = getSlot(slot);
        return slot1.getKey(null).createItemStack(slot1.getQuantity(null));
    }

    public Slot<ItemKey> getSlot(int slot) {
        return inv.getSlots().get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = getStack(slot).copy();
        Brachydium.LOGGER.info("Removing {} * {} at {}", stack, amount, slot);
        int extracted = getSlot(slot).extract(null, amount);
        Brachydium.LOGGER.info("Extracted {}", extracted);
        stack.setCount(extracted);
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = getStack(slot).copy();
        int extracted = getSlot(slot).extract(null, stack.getMaxCount());
        stack.setCount(extracted);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        Slot<ItemKey> slot1 = getSlot(slot);
        slot1.set(null, ItemKey.of(stack), stack.getCount());
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inv.clear(null);
    }

    public ArrayParticipant<ItemKey> asParticipant() {
        return inv;
    }
}
