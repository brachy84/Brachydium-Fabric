package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.INotifiableHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface IItemHandler extends Inventory, InventoryListener, INotifiableHandler {

    /**
     * Sets the fluid in the slot without causing listeners to run.
     * Good for initialisation or when {@link #setStack(int, ItemStack)} is called a lot
     *
     * @param slot  to set
     * @param stack to set
     */
    void setStackSilently(int slot, ItemStack stack);

    @Override
    default void setStack(int slot, ItemStack stack) {
        setStackSilently(slot, stack);
        markDirty();
    }

    @Override
    default void clear() {
        for(int i = 0; i < size(); i++) {
            setStackSilently(i, ItemStack.EMPTY);
        }
        markDirty();
    }
}
