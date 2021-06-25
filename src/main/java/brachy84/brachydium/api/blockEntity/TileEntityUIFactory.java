package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.wrapper.UIFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class TileEntityUIFactory extends UIFactory<BlockEntityHolder> {

    public static final TileEntityUIFactory INSTANCE = new TileEntityUIFactory();

    private TileEntityUIFactory() {
        super(Brachydium.id("tile_ui_factory"));
    }

    public void init() {
        UIFactory.UI_FACTORY_REGISTRY.register(getId(), this);
    }

    @Override
    public BlockEntityHolder readHolderFromSyncData(PacketByteBuf syncData) {
        assert MinecraftClient.getInstance().world != null;
        BlockPos pos = syncData.readBlockPos();
        Brachydium.LOGGER.info("Reading BlockPos: " + pos);
        return (BlockEntityHolder) MinecraftClient.getInstance().world.getBlockEntity(pos);
    }

    @Override
    public void writeHolderToSyncData(PacketByteBuf syncData, BlockEntityHolder holder) {
        Brachydium.LOGGER.info("Writing BlockPos: " + holder.getPos());
        syncData.writeBlockPos(holder.getPos());
    }
}
