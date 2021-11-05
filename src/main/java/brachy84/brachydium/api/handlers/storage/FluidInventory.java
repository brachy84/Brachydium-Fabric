package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.INotifiableHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class FluidInventory implements IFluidHandler, InventoryListener, INotifiableHandler {

    private final long capacity;
    private final DefaultedList<FluidStack> fluids;
    private final List<Runnable> listeners = new ArrayList<>();
    private final boolean extractable, insertable;
    private TileEntity notifiable;

    public FluidInventory(int size, long capacityPerTank) {
        this(size, capacityPerTank, true, true);
    }

    public FluidInventory(int size, long capacityPerTank, boolean extractable, boolean insertable) {
        this.fluids = DefaultedList.ofSize(size, FluidStack.EMPTY);
        this.extractable = extractable;
        this.insertable = insertable;
        this.capacity = capacityPerTank;
    }

    public FluidInventory(long capacityPerTank, boolean extractable, boolean insertable, FluidStack... stacks) {
        this.fluids = DefaultedList.copyOf(FluidStack.EMPTY, stacks);
        this.extractable = extractable;
        this.insertable = insertable;
        this.capacity = capacityPerTank;
    }

    public static FluidInventory importInventory(int size, long capacity) {
        return new FluidInventory(size, capacity, false, true);
    }

    public static FluidInventory exportInventory(int size, long capacity) {
        return new FluidInventory(size, capacity, true, false);
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
        for(FluidStack stack : fluids)  {
            if(!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public FluidStack removeStack(int slot, int amount) {
        FluidStack stack = getStackAt(slot);
        amount = Math.min(amount, stack.getAmount());
        stack.decrement(amount);
        if(amount > 0)
            onChange();
        return stack.copyWith(amount);
    }

    @Override
    public FluidStack removeStack(int slot) {
        FluidStack stack = getStackAt(slot);
        setStack(slot, FluidStack.EMPTY);
        if(!stack.isEmpty())
            onChange();
        return stack;
    }

    @Override
    public void setStack(int slot, FluidStack stack) {
        fluids.set(slot, stack);
        onChange();
    }

    @Override
    public void markDirty() {
        addToNotifiedList(notifiable, this, extractable && !insertable);
    }

    @Override
    public void addListener(Runnable runnable) {
        listeners.add(runnable);
    }

    @Override
    public void onChange() {
        listeners.forEach(Runnable::run);
        markDirty();
    }

    @Override
    public void setNotifiableMetaTileEntity(TileEntity metaTileEntity) {
        this.notifiable = metaTileEntity;
    }
}
