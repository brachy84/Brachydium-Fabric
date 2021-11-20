package brachy84.brachydium.api.gui;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.gui.api.UIFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class TileEntityUiFactory extends UIFactory<TileEntity> {

    public static final TileEntityUiFactory INSTANCE = new TileEntityUiFactory();

    private TileEntityUiFactory() {
    }

    @Override
    public Identifier getId() {
        return Brachydium.id("tile_entity_factory");
    }

    @Override
    public TileEntity readHolderFromSyncData(PacketByteBuf syncData) {
        return TileEntity.getOf(MinecraftClient.getInstance().world, syncData.readBlockPos());
    }

    @Override
    public void writeHolderToSyncData(PacketByteBuf syncData, TileEntity holder) {
        syncData.writeBlockPos(holder.getPos());
    }
}
