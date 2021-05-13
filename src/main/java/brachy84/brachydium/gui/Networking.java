package brachy84.brachydium.gui;

import brachy84.brachydium.Brachydium;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Networking {

    public static final Identifier WIDGET_UPDATE = Brachydium.id("widget_sync");

    public static final Identifier MOUSE_CLICKED = Brachydium.id("mouse_clicked");
    public static final Identifier MOUSE_RELEASED = Brachydium.id("mouse_released");
    public static final Identifier MOUSE_DRAGGED = Brachydium.id("mouse_dragged");
    public static final Identifier MOUSE_SCROLLED = Brachydium.id("mouse_scrolled");


    public static class Package {

        private CompoundTag data;
        private Identifier id;

        public Package() {}

        public Package(Identifier id, CompoundTag data) {
            this.id = id;
            this.data = data;
        }

        public void send(ServerPlayerEntity player, Identifier channel) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(id);
            buf.writeCompoundTag(data);
            ServerPlayNetworking.send(player, channel, buf);
        }

        public void read(PacketByteBuf buf) {
            this.id = buf.readIdentifier();
            this.data = buf.readCompoundTag();
        }
    }

}
