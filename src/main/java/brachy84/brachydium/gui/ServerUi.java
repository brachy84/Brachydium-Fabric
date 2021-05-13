package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.wrapper.ModularScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ServerUi {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget != null) {
                syncedWidget.receiveData(buf);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_CLICKED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Point point = new Point(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onClick(point, button);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_RELEASED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Point point = new Point(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onClickReleased(point, button);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_DRAGGED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Point point = new Point(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                double deltaX = buf.readDouble(), deltaY = buf.readDouble();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onMouseDragged(point, button, deltaX, deltaY);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_SCROLLED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Point point = new Point(buf.readDouble(), buf.readDouble());
                double amount = buf.readDouble();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onScrolled(point, amount);
                });
            }
        }));
    }

    @Nullable
    private static ISyncedWidget getSyncedWidget(ServerPlayerEntity player, PacketByteBuf buf) {
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
