package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.fluid.FluidStack;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This for Single fluid tanks, don't use it for machines with multiple fluid slots
 * see also: {@link FluidTank}
 */
public class SingleFluidTank implements Slot<Fluid>, InventoryListener {

    private final Key.Object<FluidStack> type;
    private final boolean extractable, insertable;
    private final int capacity;


    public SingleFluidTank(boolean extractable, boolean insertable) {
        this(64 * 81000, extractable, insertable);
    }

    public SingleFluidTank(int capacity) {
        this(capacity, true, true);
    }

    public SingleFluidTank(int capacity, boolean extractable, boolean insertable) {
        this.extractable = extractable;
        this.insertable = insertable;
        this.capacity = capacity;
        this.type = new ObjectKeyImpl<>(FluidStack.EMPTY);
    }

    public static SingleFluidTank fromTag(CompoundTag tag) {
        FluidStack stack = FluidStack.fromTag(tag.getCompound("content"));
        SingleFluidTank tank = new SingleFluidTank(tag.getInt("capacity"), tag.getBoolean("ext"), tag.getBoolean("ins"));
        tank.set(null, stack.getFluid(), stack.getAmount());
        return tank;
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
        if(quantity > capacity || quantity < 0) return false;
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
            int capacity = insertable.insert(transaction1, this.getKey(transaction), this.getQuantity(transaction));
            if(this.extract(transaction, capacity) != capacity) {
                transaction1.abort();
            }
        }
    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Fluid key, int quantity) {
        if(quantity == 0) return 0;
        int oldQuantity = this.getQuantity(transaction);
        int result = Math.min(capacity, Droplet.minSum(oldQuantity, quantity));
        if(this.set(transaction, key, result)) {
            return result - oldQuantity;
        }
        return 0;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ins", insertable);
        tag.putBoolean("ext", extractable);
        tag.putInt("capacity", capacity);
        tag.put("content", getStack(null).toTag(new CompoundTag()));
        return tag;
    }

    @Override
    public void addListener(Runnable runnable) {
        type.onApply(runnable);
    }
}
