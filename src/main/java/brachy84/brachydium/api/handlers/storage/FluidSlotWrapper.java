package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class FluidSlotWrapper extends SingleFluidTank {

    private final FluidInventoryStorage storage;
    private final int slot;

    public FluidSlotWrapper(FluidInventoryStorage storage, int slot) {
        super(storage.inventory.getStackAt(slot), storage.inventory.getCapacityAt(slot));
        this.storage = storage;
        this.slot = slot;
    }

    @Override
    public FluidStack getStack() {
        return storage.inventory.getStackAt(slot);
    }

    @Override
    public void setStack(FluidStack stack) {
        storage.inventory.setStack(slot, stack);
    }

    @Override
    protected boolean canInsert(FluidVariant fluidVariant) {
        return storage.inventory.isValid(new FluidStack(fluidVariant, 1));
    }

    @Override
    public long getCapacity(FluidVariant variant) {
        return storage.inventory.getCapacityAt(slot);
    }

    // We override updateSnapshots to also schedule a markDirty call for the backing inventory.
    @Override
    public void updateSnapshots(TransactionContext transaction) {
        storage.markDirtyParticipant.updateSnapshots(transaction);
        super.updateSnapshots(transaction);
    }
}
