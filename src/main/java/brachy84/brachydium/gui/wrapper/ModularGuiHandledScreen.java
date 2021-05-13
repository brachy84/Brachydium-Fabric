package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.impl.GuiHelperImpl;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Shape;
import brachy84.brachydium.gui.math.Size;
import brachy84.brachydium.gui.api.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ModularGuiHandledScreen extends HandledScreen<ModularScreenHandler> {

    private IUIHolder uiHolder;
    private PlayerEntity player;
    private Interactable focused;
    //private ModularGuiOld guiOld;
    private ModularGui gui;
    private List<Interactable> interactables = new ArrayList<>();
    private GuiHelperImpl guiHelper;
    private Shape screenShape;

    public ModularGuiHandledScreen(ModularScreenHandler screenHandler, PlayerInventory inventory) {
        super(screenHandler, inventory, new LiteralText("H"));
        this.uiHolder = screenHandler.getUiHolder();
        this.player = inventory.player;
        this.gui = uiHolder.createUi(inventory.player);

        this.guiHelper = new GuiHelperImpl(new MatrixStack());
        this.guiHelper.setZOffset(-1);
        setZOffset(guiHelper.getZOffset());
        screenShape = Shape.rect(new Size(width, height));

        gui.initWidgets();
        initializeInteractables();
    }

    /*public ModularGuiHandledScreen(ModularGui gui) {
        super(new LiteralText("Hello"));
        this.gui = gui;
        this.guiHelper = new GuiHelperImpl(new MatrixStack());
        this.guiHelper.setZOffset(0);
    }*/

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        gui.resize(new Size(width, height));
        screenShape = Shape.rect(new Size(width, height));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //super.render(matrices, mouseX, mouseY, delta);
        guiHelper.setMatrixStack(matrices);
        renderBackground(matrices);
        //drawBackground(matrices, delta, mouseX, mouseY);
        //gui.forEachWidget(widget -> widget.render(matrices, Point.cartesian(mouseX, mouseY), delta));
        gui.render(matrices, new Point(mouseX, mouseY), delta);
        guiHelper.setZ(1000);
        guiHelper.drawItem(player.inventory.getCursorStack(), AABB.of(new Size(16, 16), new Point(mouseX, mouseY)).getCenter());
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        // draw dark background
        //guiHelper.drawShape(Point.ZERO, screenShape, Color.of(0.2f, 0.2f, 0.2f, 0.4f));
    }

    public void initializeInteractables() {
        interactables = gui.getInteractables();
    }

    public void forEachInteractableBlowMouse(Point point, Consumer<Interactable> consumer) {
        interactables.stream().filter(interactable -> interactable.isMouseOver(point) && interactable.getParent().isEnabled()).forEach(consumer);
    }

    @Override
    public void onClose() {
        super.onClose();
        //gui.forEachWidget(Widget::onDestroy);
        gui.close();
        //ModularGuiOld.clientGui = null;
    }

    @Nullable
    public Interactable getHoveredInteractable(Point point) {
        Interactable topWidget = null;
        for(Interactable interactable : interactables.stream().filter(interactable ->
                interactable.isMouseOver(point) && interactable.getParent().isEnabled()).collect(Collectors.toSet())) {
            if(interactable instanceof Widget) {
                if(topWidget == null) {
                    topWidget = interactable;
                    continue;
                }
                if(((Widget) interactable).getLayer() > topWidget.getParent().getLayer()) {
                    topWidget = interactable;
                }
            }
        }
        return topWidget;
    }

    public int getInteractableId(Interactable interactable) {
        return gui.findIdForSynced(interactable);
    }

    public void setFocused(Interactable interactable) {
        focused = interactable;
    }

    public boolean isFocused(Interactable interactable) {
        return focused  != null && interactable == focused;
    }

    @Nullable
    public Interactable getFocusedWidget() {
        return focused;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Point point = new Point(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(point);
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(gui.findIdForSynced(focused));
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_CLICKED, buf);
            setFocused(focused);
            setDragging(true);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Point point = new Point(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(point);
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(gui.findIdForSynced(focused));
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_RELEASED, buf);

            setDragging(false);
        }
        /*Point point = new Point(mouseX, mouseY);
        Interactable top = getHoveredInteractable(point);
        if(top != null) {
            top.onClickReleased(point, button);
            return true;
        }*/
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.getFocusedWidget() != null && this.isDragging()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(gui.findIdForSynced(getFocusedWidget()));
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);
            buf.writeDouble(deltaX);
            buf.writeDouble(deltaY);

            ClientPlayNetworking.send(Networking.MOUSE_DRAGGED, buf);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Point point = new Point(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(point);
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(gui.findIdForSynced(focused));
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeDouble(amount);

            ClientPlayNetworking.send(Networking.MOUSE_SCROLLED, buf);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // TODO implement
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // TODO implement
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // TODO implement
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        //interactables.forEach(interactable -> interactable.onMouseMoved(new Point(mouseX, mouseY)));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return gui.getBounds().isInBounds(new Point(mouseX, mouseY));
    }

    @Deprecated
    @Nullable
    @Override
    public final Element getFocused() { return null; }

    @Deprecated
    @Override
    public final void setFocused(@Nullable Element focused) { }
}
