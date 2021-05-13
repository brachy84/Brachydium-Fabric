package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.BrachydiumRegistry;
import brachy84.brachydium.gui.api.IUIHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public abstract class UIFactory<T extends IUIHolder> {

    public static final Identifier UI_SYNC_ID = new Identifier("brachydium", "modular_gui");
    public static final BrachydiumRegistry<Identifier, UIFactory<?>> UI_FACTORY_REGISTRY = new BrachydiumRegistry<>();
    public static final Map<Integer, IUIHolder> holderCache = new HashMap<>();

    public static IUIHolder getCachedHolder(int syncId) {
        return holderCache.get(syncId);
    }

    public final Identifier id;

    public UIFactory(Identifier id) {
        this.id = id;
    }

    public final void openUI(T uiHolder, ServerPlayerEntity player) {
        if(!uiHolder.hasUI()) return;
        Brachydium.LOGGER.info("Building UI");

        OptionalInt optionalInt = player.openHandledScreen(ModularScreenHandler.createFactory(uiHolder));
        if(optionalInt.isPresent()) {
            int syncId = optionalInt.getAsInt();
            holderCache.put(syncId, uiHolder);
        }
    }

    @Environment(EnvType.CLIENT)
    public final void openClientUi(IUIHolder uiHolder, int syncId) {
    }

    public Identifier getId() {
        return id;
    }

    @Environment(EnvType.CLIENT)
    public abstract T readHolderFromSyncData(PacketByteBuf syncData);

    public abstract void writeHolderToSyncData(PacketByteBuf syncData, T holder);

    public static class SyncPacket {

        public static void read(PacketByteBuf buf) {
            Identifier factoryId = buf.readIdentifier();
            UIFactory<?> factory = UIFactory.UI_FACTORY_REGISTRY.tryGetEntry(factoryId);
            if(factory != null) {
                IUIHolder holder = factory.readHolderFromSyncData(buf);
                factory.openClientUi(holder, buf.readInt());
            }
        }

        public static <T extends IUIHolder> PacketByteBuf write(Identifier id, T uiHolder, UIFactory<T> factory) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(id);
            factory.writeHolderToSyncData(buf, uiHolder);
            return buf;
        }
    }

}
