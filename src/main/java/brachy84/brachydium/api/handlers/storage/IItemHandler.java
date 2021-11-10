package brachy84.brachydium.api.handlers.storage;

import brachy84.brachydium.api.blockEntity.InventoryListener;
import brachy84.brachydium.api.handlers.INotifiableHandler;
import net.minecraft.inventory.Inventory;

public interface IItemHandler extends Inventory, InventoryListener, INotifiableHandler {
}
