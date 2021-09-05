package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;

import java.util.Objects;

public class FluidTank implements IFluidHandler {

    private FluidStack stack;
    private final long capacity;

    public FluidTank(long capacity, FluidStack stack) {
        this.capacity = Math.max(0, capacity);
        this.stack = Objects.requireNonNull(stack);
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public FluidStack getStackAt(int slot) {
        return stack;
    }

    @Override
    public long getCapacityAt(int slot) {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public FluidStack removeStack(int slot, int amount) {
        FluidStack stack = this.stack;
        amount = Math.min(amount, stack.getAmount());
        stack.decrement(amount);
        return stack.copyWith(amount);
    }

    @Override
    public FluidStack removeStack(int slot) {
        FluidStack stack = this.stack.copy();
        this.stack = FluidStack.EMPTY;
        return stack;
    }

    @Override
    public void setStack(int slot, FluidStack stack) {
        this.stack = Objects.requireNonNull(stack);
        this.stack.setAmount((int) Math.min(this.stack.getAmount(), capacity));
    }

    @Override
    public void markDirty() {
    }
}
