package brachy84.brachydium.compat.wthit;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;

public class BrachydiumWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addDisplayItem(new IBlockComponentProvider() {
            @Override
            public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
                if (accessor.getBlockEntity() instanceof BlockEntityHolder) {
                    TileEntity tile = ((BlockEntityHolder) accessor.getBlockEntity()).getActiveTileEntity();
                    if (tile != null) {
                        return tile.asStack();
                    }
                }
                return ItemStack.EMPTY;
            }
        }, BlockEntityHolder.class, 100);
    }
}
