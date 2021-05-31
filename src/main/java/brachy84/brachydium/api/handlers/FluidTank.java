package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.fluid.FluidStack;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A fluid tank in a tank array don't use it for single fluid tanks
 * see also: {@link SingleFluidTank}
 */
public class FluidTank implements Slot<Fluid> {

    private final DiffKey.Array<FluidStack> inventory;
    private final int index;
    private final boolean extractable, insertable;
    private final int capacity;

    public FluidTank(DiffKey.Array<FluidStack> inventory, int index) {
        this(inventory, index, 64 * 81000);
    }

    public FluidTank(DiffKey.Array<FluidStack> inventory, int index, boolean extractable, boolean insertable) {
        this(inventory, index, 64 * 81000, extractable, insertable);
    }

    public FluidTank(DiffKey.Array<FluidStack> inventory, int index, int capacity) {
        this(inventory, index, capacity, true, true);
    }

    public FluidTank(DiffKey.Array<FluidStack> inventory, int index, int capacity, boolean extractable, boolean insertable) {
        this.inventory = inventory;
        this.index = index;
        this.extractable = extractable;
        this.insertable = insertable;
        this.capacity = capacity;
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
        return inventory.get(transaction).get(index);
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
        if(quantity > capacity || quantity < 0) return false;
        List<FluidStack> stacks = inventory.get(transaction);
        stacks.set(index, new FluidStack(key, quantity));
        inventory.set(transaction, stacks);
        return true;
    }

    @Override
    public boolean isFull(@Nullable Transaction transaction) {
        return getQuantity(transaction) >= capacity;
    }

    @Override
    public int extract(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
        if (type.equals(this.getKey(transaction))) {
            return this.extract(transaction, quantity);
        }
        return 0;
    }

    @Override
    public int extract(@Nullable Transaction transaction, int quantity) {
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
        if(insertable.isFull(transaction)) return;
        try(Transaction transaction1 = Transaction.create()) {
            int inserted = insertable.insert(transaction1, this.getKey(transaction), this.getQuantity(transaction));
            if(this.extract(transaction, inserted) != inserted) {
                transaction1.abort();
            }
        }
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid key, int quantity) {
        if(quantity == 0) return 0;
        int result = Droplet.minSum(this.getQuantity(transaction), quantity);
        int oldQuantity = this.getQuantity(transaction);
        if(this.set(transaction, key, result)) {
            return result - oldQuantity;
        }
        return 0;
    }

    @Override
    public boolean isEmpty(@Nullable Transaction transaction) {
        return getStack(transaction).isEmpty();
    }
}
