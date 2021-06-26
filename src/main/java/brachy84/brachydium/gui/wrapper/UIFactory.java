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

public class UIFactory {

    public static final Identifier UI_SYNC_ID = new Identifier("brachydium", "modular_gui");
    public static final Map<Integer, IUIHolder> holderCache = new HashMap<>();

    public static IUIHolder getCachedHolder(int syncId) {
        return holderCache.remove(syncId);
        //return holderCache.get(syncId);
    }

    public static void openUI(IUIHolder uiHolder, ServerPlayerEntity player) {
        if (!uiHolder.hasUI()) return;
        Brachydium.LOGGER.info("Building UI");

        OptionalInt optionalInt = player.openHandledScreen(ModularScreenHandler.createFactory(uiHolder));
        optionalInt.ifPresent(id -> holderCache.put(id, uiHolder));
    }
}
