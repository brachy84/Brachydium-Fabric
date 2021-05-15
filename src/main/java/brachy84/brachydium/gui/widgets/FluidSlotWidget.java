package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.gui.Sprites;
import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

public class FluidSlotWidget extends ResourceSlotWidget<FluidStack> {

    private static final Size SIZE = new Size(18, 18);
    private final Slot<Fluid> fluidSlot;

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

    @Override
    public Optional<Widget> getReiWidget() {
        me.shedaniel.math.Point point = relativPos.toReiPoint();
        me.shedaniel.rei.api.widgets.Slot slot = Widgets.createSlot(point);
        slot.backgroundEnabled(false);
        GuiHelperImpl guiHelper = new GuiHelperImpl(new MatrixStack());
        Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            guiHelper.setMatrixStack(matrices);
            if (getTextures().size() > 0) {
                for (ISprite sprite : getTextures()) {
                    guiHelper.drawSprite(sprite, relativPos);
                }
            } else {
                guiHelper.drawSprite(getDefaultTexture(), relativPos);
            }
        }));
        if (fluidSlot.supportsInsertion()) {
            slot.markInput();
        } else if (fluidSlot.supportsExtraction()) {
            slot.markOutput();
        }
        return Optional.of(slot);
    }
}
