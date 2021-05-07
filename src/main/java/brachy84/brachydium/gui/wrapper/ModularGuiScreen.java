package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.widgets.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ModularGuiScreen extends Screen {

    private Interactable<Widget> focused;
    private ModularGui gui;
    private List<Interactable<Widget>> interactables = new ArrayList<>();

    public ModularGuiScreen(ModularGui gui) {
        super(new LiteralText(""));
        this.gui = gui;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        gui.forEachWidget(widget -> widget.render(matrices, Point.cartesian(mouseX, mouseY), delta));

    }

    public void initializeInteractables() {
        gui.forEachWidget((widget -> {
            if(widget instanceof Interactable) {
                interactables.add((Interactable<Widget>) widget);
            }
        }));
    }

    public void forEachInteractableBlowMouse(Point point, Consumer<Interactable<Widget>> consumer) {
        interactables.stream().filter(interactable -> interactable.isMouseOver(point) && interactable.getParent().isEnabled()).forEach(consumer);
    }

    @Override
    public void onClose() {
        super.onClose();
        gui.forEachWidget(Widget::onDestroy);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        gui.updateScreenSize(width, height);
    }

    @Nullable
    public Interactable<Widget> getHoveredInteractable(Point point) {
        Interactable<Widget> topWidget = null;
        for(Interactable<Widget> interactable : interactables.stream().filter(interactable ->
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

    public void setFocused(Interactable<Widget> interactable) {
        focused = interactable;
    }

    public boolean isFocused(Interactable<Widget> interactable) {
        return focused  != null && interactable == focused;
    }

    @Nullable
    public Interactable<Widget> getFocusedWidget() {
        return focused;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Point point = new Point(mouseX, mouseY);
        Interactable<Widget> focused = getHoveredInteractable(point);
        if(focused != null) {
            setFocused(focused);
            focused.onClick(point, button);
            setDragging(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setDragging(false);
        Point point = new Point(mouseX, mouseY);
        Interactable<Widget> top = getHoveredInteractable(point);
        if(top != null) {
            top.onClickReleased(point, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Point point = new Point(mouseX, mouseY);
        if(this.getFocusedWidget() != null && this.isDragging()) {
            this.getFocusedWidget().onMouseDragged(point, button, deltaX, deltaY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Point point = new Point(mouseX, mouseY);
        Interactable<Widget> top = getHoveredInteractable(point);
        if(top != null) {
            top.onScrolled(point, amount);
            return true;
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
        interactables.forEach(interactable -> interactable.onMouseMoved(new Point(mouseX, mouseY)));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return gui.getBackgroundWidget().isHovering(new Point(mouseX, mouseY));
    }

    @Deprecated
    @Nullable
    @Override
    public final Element getFocused() { return null; }

    @Deprecated
    @Override
    public final void setFocused(@Nullable Element focused) { }
}
