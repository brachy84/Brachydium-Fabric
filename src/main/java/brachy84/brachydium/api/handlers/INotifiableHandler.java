package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.TileEntity;

/**
 * For Item and Fluid handlers capable of notifying entities when
 * their contents change
 */
public interface INotifiableHandler {

    /**
     * Adds the notified handler to the notified list
     *
     * @param isExport boolean specifying if a handler is an output handler
     */

    default <T> void addToNotifiedList(TileEntity tile, T handler, boolean isExport) {
        if (tile != null && tile.isValid()) {
            if (isExport) {
                tile.addNotifiedOutput(handler);
            } else {
                tile.addNotifiedInput(handler);
            }
        }
    }

    /**
     * @param metaTileEntity MetaTileEntity to be notified
     */
    default void setNotifiableMetaTileEntity(TileEntity metaTileEntity) {

    }
}
