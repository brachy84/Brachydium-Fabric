package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.Sprites;
import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ItemSlotWidget extends ResourceSlotWidget<ItemStack> {

    private static final Size SIZE = new Size(18, 18);
    private final Slot<ItemKey> itemSlot;

    public ItemSlotWidget(Slot<ItemKey> itemSlot, Point point) {
        super(AABB.of(SIZE, point));
        this.itemSlot = itemSlot;
    }

    @Override
    public void renderResource(MatrixStack matrices) {
        guiHelper.drawItem(getResource(), pos);
    }

    @Override
    public ItemStack getResource() {
        return itemSlot.getKey(Transaction.GLOBAL).createItemStack(itemSlot.getQuantity(Transaction.GLOBAL));
    }

    @Override
    public void setResource(ItemStack resource) {
        itemSlot.set(Transaction.GLOBAL, ItemKey.of(resource), resource.getCount());
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public ISprite getDefaultTexture() {
        return Sprites.SLOT;
    }


    private void setCursorStack(ItemStack stack) {
        gui.player.inventory.setCursorStack(stack);
    }

    @Override
    public void receiveData(PacketByteBuf data) {
        setResource(data.readItemStack());
        MinecraftClient.getInstance().player.inventory.setCursorStack(data.readItemStack());
        //setCursorStack(data.readItemStack());
    }

    @Override
    public void writeData(PacketByteBuf data) {
        data.writeItemStack(getResource());
        data.writeItemStack(gui.player.inventory.getCursorStack());
    }

    @Override
    public void onClick(Point point, int buttonId) {
        Brachydium.LOGGER.info("Clicking slot");
        ItemStack cursorStack = gui.player.inventory.getCursorStack();
        ItemStack slotStack = getResource();
        // Left click
        if(buttonId == 0) {
            if(hasShiftDown()) {

            }
            if(cursorStack.isEmpty()) {
                if(slotStack.isEmpty()) return;
                setCursorStack(slotStack.copy());
                setResource(ItemStack.EMPTY);
            } else if(cursorStack.getItem() == slotStack.getItem()) {
                int cursorAmount = cursorStack.getCount();
                int slotAmount = slotStack.getCount();
                int moved = Math.min(cursorAmount, slotStack.getItem().getMaxCount() - slotAmount);
                cursorStack.setCount(cursorAmount + moved);
                slotStack.setCount(slotAmount - moved);
            } else if(slotStack.isEmpty()) {
                setResource(cursorStack.copy());
                setCursorStack(ItemStack.EMPTY);
            }
        // Right click
        } else if(buttonId == 1) {
            if(hasShiftDown()) {

            }
            if(cursorStack.isEmpty()) {
                if(slotStack.isEmpty()) return;
                int taken = slotStack.getCount() / 2;
                slotStack.setCount(slotStack.getCount() - taken);
                setCursorStack(new ItemStack(slotStack.getItem(), taken));
            } else if(slotStack.isEmpty()) {
                setResource(new ItemStack(cursorStack.getItem()));
                cursorStack.setCount(cursorStack.getCount() - 1);
            } else if(slotStack.getItem() == cursorStack.getItem()) {
                if(slotStack.getItem().getMaxCount() - slotStack.getCount() >= 1) {
                    slotStack.setCount(slotStack.getCount() + 1);
                    cursorStack.setCount(cursorStack.getCount() - 1);
                }
            }
        // Scroll click
        } else if(buttonId == 2) {
            if(gui.player.isCreative() && !slotStack.isEmpty()) {
                setCursorStack(new ItemStack(slotStack.getItem(), slotStack.getMaxCount()));
            }
        }
        // lastly simply sync the slot and the cursor slot to the client
        if(gui.player instanceof ServerPlayerEntity) {
            sendToClient((ServerPlayerEntity) gui.player);
        }
    }
}
