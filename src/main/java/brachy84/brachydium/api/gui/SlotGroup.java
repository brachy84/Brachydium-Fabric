package brachy84.brachydium.api.gui;

import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.gui.api.ITexture;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.MultiChildWidget;
import brachy84.brachydium.gui.internal.Widget;
import net.minecraft.inventory.Inventory;

import java.util.List;

public class SlotGroup extends MultiChildWidget {

    private final int size;
    private final int itemInvSize;
    private int width, height = -1;
    private ITexture[] itemSlotBackgrounds;
    private ITexture[] fluidSlotBackgrounds;

    public SlotGroup(Inventory inventory) {
        this.size = inventory.size();
        this.itemInvSize = size;
        for (int i = 0; i < size; i++) {
            child(new ItemSlotWidget(inventory, i, Pos2d.ZERO));
        }
        width = (int) Math.ceil(Math.sqrt(size));
    }

    public SlotGroup(IFluidHandler inventory) {
        this.size = inventory.getTanks();
        this.itemInvSize = 0;
        for (int i = 0; i < size; i++) {
            child(new FluidSlotWidget(inventory, i, Pos2d.ZERO));
        }
        width = (int) Math.ceil(Math.sqrt(size));
    }

    public SlotGroup(Inventory inventory, IFluidHandler fluidHandler) {
        this.size = inventory.size() + fluidHandler.getTanks();
        this.itemInvSize = inventory.size();
        for (int i = 0; i < itemInvSize; i++) {
            child(new ItemSlotWidget(inventory, i, Pos2d.ZERO));
        }
        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            child(new FluidSlotWidget(fluidHandler, i, Pos2d.ZERO));
        }
        width = (int) Math.ceil(Math.sqrt(size));
    }

    public SlotGroup setFluidSlotBackgrounds(ITexture... fluidSlotBackgrounds) {
        this.fluidSlotBackgrounds = fluidSlotBackgrounds;
        return this;
    }

    public SlotGroup setItemSlotBackgrounds(ITexture... itemSlotBackgrounds) {
        this.itemSlotBackgrounds = itemSlotBackgrounds;
        return this;
    }

    public SlotGroup setWidth(int i) {
        this.width = i;
        return this;
    }

    public SlotGroup setHeight(int i) {
        this.height = i;
        return this;
    }

    @Override
    public void onInit() {
        super.onInit();
        if(itemSlotBackgrounds != null) {
            for(Widget widget : getChildren()) {
                if(widget instanceof ItemSlotWidget itemSlot)
                    itemSlot.addBackgroundSprites(itemSlotBackgrounds);
            }
        }
        if(fluidSlotBackgrounds != null) {
            for(Widget widget : getChildren()) {
                if(widget instanceof FluidSlotWidget fluidSlot)
                    fluidSlot.addBackgroundSprites(fluidSlotBackgrounds);
            }
        }
    }

    @Override
    public void layoutChildren() {
        List<Widget> children = getChildren();
        if (width > 0) {
            if (height <= 0) {
                height = size / width;
            }
        } else if (height > 0) {
            width = size / height;
        }
        setSize(new Size(width, height));
        if (width > 0 && height > 0) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    children.get(j + i * width).setPos(new Pos2d(i * 18, j * 18));
                }
            }
        }
    }
}
