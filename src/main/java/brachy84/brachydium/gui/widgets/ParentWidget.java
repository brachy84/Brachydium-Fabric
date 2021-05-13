package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import com.google.common.collect.BiMap;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class ParentWidget extends Widget {

    private final BiMap<Integer, Widget> children;
    private final List<ParentWidget> parentsInChildren = new ArrayList<>();
    protected final BackgroundWidget background;

    public ParentWidget(BackgroundWidget background, BiMap<Integer, Widget> children) {
        super(background.getBounds());
        this.background = background;
        this.children = children;
    }

    @Override
    public void render(MatrixStack matrices, Point mousePos, float delta) {
        super.render(matrices, mousePos, delta);
        forEachChild(widget -> widget.render(matrices, mousePos, delta));
        forEachChildParent(parentWidget -> parentWidget.render(matrices, mousePos, delta));
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {}

    /**
     * @return the children of this widget
     */
    public Collection<Widget> getChildren() {
        return children.values();
    }

    /**
     * @return children with all sub-children
     */
    public Collection<Widget> getAllChildren() {
        List<Widget> widgets = new ArrayList<>();
        for(Widget widget : getChildren()) {
            widgets.add(widget);
            if(widget instanceof ParentWidget) {
                widgets.addAll(((ParentWidget) widget).getAllChildren());
            }
        }
        return widgets;
    }

    public void forEachChild(Consumer<Widget> consumer) {
        for(Widget child : getChildren()) {
            consumer.accept(child);
        }
    }

    public void forAllChildren(Consumer<Widget> consumer) {
        List<ParentWidget> nextLayer = new ArrayList<>();
        for(Widget child : getChildren()) {
            consumer.accept(child);
            if(child instanceof ParentWidget) {
                nextLayer.add((ParentWidget) child);
            }
        }
        if(nextLayer.size() > 0) {
            for(ParentWidget parent : nextLayer) {
                parent.forEachChild(consumer);
            }
        }
    }

    /**
     * Goes through all ParentWidgets in children
     */
    public void forEachChildParent(Consumer<ParentWidget> consumer) {
        if(parentsInChildren.size() > 0) {
            for(ParentWidget widget : parentsInChildren) {
                consumer.accept(widget);
            }
        }
    }

    public void resize(Size screenSize) {
        pos = screenSize.getCenteringPointForChild(size);
        forEachChild(widget -> widget.setParentPosition(pos));
        forEachChildParent(widget -> widget.resize(screenSize));
    }

    public void initWidgets(ModularGui gui) {
        AtomicInteger childrenLayer = new AtomicInteger(layer);
        forEachChild(widget -> {
            widget.setGui(gui);
            //widget.setSizes(this);
            widget.onInit();
            if(widget instanceof ISyncedWidget) {
                gui.addSyncedWidget((ISyncedWidget) widget);
            }
            widget.setLayer(childrenLayer.getAndIncrement());
            if(widget instanceof ParentWidget)
                parentsInChildren.add((ParentWidget) widget);
        });
        forEachChildParent(widget -> {
            widget.setLayer((int) (Math.ceil(layer / 100D) * 100));
            widget.initWidgets(gui);
        });
    }
}
