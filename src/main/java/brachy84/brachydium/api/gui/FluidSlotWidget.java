package brachy84.brachydium.api.gui;

import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.api.ITexture;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FluidSlotWidget extends ResourceSlotWidget<FluidStack> {

    private static final Size SIZE = new Size(18, 18);
    private final IFluidHandler fluidHandler;
    private final int index;
    private byte mark;

    public FluidSlotWidget(IFluidHandler fluidHandler, int index, Pos2d pos) {
        this.fluidHandler = Objects.requireNonNull(fluidHandler);
        this.index = index;
        setSize(SIZE);
        setPos(pos);
    }

    @Override
    public void renderResource(MatrixStack matrices, Pos2d mousePos) {
        GuiHelper.drawFluid(matrices, getResource().getFluid(), getResource().getAmount() + "mb", getPos().add(new Pos2d(1, 1)), new Size(16, 16));

    }

    @Override
    public void renderTooltip(MatrixStack matrices, Pos2d mousePos, float delta) {
        GuiHelper.drawTooltip(matrices, Lists.transform(getResource().getTooltipLines(), Text::asOrderedText), mousePos);
    }

    @Override
    public void readData(boolean fromServer, PacketByteBuf data) {
        setResource(FluidStack.readData(data));
    }

    @Override
    public void writeData(boolean fromServer, PacketByteBuf data) {
        getResource().writeData(data);
    }

    @Override
    public FluidStack getResource() {
        return fluidHandler.getStackAt(index);
    }

    @Override
    public boolean setResource(FluidStack resource) {
        fluidHandler.setStack(index, resource);
        return true;
    }

    /*public long insert(FluidStack stack, TransactionContext transactionContext) {
        return fluidSlot.insert(FluidVariant.of(stack.getFluid(), stack.getNbt()), stack.getAmount(), transactionContext);
    }

    public FluidStack extract(long amount, TransactionContext transactionContext) {
        FluidStack stack = getResource();
        long extrcated = fluidSlot.extract(FluidVariant.of(stack.getFluid(), stack.getNbt()), amount, transactionContext);
        if(extrcated == 0) return FluidStack.EMPTY;
        return stack.copyWith((int) extrcated);
    }*/

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
    public ITexture getFallbackTexture() {
        return GuiTextures.FLUID_SLOT;
    }

    public FluidSlotWidget markInput() {
        this.mark = 1;
        return this;
    }

    public FluidSlotWidget markOutput() {
        this.mark = 2;
        return this;
    }

   /* @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        ItemStack cursorStack = getCursorStack();

        if (cursorStack.getCount() == 1 && cursorStack.getItem() instanceof BucketItemAccess_AccessFluid) {
            Fluid fluid = ((BucketItemAccess_AccessFluid) cursorStack.getItem()).getFluid();
            if (fluid == Fluids.EMPTY && !isEmpty()) {
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
    }*/

    @Override
    public List<Widget> getReiWidgets(AABB bounds, Pos2d reiPos) {
        List<Widget> widgets = new ArrayList<>();
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Pos2d(1, 1)).asReiPoint());
        slot.backgroundEnabled(false);
        slot.setNoticeMark(mark);
        widgets.add(slot);
        me.shedaniel.rei.api.client.gui.widgets.Widget render = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            if (getTextures().size() > 0) {
                for (ITexture drawable : getTextures()) {
                    GuiHelper.drawTexture(matrices, drawable, reiPos, getSize());
                }
            } else {
                GuiHelper.drawTexture(matrices, getFallbackTexture(), reiPos, getSize());
            }
        }));
        widgets.add(render);
        return widgets;
    }
}
