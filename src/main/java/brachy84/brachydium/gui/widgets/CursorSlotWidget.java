package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class CursorSlotWidget extends ResourceSlotWidget<ItemStack> {

    public CursorSlotWidget() {
        super(AABB.of(new Size(16, 16), Point.ZERO));
        name = "Cursorslot";
    }

    public static Optional<CursorSlotWidget> get() {
        return Optional.ofNullable(RootWidget.getCursorSlot());
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
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        mousePos.translate(-8, -8);
        guiHelper.drawItem(getResource(), mousePos);
    }

    @Override
    public void renderResource(MatrixStack matrices) {}

    @Override
    public ItemStack getResource() {
        return gui.player.inventory.getCursorStack();
    }

    @Override
    public boolean setResource(ItemStack resource) {
        gui.player.inventory.setCursorStack(resource);
        if(gui.player instanceof ServerPlayerEntity) {
            sendToClient((ServerPlayerEntity) gui.player);
        }
        return true;
    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        Brachydium.LOGGER.info("Set layer of Cursor to " + layer);
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public TextureArea getDefaultTexture() { return null; }
}
