package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

import java.util.Objects;

public class SingleFluidTank extends SingleVariantStorage<FluidVariant> {

    private final boolean extractable, insertable;
    private final long capacity;

    public SingleFluidTank(long capacity, boolean extractable, boolean insertable) {
        this(FluidStack.EMPTY, capacity, extractable, insertable);
    }

    public SingleFluidTank(FluidStack stack, long capacity) {
        this(stack, capacity, true, true);
    }

    public SingleFluidTank(FluidStack stack, long capacity, boolean extractable, boolean insertable) {
        this.extractable = extractable;
        this.insertable = insertable;
        this.capacity = capacity;
        Objects.requireNonNull(stack);
        this.variant = stack.asFluidVariant();
        this.amount = stack.getAmount();
    }

    public FluidStack getStack() {
        return new FluidStack(variant, amount);
    }

    public void setStack(FluidStack stack) {
        this.variant = stack.asFluidVariant();
        this.amount = stack.getAmount();
    }

    @Override
    public boolean supportsExtraction() {
        return extractable;
    }


    @Override
    public boolean supportsInsertion() {
        return insertable;
    }


    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }
}
