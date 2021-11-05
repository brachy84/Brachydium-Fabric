package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class FluidHandler implements IFluidHandler {

    private final List<FluidStack> fluids;
    private final long capacity;

    public FluidHandler(long capacity, FluidStack... fluids) {
        this.fluids = DefaultedList.copyOf(FluidStack.EMPTY, fluids);
        this.capacity = capacity;
    }

    public FluidHandler(int size, long capacity) {
        this.fluids = DefaultedList.ofSize(size, FluidStack.EMPTY);
        this.capacity = capacity;
    }

    @Override
    public int getTanks() {
        return fluids.size();
    }

    @Override
    public FluidStack getStackAt(int slot) {
        return fluids.get(slot);
    }

    @Override
    public long getCapacityAt(int slot) {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getTanks(); i++) {
            if(!getStackAt(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public FluidStack removeStack(int slot, int amount) {
        FluidStack stack = getStackAt(slot);
        amount = Math.min(amount, stack.getAmount());
        stack.decrement(amount);
        return stack.copyWith(amount);
    }

    @Override
    public FluidStack removeStack(int slot) {
        FluidStack stack = getStackAt(slot).copy();
        setStack(slot, FluidStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, FluidStack stack) {
        fluids.set(slot, stack);
    }

    @Override
    public void markDirty() {

    }
}
