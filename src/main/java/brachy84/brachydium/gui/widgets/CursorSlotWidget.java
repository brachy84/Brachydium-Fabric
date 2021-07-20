package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class CursorSlotWidget extends ResourceSlotWidget<ItemStack> {


    public CursorSlotWidget() {
        super(AABB.of(new Size(16, 16), Point.ZERO));
        name = "Cursorslot";
    }

    @Override
    public void receiveData(PacketByteBuf data) {
        setResource(data.readItemStack());
    }

    @Override
    public void writeData(PacketByteBuf data) {
        data.writeItemStack(getResource());
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {}

    @Override
    public void drawForeground(MatrixStack matrices, Point mousePos, float delta) {
        mousePos.translate(-8, -8);
        guiHelper.drawItem(getResource(), mousePos);
    }

    @Override
    public void renderResource(MatrixStack matrices) {}

    @Override
    public ItemStack getResource() {
        return gui.getScreen().getScreenHandler().getCursorStack();
    }

    @Override
    public boolean setResource(ItemStack resource) {
        ScreenHandler handler = gui.getScreen().getScreenHandler();
        //handler.setPreviousCursorStack(handler.getCursorStack());
        handler.disableSyncing();
        handler.setCursorStack(resource);
        //handler.sendContentUpdates();
        if(gui.player instanceof ServerPlayerEntity) {
            gui.getScreen().getScreenHandler().sendContentUpdates();
            Brachydium.LOGGER.info("Setting cursor slot to {} on server", resource);
            PacketByteBuf buf = PacketByteBufs.create();
            writeData(buf);
            ServerPlayNetworking.send((ServerPlayerEntity) gui.player, Networking.SYNC_CURSOR, buf);
        } else {
            Brachydium.LOGGER.info("Setting cursor slot to {} on client", resource);
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public TextureArea getDefaultTexture() { return null; }
}
