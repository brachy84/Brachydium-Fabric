package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public class FluidInventoryStorage extends CombinedStorage<FluidVariant, SingleSlotStorage<FluidVariant>> {

    private final IFluidHandler inventory;

    public FluidInventoryStorage(IFluidHandler inventory) {
        super(new ArrayList<>());
        this.inventory = inventory;
        for (int i = 0; i < inventory.size(); i++) {
            parts.add(new FluidTankSlot(inventory, i));
        }
    }

    protected static class FluidTankSlot extends SnapshotParticipant<FluidStack> implements SingleSlotStorage<FluidVariant> {

        private final IFluidHandler inventory;
        private final int index;

        private FluidTankSlot(IFluidHandler inventory, int index) {
            this.inventory = inventory;
            this.index = index;
        }

        public FluidStack getStack() {
            return inventory.getFluid(index);
        }

        public void setStack(FluidStack stack) {
            inventory.setFluid(index, stack);
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

            FluidStack currentStack = getStack();

            if (currentStack.isEmpty() || currentStack.matches(insertedVariant)) {
                int insertedAmount = (int) Math.min(maxAmount, getCapacity() - currentStack.getAmount());

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);

                    if (currentStack.isEmpty()) {
                        currentStack = new FluidStack(insertedVariant, insertedAmount);
                    } else {
                        currentStack.increment(insertedAmount);
                    }

                    setStack(currentStack);
                }

                return insertedAmount;
            }

            return 0;
        }

        @Override
        public long extract(FluidVariant variant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(variant, maxAmount);

            FluidStack currentStack = getStack();

            if (currentStack.matches(variant)) {
                int extracted = (int) Math.min(currentStack.getAmount(), maxAmount);

                if (extracted > 0) {
                    this.updateSnapshots(transaction);
                    currentStack.decrement(extracted);
                    setStack(currentStack);
                }

                return extracted;
            }

            return 0;
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public FluidVariant getResource() {
            return getStack().asFluidVariant();
        }

        @Override
        public long getAmount() {
            return getStack().getAmount();
        }

        @Override
        public long getCapacity() {
            return inventory.getCapacityAt(index);
        }

        @Override
        protected FluidStack createSnapshot() {
            return getStack().copy();
        }

        @Override
        protected void readSnapshot(FluidStack snapshot) {
            setStack(snapshot);
        }

        @Override
        protected void onFinalCommit() {
            inventory.markDirty();
        }
    }
}
