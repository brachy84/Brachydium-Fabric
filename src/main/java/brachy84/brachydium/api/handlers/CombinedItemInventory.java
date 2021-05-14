package brachy84.brachydium.api.handlers;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item inventory that usually consists of an import and an export tank list
 */
public class CombinedItemInventory implements Participant<ItemKey> {

    private final ArrayParticipant<ItemKey>[] fluidTankLists;

    @SafeVarargs
    public CombinedItemInventory(ArrayParticipant<ItemKey>... fluidTankLists) {
        this.fluidTankLists = fluidTankLists;
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
        for(ArrayParticipant<ItemKey> tankList : fluidTankLists) {
            tankList.extract(transaction, insertable);
            if(insertable.isFull(transaction)) {
                return;
            }
        }
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
        int count = 0;
        for(ArrayParticipant<ItemKey> tankList : fluidTankLists) {
            int extracted = tankList.extract(transaction, type, quantity);
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
        int count = 0;
        for(ArrayParticipant<ItemKey> tankList : fluidTankLists) {
            int inserted = tankList.insert(transaction, type, quantity);
            count += inserted;
            quantity -= inserted;
            if(quantity == 0) {
                break;
            }
        }
        return count;
    }
}
