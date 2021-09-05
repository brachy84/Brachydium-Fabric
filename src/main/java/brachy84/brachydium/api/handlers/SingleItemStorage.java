package brachy84.brachydium.api.handlers;

import brachy84.brachydium.gui.internal.wrapper.IModifiableStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class SingleItemStorage extends SnapshotParticipant<ItemStack> implements IModifiableStorage<ItemVariant> {

    private final boolean extractable, insertable;
    private ItemStack itemStack;

    public SingleItemStorage(boolean extractable, boolean insertable) {
        this(ItemStack.EMPTY, extractable, insertable);
    }

    public SingleItemStorage(ItemStack stack) {
        this(stack, true, true);
    }

    public SingleItemStorage(ItemStack stack, boolean extractable, boolean insertable) {
        this.extractable = extractable;
        this.insertable = insertable;
        Objects.requireNonNull(stack);
        this.itemStack = Objects.requireNonNull(stack);
    }

    public ItemStack getStack() {
        return itemStack;
    }

    public void setStack(ItemStack stack) {
        this.itemStack = stack;
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }

    @Override
    public boolean supportsInsertion() {
        return insertable;
    }

    protected int getCapacity(ItemVariant itemVariant) {
        return itemVariant.getItem().getMaxCount();
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
        return getCapacity(getResource());
    }

    @Override
    public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

        ItemStack currentStack = getStack();

        if ((insertedVariant.matches(currentStack) || currentStack.isEmpty()) && canInsert(insertedVariant)) {
            int insertedAmount = (int) Math.min(maxAmount, getCapacity(insertedVariant) - currentStack.getCount());

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
    public ItemStack createSnapshot() {
        return getStack().copy();
    }

    @Override
    public void readSnapshot(ItemStack snapshot) {
        setStack(snapshot);
    }

    @Override
    public void onFinalCommit() {
    }

    @Override
    public boolean setResource(ItemVariant resource, long amount) {
        setStack(resource.toStack((int) amount));
        return true;
    }
}
