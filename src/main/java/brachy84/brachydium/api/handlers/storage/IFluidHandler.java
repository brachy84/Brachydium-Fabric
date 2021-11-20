package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.INotifiableHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Clearable;

import java.util.Set;

public interface IFluidHandler extends Clearable, InventoryListener, INotifiableHandler {

    default Storage<FluidVariant> asStorage() {
        return new FluidInventoryStorage(this);
    }

    int size();

    FluidStack getFluid(int slot);

    long getCapacityAt(int slot);

    boolean isEmpty();

    /**
     * Removes a specific number of items from the given slot.
     *
     * @return the removed items as a stack
     */
    FluidStack removeFluid(int slot, long amount);

    /**
     * Removes the stack currently stored at the indicated slot.
     *
     * @return the stack previously stored at the indicated slot.
     */
    FluidStack removeFluid(int slot);

    void setFluid(int slot, FluidStack stack);

    default void markDirty() {
    }

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
        for (int j = 0; j < this.size(); ++j) {
            FluidStack fluidStack = this.getFluid(j);
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
        for (int i = 0; i < this.size(); ++i) {
            FluidStack fluidStack = this.getFluid(i);
            if (!fluidStack.isEmpty() && fluids.contains(fluidStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    default void clear() {
        for (int i = 0; i < size(); i++) {
            setFluid(i, FluidStack.EMPTY);
        }
    }

    public static IFluidHandler EMPTY = new IFluidHandler() {
        @Override
        public void addListener(Runnable runnable) {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public FluidStack getFluid(int slot) {
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
        public FluidStack removeFluid(int slot, long amount) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack removeFluid(int slot) {
            return FluidStack.EMPTY;
        }

        @Override
        public void setFluid(int slot, FluidStack stack) {
        }

        @Override
        public void markDirty() {
        }
    };
}
