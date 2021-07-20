package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.api.fluid.Amounts;
import brachy84.brachydium.api.fluid.BucketHelper;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.SingleFluidTank;
import brachy84.brachydium.gui.GuiTextures;
import brachy84.brachydium.gui.api.ResourceSlotWidget;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import com.mojang.blaze3d.systems.RenderSystem;
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

import java.util.List;
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
        setResource(FluidStack.readData(data));
    }

    @Override
    public void writeData(PacketByteBuf data) {
        getResource().writeData(data);
    }

    @Override
    public void drawForeground(MatrixStack matrices, Point mousePos, float delta) {
        if(getBounds().isInBounds(mousePos)) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int x = (int) pos.x + 1, y = (int) pos.y + 2;
            guiHelper.fillGradient(matrices, x, y, x + 16, y + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            if(!isEmpty()) guiHelper.renderFluidTooltip(getResource(), mousePos.add(new Point(8, 0)));
        }
    }

    @Override
    public void renderResource(MatrixStack matrices) {
        guiHelper.drawFluid(getResource(), pos.add(new Point(1, 1)), new Size(16, 16));
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
        gui.setCursorStack(stack);
    }

    private ItemStack getCursorStack() {
        return gui.getCursorStack();
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public TextureArea getDefaultTexture() {
        return GuiTextures.FLUID_SLOT;
    }

    @Override
    public void onClick(Point point, int buttonId) {
        ItemStack cursorStack = getCursorStack();

        if(cursorStack.getCount() == 1 && cursorStack.getItem() instanceof BucketItemAccess_AccessFluid) {
            Fluid fluid = ((BucketItemAccess_AccessFluid) cursorStack.getItem()).getFluid();
            if(fluid == Fluids.EMPTY && !fluidSlot.isEmpty(null)) {
                fillBucket(fluidSlot);
            } else if(fluid != Fluids.EMPTY){
                emptyBucket(cursorStack, fluidSlot);
            }
        }

        sendToClient((ServerPlayerEntity) gui.player);
    }

    public boolean emptyBucket(ItemStack stack, Insertable<Fluid> insertable) {
        Fluid fluid = BucketHelper.getFluid(stack.getItem());
        if(fluid != Fluids.EMPTY) {
            try(Transaction transaction = Transaction.create()) {
                if(insertable.insert(transaction, fluid, Amounts.BUCKET) != Amounts.BUCKET) {
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
        try(Transaction transaction = Transaction.create()) {
            extractable.extract(transaction, tank);
            fluid = tank.getKey(transaction);
            transaction.abort();
        }
        if(fluid != null && fluid != Fluids.EMPTY) {
            try(Transaction transaction = Transaction.create()) {
                int extracted = extractable.extract(transaction, fluid, Amounts.BUCKET);
                if(extracted != Amounts.BUCKET) {
                    transaction.abort();
                    return false;
                }
                //TODO: This might get laggy with a lot of items
                Optional<Item> bucket = BucketHelper.getBucketForFluid(fluid);
                if(bucket.isPresent()) {
                    setCursorStack(new ItemStack(bucket.get()));
                    return true;
                }
                transaction.abort();
            }
        }
        return false;
    }

    @Override
    public void getReiWidgets(List<Widget> widgets, Point origin) {
        Point reiPos = origin.add(relativPos);
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Point(1, 1)).toReiPoint());
        slot.backgroundEnabled(false);
        if (fluidSlot.supportsInsertion()) {
            slot.markInput();
        } else if (fluidSlot.supportsExtraction()) {
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
