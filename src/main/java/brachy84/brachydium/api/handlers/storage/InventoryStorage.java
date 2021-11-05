package brachy84.brachydium.api.handlers.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class InventoryStorage extends CombinedStorage<ItemVariant, InventoryStorage.InventorySlot> {

    private final Inventory inventory;

    public InventoryStorage(Inventory inventory) {
        super(new ArrayList());
        this.inventory = inventory;
        for (int i = 0; i < inventory.size(); i++) {
            parts.add(new InventorySlot(inventory, i));
        }
    }

    protected static class InventorySlot extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {

        private final Inventory inventory;
        private final int index;

        private InventorySlot(Inventory inventory, int index) {
            this.inventory = inventory;
            this.index = index;
        }

        public ItemStack getStack() {
            return inventory.getStack(index);
        }

        public void setStack(ItemStack stack) {
            inventory.setStack(index, stack);
        }

        @Override
        public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

            ItemStack currentStack = getStack();

            if (insertedVariant.matches(currentStack) || currentStack.isEmpty()) {
                int insertedAmount = (int) Math.min(maxAmount, currentStack.getMaxCount() - currentStack.getCount());

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);

                    if (currentStack.isEmpty()) {
                        currentStack = insertedVariant.toStack(insertedAmount);
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
        public long extract(ItemVariant variant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(variant, maxAmount);

            ItemStack currentStack = getStack();

            if (variant.matches(currentStack)) {
                int extracted = (int) Math.min(currentStack.getCount(), maxAmount);

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
        public ItemVariant getResource() {
            return ItemVariant.of(getStack());
        }

        @Override
        public long getAmount() {
            return getStack().getCount();
        }

        @Override
        public long getCapacity() {
            return 64;
        }

        @Override
        protected ItemStack createSnapshot() {
            return getStack().copy();
        }

        @Override
        protected void readSnapshot(ItemStack snapshot) {
            setStack(snapshot);
        }

        @Override
        protected void onFinalCommit() {
            inventory.markDirty();
        }
    }

}
