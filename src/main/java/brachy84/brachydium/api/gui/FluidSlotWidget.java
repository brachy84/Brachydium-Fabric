package brachy84.brachydium.api.gui;

import brachy84.brachydium.api.fluid.Amounts;
import brachy84.brachydium.api.fluid.BucketHelper;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.SingleFluidTank;
import brachy84.brachydium.gui.api.IDrawable;
import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import brachy84.brachydium.gui.internal.GuiHelper;
import com.google.common.collect.Lists;
import io.github.astrarre.transfer.internal.mixin.BucketItemAccess_AccessFluid;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Optional;

public class FluidSlotWidget extends ResourceSlotWidget<FluidStack> {

    private static final Size SIZE = new Size(18, 18);
    private final Slot<Fluid> fluidSlot;

    public FluidSlotWidget(Slot<Fluid> fluidSlot, Pos2d point) {
        this.fluidSlot = fluidSlot;
    }

    @Override
    public void readData(PacketByteBuf data) {
        setResource(FluidStack.readData(data));
    }

    @Override
    public void writeData(PacketByteBuf data) {
        getResource().writeData(data);
    }

    @Override
    public void renderResource(IGuiHelper helper, MatrixStack matrices) {
        helper.drawFluid(matrices, getResource().getFluid(), getResource().getAmount() + "mb", getPos().add(new Pos2d(1, 1)), new Size(16, 16));
    }

    @Override
    public void renderTooltip(IGuiHelper helper, MatrixStack matrices, float delta) {
        helper.drawTooltip(matrices, Lists.transform(getResource().getTooltipLines(), Text::asOrderedText), helper.getMousePos());
    }

    @Override
    public FluidStack getResource() {
        return new FluidStack(fluidSlot.getKey(Transaction.GLOBAL), fluidSlot.getQuantity(Transaction.GLOBAL));
    }

    @Override
    public boolean setResource(FluidStack resource) {
        return fluidSlot.set(Transaction.GLOBAL, resource.getFluid(), resource.getAmount());
    }

    private void setCursorStack(ItemStack stack) {
        getGui().setCursorStack(stack);
    }

    private ItemStack getCursorStack() {
        return getGui().getCursorStack();
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public IDrawable getFallbackTexture() {
        return null;
    }

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        ItemStack cursorStack = getCursorStack();

        if (cursorStack.getCount() == 1 && cursorStack.getItem() instanceof BucketItemAccess_AccessFluid) {
            Fluid fluid = ((BucketItemAccess_AccessFluid) cursorStack.getItem()).getFluid();
            if (fluid == Fluids.EMPTY && !fluidSlot.isEmpty(null)) {
                fillBucket(fluidSlot);
            } else if (fluid != Fluids.EMPTY) {
                emptyBucket(cursorStack, fluidSlot);
            }
        }

        sendToClient((ServerPlayerEntity) getGui().player);
        return ActionResult.PASS;
    }

    public boolean emptyBucket(ItemStack stack, Insertable<Fluid> insertable) {
        Fluid fluid = BucketHelper.getFluid(stack.getItem());
        if (fluid != Fluids.EMPTY) {
            try (Transaction transaction = Transaction.create()) {
                if (insertable.insert(transaction, fluid, Amounts.BUCKET) != Amounts.BUCKET) {
                    transaction.abort();
                    return false;
                }
            }
            setCursorStack(new ItemStack(Items.BUCKET));
            return true;
        }
        return false;
    }

    public boolean fillBucket(Extractable<Fluid> extractable) {
        Fluid fluid;
        SingleFluidTank tank = new SingleFluidTank(1);
        try (Transaction transaction = Transaction.create()) {
            extractable.extract(transaction, tank);
            fluid = tank.getKey(transaction);
            transaction.abort();
        }
        if (fluid != null && fluid != Fluids.EMPTY) {
            try (Transaction transaction = Transaction.create()) {
                int extracted = extractable.extract(transaction, fluid, Amounts.BUCKET);
                if (extracted != Amounts.BUCKET) {
                    transaction.abort();
                    return false;
                }
                //TODO: This might get laggy with a lot of items
                Optional<Item> bucket = BucketHelper.getBucketForFluid(fluid);
                if (bucket.isPresent()) {
                    setCursorStack(new ItemStack(bucket.get()));
                    return true;
                }
                transaction.abort();
            }
        }
        return false;
    }

    @Override
    public void getReiWidgets(List<Widget> widgets) {
        Pos2d reiPos = new Pos2d(0, 0);
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Pos2d(1, 1)).asReiPoint());
        slot.backgroundEnabled(false);
        if (fluidSlot.supportsInsertion()) {
            slot.markInput();
        } else if (fluidSlot.supportsExtraction()) {
            slot.markOutput();
        }
        widgets.add(slot);
        me.shedaniel.rei.api.client.gui.widgets.Widget render = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            GuiHelper guiHelper = GuiHelper.create(0, new Pos2d(mouseX, mouseY));
            if (getTextures().size() > 0) {
                for (IDrawable drawable : getTextures()) {
                    guiHelper.drawTexture(matrices, drawable, reiPos, getSize());
                }
            } else {
                guiHelper.drawTexture(matrices, getFallbackTexture(), reiPos, getSize());
            }
        }));
        widgets.add(render);
    }
}
