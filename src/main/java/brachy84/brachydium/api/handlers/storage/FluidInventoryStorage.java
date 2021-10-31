package brachy84.brachydium.api.handlers.storage;

import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public class FluidInventoryStorage extends CombinedStorage<FluidVariant, SingleSlotStorage<FluidVariant>> {

    private static final Map<IFluidHandler, FluidInventoryStorage> WRAPPERS = new MapMaker().weakValues().makeMap();

    public static FluidInventoryStorage of(IFluidHandler fluidHandler) {
        FluidInventoryStorage storage = WRAPPERS.computeIfAbsent(fluidHandler, inv -> {
            return new FluidInventoryStorage(inv);
        });
        storage.resizeSlotList();
        return storage;
    }

    final IFluidHandler inventory;
    /**
     * This {@code backingList} is the real list of wrappers.
     * The {@code parts} in the superclass is the public-facing unmodifiable sublist with exactly the right amount of slots.
     */
    final List<FluidSlotWrapper> backingList;
    /**
     * This participant ensures that markDirty is only called once for the entire inventory.
     */
    final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();

    FluidInventoryStorage(IFluidHandler inventory) {
        super(Collections.emptyList());
        this.inventory = inventory;
        this.backingList = new ArrayList<>();
    }

    public List<SingleSlotStorage<FluidVariant>> getSlots() {
        return parts;
    }

    /**
     * Resize slot list to match the current size of the inventory.
     */
    private void resizeSlotList() {
        int inventorySize = inventory.getSlots();

        // If the public-facing list must change...
        if (inventorySize != parts.size()) {
            // Ensure we have enough wrappers in the backing list.
            while (backingList.size() < inventorySize) {
                backingList.add(new FluidSlotWrapper(this, backingList.size()));
            }

            // Update the public-facing list.
            parts = Collections.unmodifiableList(backingList.subList(0, inventorySize));
        }
    }

    // Boolean is used to prevent allocation. Null values are not allowed by SnapshotParticipant.
    class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {
        @Override
        protected Boolean createSnapshot() {
            return Boolean.TRUE;
        }

        @Override
        protected void readSnapshot(Boolean snapshot) {
        }

        @Override
        protected void onFinalCommit() {
            inventory.markDirty();
        }
    }
}
