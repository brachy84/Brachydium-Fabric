package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.wrapper.ModularScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import brachy84.brachydium.gui.Networking;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implement this to let them synchronize data between server and client
 */
public interface ISyncedWidget {

    /**
     * This methods handles data reading
     * You should override but not not call this
     * @param data to read
     */
    @ApiStatus.OverrideOnly
    void receiveData(PacketByteBuf data);

    /**
     * This methods handles data writing
     * You should override but not not call this
     * @param data to write to
     */
    @ApiStatus.OverrideOnly
    void writeData(PacketByteBuf data);

    /**
     * Use this method to send the data
     * You don't need to override it, just call
     * @param player to send data to
     */
    default void sendToClient(ServerPlayerEntity player) {
        ScreenHandler sh = player.currentScreenHandler;
        if(sh instanceof ModularScreenHandler) {
            ModularGui gui = ((ModularScreenHandler) sh).getGui();
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(gui.findIdForSynced(this));
            writeData(buf);
            ServerPlayNetworking.send(player, Networking.WIDGET_UPDATE, buf);
        }
    }

    /**
     * Use this method to send the data
     * You don't need to override it, just call
     */
    @Environment(EnvType.CLIENT)
    default void sendToServer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player != null) {
            ScreenHandler sh = player.currentScreenHandler;
            if(sh instanceof ModularScreenHandler) {
                ModularGui gui = ((ModularScreenHandler) sh).getGui();
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(gui.findIdForSynced(this));
                writeData(buf);
                ClientPlayNetworking.send(Networking.WIDGET_UPDATE, buf);
            }
        }
    }
}
