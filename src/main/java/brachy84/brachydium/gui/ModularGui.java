package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.math.Size;
import brachy84.brachydium.gui.widgets.RootWidget;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ModularGui implements ISizeProvider {

    private final RootWidget rootWidget;

    private Size screenSize;
    //private final ImmutableList<Runnable> uiOpenCallback;
    //private final ImmutableList<Runnable> uiCloseCallback;
    private final BiMap<Integer, ISyncedWidget> syncedWidgets = HashBiMap.create(1);
    private int nextSyncedId = 0;

    /**
     * UIHolder of this modular UI
     */
    public final IUIHolder holder;
    public final PlayerEntity player;

    public ModularGui(RootWidget rootWidget, IUIHolder holder, PlayerEntity player) {
        this.rootWidget = rootWidget;
        //this.uiOpenCallback = uiOpenCallback;
        //this.uiCloseCallback = uiCloseCallback;
        this.holder = holder;
        this.player = player;
        this.screenSize = Size.ZERO;
    }

    public void resize(Size screenSize) {
        this.screenSize = screenSize;
        rootWidget.resize(screenSize);
    }

    public void initWidgets() {
        rootWidget.initWidgets(this);
    }

    public void addSyncedWidget(ISyncedWidget widget) {
        syncedWidgets.forcePut(nextSyncedId++, widget);
    }

    public List<Interactable> getInteractables() {
        List<Interactable> interactables = new ArrayList<>();
        rootWidget.forAllChildren(widget -> {
            if(widget instanceof Interactable) {
                interactables.add((Interactable) widget);
            }
        });
        return interactables;
    }

    public ISyncedWidget findSyncedWidget(int id) {
        return syncedWidgets.get(id);
    }

    public int findIdForSynced(ISyncedWidget syncedWidget) {
        return syncedWidgets.inverse().get(syncedWidget);
    }

    public void render(MatrixStack matrices, Point mousePos, float delta) {
        rootWidget.render(matrices, mousePos, delta);
    }

    public void close() {

    }

    public void open() {

    }

    @Override
    public Size getScreenSize() {
        return screenSize;
    }

    @Override
    public Size getGuiSize() {
        return rootWidget.getSize();
    }

    @Override
    public AABB getBounds() {
        return rootWidget.getBounds();
    }

    public Point getOrigin() {
        return rootWidget.getPos();
    }
}
