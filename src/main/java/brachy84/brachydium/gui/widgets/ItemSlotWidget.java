package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.gui.GuiTextures;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.SlotTags;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.*;
import brachy84.brachydium.gui.wrapper.ModularGuiHandledScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ItemSlotWidget extends ResourceSlotWidget<ItemStack> {

    private static final Size SIZE = new Size(18, 18);
    private final Slot<ItemKey> itemSlot;
    private final boolean isOutput;

    public ItemSlotWidget(Slot<ItemKey> itemSlot, Point point) {
        this(itemSlot, point, false);
    }

    public ItemSlotWidget(Slot<ItemKey> itemSlot, Point point, boolean isOutput) {
        super(AABB.of(SIZE, point));
        this.itemSlot = itemSlot;
        this.isOutput = isOutput;
        shape = Shape.rect(new Size(16, 16));
    }

    @Override
    public void drawForeground(MatrixStack matrices, Point mousePos, float delta) {
        if(getBounds().isInBounds(mousePos) && gui.getScreen() != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int x = (int) pos.x + 1, y = (int) pos.y + 2;
            guiHelper.fillGradient(matrices, x, y, x + 16, y + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            if(!isEmpty()) gui.getScreen().renderTooltip(matrices, getResource(), (int) mousePos.getX() + 8, (int) mousePos.getY());
        }
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
    public boolean setResource(ItemStack resource) {
        Transaction transaction = Transaction.create();
        if (gui.player instanceof ClientPlayerEntity) transaction = null;
        if (!itemSlot.set(transaction, ItemKey.of(resource), resource.getCount())) {
            Brachydium.LOGGER.error("Could not set " + resource + " in ItemSlot");
            return false;
        }
        if (transaction != null) transaction.commit();
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public boolean canPut(ItemStack resource, PlayerEntity player) {
        return !isOutput;
    }

    @Override
    public TextureArea getDefaultTexture() {
        return GuiTextures.SLOT;
    }


    private void setCursorStack(ItemStack stack) {
        CursorSlotWidget.get().ifPresent(cursorSlot -> {
            cursorSlot.setResource(stack);
        });
    }

    private ItemStack getCursorStack() {
        if (CursorSlotWidget.get().isPresent()) {
            return CursorSlotWidget.get().get().getResource();
        }
        throw new IllegalStateException("CursorSlotWidget can not be null!!!");
    }

    @Override
    public void receiveData(PacketByteBuf data) {
        setResource(data.readItemStack(), Action.SYNC);
    }

    @Override
    public void writeData(PacketByteBuf data) {
        data.writeItemStack(getResource());
    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
    }

    @Override
    public void onClick(Point point, int buttonId) {
        ItemStack cursorStack = getCursorStack();
        ItemStack slotStack = getResource();
        // Left click
        if (buttonId == 0) {
            if (hasShiftDown()) {
                transferStack();
            } else if (cursorStack.isEmpty()) {
                if (slotStack.isEmpty()) return;
                if (setResource(ItemStack.EMPTY, Action.TAKE))
                    setCursorStack(slotStack.copy());
            } else if (cursorStack.getItem() == slotStack.getItem()) {
                int cursorAmount = cursorStack.getCount();
                int slotAmount = slotStack.getCount();
                int moved = Math.min(cursorAmount, slotStack.getItem().getMaxCount() - slotAmount);
                if (setResource(newStack(slotStack, slotAmount + moved), Action.PUT))
                    setCursorStack(newStack(cursorStack, cursorAmount - moved));
            } else if (slotStack.isEmpty()) {
                if (setResource(cursorStack.copy(), Action.PUT))
                    setCursorStack(ItemStack.EMPTY);
            } else {
                setResource(cursorStack.copy());
                setCursorStack(slotStack.copy());
            }
            // Right click
        } else if (buttonId == 1) {
            if (cursorStack.isEmpty()) {
                if (slotStack.isEmpty()) return;
                int taken = slotStack.getCount() / 2;
                //slotStack.setCount(slotStack.getCount() - taken);
                if (setResource(newStack(slotStack, slotStack.getCount() - taken), Action.TAKE))
                    setCursorStack(newStack(slotStack, taken));
            } else if (slotStack.isEmpty()) {
                if (setResource(new ItemStack(cursorStack.getItem()), Action.PUT))
                    setCursorStack(newStack(cursorStack, cursorStack.getCount() - 1));
            } else if (slotStack.getItem() == cursorStack.getItem()) {
                if (slotStack.getItem().getMaxCount() - slotStack.getCount() >= 1) {
                    if (setResource(newStack(slotStack, slotStack.getCount() + 1), Action.PUT))
                        setCursorStack(newStack(cursorStack, cursorStack.getCount() - 1));
                }
            }
            // Scroll click
        } else if (buttonId == 2) {
            if (gui.player.isCreative() && !slotStack.isEmpty()) {
                setCursorStack(new ItemStack(slotStack.getItem(), slotStack.getMaxCount()));
            }
        }
        // lastly simply sync the slot and the cursor slot to the client
        if (gui.player instanceof ServerPlayerEntity) {
            sendToClient((ServerPlayerEntity) gui.player);
        }
    }

    private void transferStack() {
        if(getTag().trim().equals("")) return;
        List<ResourceSlotWidget<?>> slots = gui.getSlots(slot -> {
            return slot instanceof ItemSlotWidget && !slot.getTag().trim().equals("") && !slot.getTag().equals(getTag());
        });
        String[] order;
        if(getTag().equals(SlotTags.INPUT) || getTag().equals(SlotTags.OUTPUT)) {
            order = new String[]{SlotTags.PLAYER, SlotTags.HOTBAR};
        } else if(getTag().equals(SlotTags.HOTBAR)) {
            order = new String[]{SlotTags.INPUT, SlotTags.PLAYER};
        } else if(getTag().equals(SlotTags.PLAYER)) {
            order = new String[]{SlotTags.INPUT, SlotTags.HOTBAR};
        } else {
            return;
        }
        ItemStack slotStack = getResource();
        List<ISyncedWidget> syncQueue = new ArrayList<>();
        int toInsert = slotStack.getCount();
        Transaction transaction = Transaction.create();
        for(String target : order) {
            for(ResourceSlotWidget<?> rslot : slots) {
                if(!rslot.getTag().equals(target)) continue;
                ItemSlotWidget slot = (ItemSlotWidget) rslot;
                int inserted = slot.itemSlot.insert(transaction, ItemKey.of(slotStack), toInsert);
                toInsert -= inserted;
                if(inserted > 0) {
                    syncQueue.add(slot);
                }
            }
        }
        transaction.commit();
        setResource(newStack(slotStack, toInsert));
        for (ISyncedWidget syncedWidget: syncQueue) {
            syncedWidget.sendToClient((ServerPlayerEntity) gui.player);
        }
    }

    private ItemStack newStack(ItemStack stack, int amount) {
        return new ItemStack(stack.getItem(), amount);
    }

    @Override
    public void getReiWidgets(List<Widget> widgets, Point origin) {
        Point reiPos = origin.add(relativPos);
        me.shedaniel.rei.api.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Point(1, 1)).toReiPoint());
        slot.backgroundEnabled(false);
        if (itemSlot.supportsInsertion()) {
            slot.markInput();
        } else if (itemSlot.supportsExtraction()) {
            slot.markOutput();
        }
        widgets.add(slot);
        GuiHelperImpl guiHelper = new GuiHelperImpl(new MatrixStack());
        Widget render = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            guiHelper.setMatrixStack(matrices);
            if (getTextures().size() > 0) {
                for (TextureArea sprite : getTextures()) {
                    guiHelper.drawTextureArea(sprite, reiPos, size);
                }
            } else {
                guiHelper.drawTextureArea(getDefaultTexture(), reiPos, size);
            }
        }));
        widgets.add(render);
    }
}
