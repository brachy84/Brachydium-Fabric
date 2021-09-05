package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Clearable;

import java.util.Set;

public interface IFluidHandler extends Clearable {

    default Storage<FluidVariant> asStorage() {
        return FluidInventoryStorage.of(this);
    }

    int getSlots();

    FluidStack getStackAt(int slot);

    long getCapacityAt(int slot);

    boolean isEmpty();

    /**
     * Removes a specific number of items from the given slot.
     *
     * @return the removed items as a stack
     */
    FluidStack removeStack(int slot, int amount);

    /**
     * Removes the stack currently stored at the indicated slot.
     *
     * @return the stack previously stored at the indicated slot.
     */
    FluidStack removeStack(int slot);

    void setStack(int slot, FluidStack stack);

    void markDirty();

    /**
     * Returns whether the given stack is a valid for the indicated slot position.
     */
    default boolean isValid(FluidStack stack) {
        return true;
    }

    /**
     * Returns the number of times the specified item occurs in this inventory across all stored stacks.
     * Ignores amount
     */
    default long count(FluidStack stack) {
        long i = 0;
        for (int j = 0; j < this.getSlots(); ++j) {
            FluidStack fluidStack = this.getStackAt(j);
            if (FluidStack.matchesStack(fluidStack, stack)) {
                i += fluidStack.getAmount();
            }
        }

        return i;
    }

    /**
     * Determines whether this inventory contains any of the given candidate items.
     * Ignores amount
     */
    default boolean containsAny(Set<FluidStack> fluids) {
        for (int i = 0; i < this.getSlots(); ++i) {
            FluidStack fluidStack = this.getStackAt(i);
            if (!fluidStack.isEmpty() && fluids.contains(fluidStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    default void clear() {
        for (int i = 0; i < getSlots(); i++) {
            setStack(i, FluidStack.EMPTY);
        }
    }

    public static IFluidHandler EMPTY = new IFluidHandler() {
        @Override
        public int getSlots() {
            return 0;
        }

        @Override
        public FluidStack getStackAt(int slot) {
            return FluidStack.EMPTY;
        }

        @Override
        public long getCapacityAt(int slot) {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public FluidStack removeStack(int slot, int amount) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack removeStack(int slot) {
            return FluidStack.EMPTY;
        }

        @Override
        public void setStack(int slot, FluidStack stack) {
        }

        @Override
        public void markDirty() {
        }
    };
}
