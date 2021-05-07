package brachy84.brachydium.api.gui_v1;

import brachy84.brachydium.api.blockEntity.IInventoryHolder;
import brachy84.brachydium.api.blockEntity.MetaBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IUiHolder {

    boolean hasUi();

    default void open(ServerPlayerEntity playerEntity, IInventoryHolder inventoryHolder) {
        BrachydiumGui.open(playerEntity, this, inventoryHolder);
    }

    BrachydiumGui.Builder buildUi(BrachydiumGui.Builder builder);
}
