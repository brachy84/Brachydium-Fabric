package brachy84.brachydium.api.gui;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.fluid.FluidStack;
import brachy84.brachydium.api.handlers.storage.IFluidHandler;
import brachy84.brachydium.api.util.TransferUtil;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.rendering.GuiHelper;
import brachy84.brachydium.gui.api.rendering.ITexture;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FluidSlotWidget extends ResourceSlotWidget<FluidStack> {

    private static final Size SIZE = new Size(18, 18);
    private static final Size FLUID_SIZE = new Size(16, 16);
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
        GuiHelper.drawFluid(matrices, getResource().asFluidVariant(), getPos().add(1, 1), FLUID_SIZE);
        drawFluidAmount(matrices, getResource(), getPos().add(17f, 12.5f), 0.45f);

    }

    public static void drawFluidAmount(MatrixStack matrices, FluidStack stack, Pos2d pos, float scale) {
        matrices.push();
        matrices.scale(scale, scale, 1);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String amountText = stack.getAmountDisplay();
        float y = pos.y / scale, x = pos.x / scale;
        x -= textRenderer.getWidth(amountText);
        textRenderer.draw(matrices, amountText, x, y, 0xFFFFFF);
        matrices.pop();
    }

    @Override
    public void renderTooltip(MatrixStack matrices, Pos2d mousePos, float delta) {
        GuiHelper.drawTooltip(matrices, Lists.transform(getResource().getTooltipLines(), Text::asOrderedText), mousePos);
    }

    @Override
    public void readClientData(int id, PacketByteBuf buf) {
        if (id == 0)
            onClickServer();
    }

    @Override
    public void readServerData(int id, PacketByteBuf buf) {
        if (id == 0)
            setResource(FluidStack.readData(buf));
    }

    @Override
    public FluidStack getResource() {
        return fluidHandler.getFluid(index);
    }

    @Override
    public boolean setResource(FluidStack resource) {
        fluidHandler.setFluid(index, resource);
        return true;
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

    public void onClickServer() {
        FluidStack currentFluid = getResource();
        long cap = fluidHandler.getCapacityAt(index);
        if (!currentFluid.isEmpty())
            cap = cap - currentFluid.getAmount();
        ItemStack cursorStack = getGui().getCursorStack();
        boolean didInsertToSlot = false;
        if (!cursorStack.isEmpty() && cursorStack.getCount() == 1) {
            // todo: use #withPlaySlot() for better compat
            ContainerItemContext itemContext = ContainerItemContext.withInitial(getGui().getCursorStack());
            Storage<FluidVariant> storage = itemContext.find(FluidStorage.ITEM);
            if (storage == null)
                return;
            if (mark != 2) {
                try (Transaction transaction = Transaction.openOuter()) {
                    for (StorageView<FluidVariant> storageView : storage.iterable(transaction)) {
                        if (storageView.isResourceBlank())
                            continue;
                        FluidVariant extractable = storageView.getResource();
                        if (currentFluid.isEmpty() || currentFluid.matches(extractable)) {
                            long extracted = storageView.extract(extractable, Math.min(storageView.getAmount(), cap), transaction);
                            if (extracted == 0)
                                continue;
                            if (currentFluid.isEmpty())
                                setResource(new FluidStack(extractable, extracted));
                            else {
                                currentFluid.increment(extracted);
                                // trigger update
                                setResource(currentFluid);
                            }
                            didInsertToSlot = true;
                            if (cursorStack.getItem() instanceof BucketItem) {
                                getGui().setCursorStack(new ItemStack(Items.BUCKET));
                            }
                            break;
                        }
                        transaction.commit();
                    }
                }
            }
            if (!didInsertToSlot && !currentFluid.isEmpty()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long inserted = storage.insert(currentFluid.asFluidVariant(), currentFluid.getAmount(), transaction);
                    if (inserted > 0) {
                        if (inserted == currentFluid.getAmount())
                            setResource(FluidStack.EMPTY);
                        else {
                            currentFluid.decrement(inserted);
                            // trigger update
                            setResource(currentFluid);
                        }
                        if (cursorStack.getItem() instanceof BucketItem) {
                            Item bucket = TransferUtil.getBucketItem(currentFluid.getFluid());
                            if (bucket != null)
                                getGui().setCursorStack(new ItemStack(bucket));
                            else
                                Brachydium.LOGGER.error("Could not find filled bucket for {}", currentFluid.getFluid().getDefaultState().getBlockState().getBlock().getName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        syncToServer(0, buf -> {
        });
        return ActionResult.SUCCESS;
    }

    @Override
    public List<Widget> getReiWidgets(AABB bounds, Pos2d reiPos) {
        List<Widget> widgets = new ArrayList<>();
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Pos2d(1, 1)).asReiPoint());
        slot.backgroundEnabled(false);
        slot.setNoticeMark(mark);
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
        widgets.add(slot);
        return widgets;
    }
}
