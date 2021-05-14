package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.fluid.FluidStack;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTank implements Slot<Fluid> {

    private final boolean extractable, insertable;
    private final int capacity;

    public final Key.Object<FluidStack> type;

    public FluidTank() {
        this(FluidStack.EMPTY);
    }

    public FluidTank(FluidStack fluidStack) {
        this(fluidStack, 64 * 81000, true, true);
    }

    public FluidTank(int capacity, boolean extractable, boolean insertable) {
        this(FluidStack.EMPTY, capacity, extractable, insertable);
    }

    public FluidTank(FluidStack fluidStack, int capacity, boolean extractable, boolean insertable) {
        this.type = new ObjectKeyImpl<>(fluidStack);
        this.capacity = capacity;
        this.extractable = extractable;
        this.insertable = insertable;
    }

    @Override
    public boolean supportsInsertion() {
        return insertable;
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }

    public FluidStack getStack(@Nullable Transaction transaction) {
        return type.get(transaction);
    }

    @Override
    public Fluid getKey(@Nullable Transaction transaction) {
        return getStack(transaction).getFluid();
    }

    @Override
    public int getQuantity(@Nullable Transaction transaction) {
        return getStack(transaction).getAmount();
    }

    @Override
    public boolean set(@Nullable Transaction transaction, Fluid key, int quantity) {
        if(quantity > capacity || quantity <= 0) return false;
        type.set(transaction, new FluidStack(key, quantity));
        return true;
    }

    @Override
    public boolean isFull(@Nullable Transaction transaction) {
        return getQuantity(transaction) >= capacity;
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        if(!supportsExtraction()) return 0;
        if (type.equals(this.getKey(transaction))) {
            return this.extract(transaction, quantity);
        }
        return 0;
    }

    @Override
    public int extract(@Nullable Transaction transaction, int quantity) {
        if(!supportsExtraction()) return 0;
        if(quantity == 0) return 0;
        int toTake = Math.min(quantity, this.getQuantity(transaction));
        if(this.set(transaction, this.getKey(transaction), this.getQuantity(transaction) - toTake)) {
            return toTake;
        } else {
            return 0;
        }
    }

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
        if(!supportsExtraction()) return;
        if(insertable.isFull(transaction)) return;
        try(Transaction transaction1 = Transaction.create()) {
            int capacity = insertable.insert(transaction1, this.getKey(transaction), this.getQuantity(transaction));
            if(this.extract(transaction, capacity) != capacity) {
                transaction1.abort();
            }
        }
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid key, int quantity) {
        if(!supportsInsertion()) return 0;
        if(quantity == 0) return 0;
        int result = Droplet.minSum(this.getQuantity(transaction), quantity);
        int oldQuantity = this.getQuantity(transaction);
        if(this.set(transaction, key, result)) {
            return result - oldQuantity;
        }
        return 0;
    }
}
