package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.wrapper.ModularGuiHandledScreen;
import brachy84.brachydium.gui.wrapper.ModularScreenHandler;
import brachy84.brachydium.gui.wrapper.UIFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientUi {

    public static void init() {
        ScreenRegistry.<ModularScreenHandler, ModularGuiHandledScreen>register(ModularScreenHandler.MODULAR_SCREEN_HANDLER, (screenHandler, inv, title) -> {
            return new ModularGuiHandledScreen(screenHandler, inv);
        });

        ClientPlayNetworking.registerGlobalReceiver(UIFactory.UI_SYNC_ID, (client, handler, buf, responseSender) -> {
            UIFactory.SyncPacket.read(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((client, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(buf);
            if(syncedWidget != null) {
                syncedWidget.receiveData(buf);
            }
        }));
    }

    @Nullable
    private static ISyncedWidget getSyncedWidget(PacketByteBuf buf) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return null;
        ScreenHandler sh = player.currentScreenHandler;
        if(sh instanceof ModularScreenHandler) {
            ModularGui gui = ((ModularScreenHandler) sh).getGui();
            if(gui != null) {
                return gui.findSyncedWidget(buf.readInt());
            }
        }
        return null;
    }
}
