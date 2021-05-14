package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.Sprites;
import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;

public class FluidSlotWidget extends ResourceSlotWidget<FluidStack> {

    private static final Size SIZE = new Size(18, 18);
    private Slot<Fluid> fluidSlot;

    public FluidSlotWidget(Slot<Fluid> fluidSlot, Point point) {
        super(AABB.of(SIZE, point));
        this.fluidSlot = fluidSlot;
    }

    @Override
    public void receiveData(PacketByteBuf data) {

    }

    @Override
    public void writeData(PacketByteBuf data) {

    }

    @Override
    public void renderResource(MatrixStack matrices) {

    }

    @Override
    public FluidStack getResource() {
        return new FluidStack(fluidSlot.getKey(Transaction.GLOBAL), fluidSlot.getQuantity(Transaction.GLOBAL));
    }

    @Override
    public void setResource(FluidStack resource) {
        fluidSlot.set(Transaction.GLOBAL, resource.getFluid(), resource.getAmount());
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public ISprite getDefaultTexture() {
        return Sprites.FLUID_SLOT;
    }
}
