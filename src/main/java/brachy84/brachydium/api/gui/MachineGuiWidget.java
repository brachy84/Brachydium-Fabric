package brachy84.brachydium.api.gui;

import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.gui.api.ProgressTexture;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.MultiChildWidget;
import brachy84.brachydium.gui.api.widgets.ProgressBarWidget;
import brachy84.brachydium.gui.internal.Widget;
import net.minecraft.inventory.Inventory;

import java.util.function.DoubleSupplier;

public class MachineGuiWidget extends MultiChildWidget {

    //private float maxHeight;

    public MachineGuiWidget(Size size) {
        setSize(size);
    }

    public MachineGuiWidget(Pos2d pos, Size size) {
        setPos(pos);
        setSize(size);
    }

    /*@Override
    public void validateSize() {
        if(!isInitialised()) return;
        if(maxHeight <= 0) {
            maxHeight = getParent().getSize().height() - (4 * 18 + 4);
        }
        setSize(new Size(getParent().getSize().width(), maxHeight));
        super.validateSize();
    }*/

    public MachineGuiWidget slot(Inventory inv, int index, Pos2d pos) {
        addChild(new ItemSlotWidget(inv, index, pos));
        return this;
    }

    public MachineGuiWidget slot(IFluidHandler inv, int index, Pos2d pos) {
        addChild(new FluidSlotWidget(inv, index, pos));
        return this;
    }

    public MachineGuiWidget slotGroup(Inventory inv, Alignment alignment, EdgeInset edgeInset) {
        addChild(new SlotGroup(inv).setAlignment(alignment).setMargin(edgeInset));
        return this;
    }

    public MachineGuiWidget slotGroup(IFluidHandler inv, Alignment alignment, EdgeInset edgeInset) {
        addChild(new SlotGroup(inv).setAlignment(alignment).setMargin(edgeInset));
        return this;
    }

    public MachineGuiWidget slotGroup(Inventory inv, IFluidHandler fluidHandler, Alignment alignment, EdgeInset edgeInset) {
        addChild(new SlotGroup(inv, fluidHandler).setAlignment(alignment).setMargin(edgeInset));
        return this;
    }

    public MachineGuiWidget slotGroup(Inventory inv, Pos2d pos) {
        addChild(new SlotGroup(inv).setPos(pos));
        return this;
    }

    public MachineGuiWidget slotGroup(IFluidHandler inv, Pos2d pos) {
        addChild(new SlotGroup(inv).setPos(pos));
        return this;
    }

    public MachineGuiWidget slotGroup(Inventory inv, IFluidHandler fluidHandler, Pos2d pos) {
        addChild(new SlotGroup(inv, fluidHandler).setPos(pos));
        return this;
    }

    public MachineGuiWidget inputSlots(Inventory inv, IFluidHandler fluidHandler) {
        Alignment alignment = Alignment.CenterLeft;
        EdgeInset edgeInset = EdgeInset.left(8);
        if (inv == null) {
            if (fluidHandler == null)
                throw new NullPointerException();
            slotGroup(fluidHandler, alignment, edgeInset);
        } else {
            if (fluidHandler == null) {
                slotGroup(inv, alignment, edgeInset);
            } else {
                slotGroup(inv, fluidHandler, alignment, edgeInset);
            }
        }
        return this;
    }

    public MachineGuiWidget outputSlots(Inventory inv, IFluidHandler fluidHandler) {
        Alignment alignment = Alignment.CenterRight;
        EdgeInset edgeInset = EdgeInset.right(8);
        if (inv == null) {
            if (fluidHandler == null)
                throw new NullPointerException();
            slotGroup(fluidHandler, alignment, edgeInset);
        } else {
            if (fluidHandler == null) {
                slotGroup(inv, alignment, edgeInset);
            } else {
                slotGroup(inv, fluidHandler, alignment, edgeInset);
            }
        }
        return this;
    }

    public MachineGuiWidget progressBar(DoubleSupplier progress, ProgressTexture texture) {
        addChild(new ProgressBarWidget(progress, texture).setAlignment(Alignment.Center));
        return this;
    }

    @Override
    public MachineGuiWidget child(Widget child) {
        super.child(child);
        return this;
    }

    @Override
    public void layoutChildren() {

    }
}
