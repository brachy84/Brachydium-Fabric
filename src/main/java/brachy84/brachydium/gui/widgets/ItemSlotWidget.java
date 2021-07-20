package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.util.NumUtil;
import brachy84.brachydium.gui.GuiTextures;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import brachy84.brachydium.gui.wrapper.McSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ItemSlotWidget extends ResourceSlotWidget<ItemStack> {

    public static final Size SIZE = new Size(18, 18);

    private final McSlot mcSlot;
    private final Slot<ItemKey> itemSlot;

    public ItemSlotWidget(ArrayParticipant<ItemKey> inv, int index, Point pos) {
        super(AABB.of(SIZE, pos));
        // all kind of interaction is handled by mc
        mcSlot = new McSlot(this, inv, index, true, NumUtil.asInt(getPos().x + 1), NumUtil.asInt(getPos().y + 1.5));
        itemSlot = inv.getSlots().get(index);
    }

    public ItemSlotWidget setInsertable(boolean canInsert) {
        mcSlot.setInsertable(canInsert);
        return this;
    }

    public ItemSlotWidget setExtractable(boolean canTake) {
        mcSlot.setExtractable(canTake);
        return this;
    }

    @Override
    public void onInit() {
        super.onInit();
        gui.addMcItemSlot(mcSlot);
    }

    @Override
    public void receiveData(PacketByteBuf data) {
    }

    @Override
    public void writeData(PacketByteBuf data) {
    }

    @Override
    public void renderResource(MatrixStack matrices) {
        //guiHelper.drawItem(getResource(), pos);
    }

    @Override
    public ItemStack getResource() {
        return itemSlot.getKey(Transaction.GLOBAL).createItemStack(itemSlot.getQuantity(Transaction.GLOBAL));
    }

    @Override
    public boolean setResource(ItemStack resource) {
        if(RenderSystem.isOnRenderThread()) {
            Brachydium.LOGGER.info("Setting slot to {} on client", resource);
        } else {
            Brachydium.LOGGER.info("Setting slot to {} on server", resource);
        }
        if (!itemSlot.set(null, ItemKey.of(resource), resource.getCount())) {
            Brachydium.LOGGER.error("Could not set " + resource + " in ItemSlot");
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public TextureArea getDefaultTexture() {
        return GuiTextures.SLOT;
    }

    public Slot<ItemKey> getItemSlot() {
        return itemSlot;
    }

    @Override
    public void getReiWidgets(List<Widget> widgets, Point origin) {
        Point reiPos = origin.add(relativPos);
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Point(1, 1)).toReiPoint());
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
