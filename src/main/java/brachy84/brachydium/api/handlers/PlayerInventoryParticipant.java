package brachy84.brachydium.api.handlers;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.player.PlayerParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import io.github.astrarre.transfer.v0.fabric.inventory.InventoryList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;

public class PlayerInventoryParticipant implements ArrayParticipant<ItemKey>, PlayerParticipant {

    public final DiffKey.Array<ItemStack> array;
    public final PlayerInventory inventory;

    public PlayerInventoryParticipant(PlayerInventory inventory) {
        this.inventory = inventory;
        this.array = new DiffKey.Array<>(new InventoryList(inventory));
    }

    @Override
    public ReplacingParticipant<ItemKey> getHandReplacingParticipant(Hand hand) {
        int slot;
        if(hand == Hand.MAIN_HAND) {
            slot = this.inventory.selectedSlot;
        } else {
            PlayerInventory inventory = this.inventory;
            slot = inventory.main.size() + inventory.armor.size();
        }
        return this.getSlotReplacingParticipant(slot);
    }

    @Override
    public ReplacingParticipant<ItemKey> getCursorItemReplacingParticipant() {
        return ReplacingParticipant.of(new ItemSlotParticipant(new io.github.astrarre.transfer.internal.compat.PlayerInventoryParticipant.CursorKey(this.inventory)) {}, this);
    }

    @Override
    public void insertOrDrop(@Nullable Transaction transaction, ItemKey key, int amount) {

    }

    @Override
    public List<Slot<ItemKey>> getSlots() {
        return new AbstractList<Slot<ItemKey>>() {
            @Override
            public Slot<ItemKey> get(int index) {
                return new ItemSlot(array, index);
            }

            @Override
            public int size() {
                return array.get(null).size();
            }
        };
    }
}
