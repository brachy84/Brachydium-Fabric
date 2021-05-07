package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISizeProvider;
import brachy84.brachydium.gui.api.IUIHolder;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.math.AABB;
import brachy84.brachydium.gui.math.Point;
import brachy84.brachydium.gui.widgets.SingleChildWidget;
import brachy84.brachydium.gui.widgets.Widget;
import brachy84.brachydium.gui.widgets.MultiChildWidget;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ModularGui implements ISizeProvider {

    /**
     * this map contains only the widgets in the top most layer
     */
    private final ImmutableBiMap<Integer, Widget> guiWidgets;
    private Widget backgroundWidget;

    //public final TextureArea backgroundPath;
    private int screenWidth, screenHeight;
    private final int width, height;
    private final ImmutableList<Runnable> uiOpenCallback;
    private final ImmutableList<Runnable> uiCloseCallback;

    public boolean isJEIHandled;

    /**
     * UIHolder of this modular UI
     */
    public final IUIHolder holder;
    public final PlayerEntity entityPlayer;

    public ModularGui(ImmutableBiMap<Integer, Widget> guiWidgets, ImmutableList<Runnable> openListeners, ImmutableList<Runnable> closeListeners, int width, int height, IUIHolder holder, PlayerEntity entityPlayer) {
        this.guiWidgets = guiWidgets;
        this.uiOpenCallback = openListeners;
        this.uiCloseCallback = closeListeners;
        this.width = width;
        this.height = height;
        this.holder = holder;
        this.entityPlayer = entityPlayer;
    }

    public List<Widget> getFlatVisibleWidgetCollection() {
        List<Widget> widgetList = new ArrayList<>(guiWidgets.size());

        for (Widget widget : guiWidgets.values()) {
            widgetList.add(widget);

            if (widget instanceof MultiChildWidget)
                widgetList.addAll(((MultiChildWidget) widget).getChildren());
        }

        return widgetList;
    }

    public void updateScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        Point displayOffset = getBounds().getTopLeft();
        guiWidgets.values().forEach(widget -> widget.setParentPosition(displayOffset));
    }

    public void initWidgets() {
        forEachWidget(widget -> {
            widget.setGui(this);
            //widget.setSizes(this);
            widget.onInit();
        });
        initLayers(guiWidgets.values(), 0);
    }

    public void triggerOpenListeners() {
        uiOpenCallback.forEach(Runnable::run);
    }

    public void triggerCloseListeners() {
        uiCloseCallback.forEach(Runnable::run);
    }

    public static Builder defaultBuilder() {
        return new Builder(176, 166);
    }
/*
    public static Builder borderedBuilder() {
        return new Builder(GuiTextures.BORDERED_BACKGROUND, 195, 136);
    }

    public static Builder extendedBuilder() {
        return new Builder(GuiTextures.BACKGROUND, 176, 216);
    }

    public static Builder builder(TextureArea background, int width, int height) {
        return new Builder(background, width, height);
    }*/

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public AABB getBounds() {
        return null;
    }

    public void forEachWidget(Consumer<Widget> consumer) {
        getFlatVisibleWidgetCollection().forEach(consumer);
    }

    private void initLayers(Collection<Widget> widgets, int startingLayer) {
        int layer = startingLayer;
        for(Widget widget : widgets) {
            widget.setLayer(layer++);
            if(widget instanceof SingleChildWidget) {
                initLayers(((SingleChildWidget) widget).getChildren(), (int) (Math.ceil(layer / 100D) * 100));
            }
        }
    }

    public Widget getBackgroundWidget() {
        return backgroundWidget;
    }

    public void setBackgroundWidget(Widget backgroundWidget) {
        this.backgroundWidget = backgroundWidget;
    }

    /**
     * Simple builder for  ModularUI objects
     */
    public static class Builder {

        private ImmutableBiMap.Builder<Integer, Widget> widgets = ImmutableBiMap.builder();
        private ImmutableList.Builder<Runnable> openListeners = ImmutableList.builder();
        private ImmutableList.Builder<Runnable> closeListeners = ImmutableList.builder();
        //private TextureArea background;
        private int width, height;
        private int nextFreeWidgetId = 0;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /*public Builder(TextureArea background, int width, int height) {
            Preconditions.checkNotNull(background);
            this.background = background;
            this.width = width;
            this.height = height;
        }*/

        public Builder widget(Widget widget) {
            Preconditions.checkNotNull(widget);
            widgets.put(nextFreeWidgetId++, widget);
            return this;
        }

        /*public Builder label(int x, int y, String localizationKey) {
            return widget(new LabelWidget(x, y, localizationKey));
        }

        public Builder label(int x, int y, String localizationKey, int color) {
            return widget(new LabelWidget(x, y, localizationKey, color, new Object[0]));
        }

        public Builder image(int x, int y, int width, int height, TextureArea area) {
            return widget(new ImageWidget(x, y, width, height, area));
        }

        public Builder dynamicLabel(int x, int y, Supplier<String> text, int color) {
            return widget(new DynamicLabelWidget(x, y, text, color));
        }

        public Builder slot(IItemHandlerModifiable itemHandler, int slotIndex, int x, int y, TextureArea... overlays) {
            return widget(new SlotWidget(itemHandler, slotIndex, x, y).setBackgroundTexture(overlays));
        }

        public Builder progressBar(DoubleSupplier progressSupplier, int x, int y, int width, int height, TextureArea texture, MoveType moveType) {
            return widget(new ProgressWidget(progressSupplier, x, y, width, height, texture, moveType));
        }

        public Builder bindPlayerInventory(InventoryPlayer inventoryPlayer) {
            bindPlayerInventory(inventoryPlayer, GuiTextures.SLOT);
            return this;
        }

        public Builder bindPlayerInventory(InventoryPlayer inventoryPlayer, int startY) {
            bindPlayerInventory(inventoryPlayer, GuiTextures.SLOT, 8, startY);
            return this;
        }

        public Builder bindPlayerInventory(InventoryPlayer inventoryPlayer, TextureArea imageLocation) {
            return bindPlayerInventory(inventoryPlayer, imageLocation, 8, 84);
        }

        public Builder bindPlayerInventory(InventoryPlayer inventoryPlayer, TextureArea imageLocation, int x, int y) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.widget(new SlotWidget(new PlayerMainInvWrapper(inventoryPlayer), col + (row + 1) * 9, x + col * 18, y + row * 18)
                            .setBackgroundTexture(imageLocation)
                            .setLocationInfo(true, false));
                }
            }
            return bindPlayerHotbar(inventoryPlayer, imageLocation, x, y + 58);
        }

        public Builder bindPlayerHotbar(InventoryPlayer inventoryPlayer, TextureArea imageLocation, int x, int y) {
            for (int slot = 0; slot < 9; slot++) {
                this.widget(new SlotWidget(new PlayerMainInvWrapper(inventoryPlayer), slot, x + slot * 18, y)
                        .setBackgroundTexture(imageLocation)
                        .setLocationInfo(true, true));
            }
            return this;
        }*/

        public Builder bindOpenListener(Runnable onContainerOpen) {
            this.openListeners.add(onContainerOpen);
            return this;
        }

        public Builder bindCloseListener(Runnable onContainerClose) {
            this.closeListeners.add(onContainerClose);
            return this;
        }

        public ModularGui build(IUIHolder holder, PlayerEntity player) {
            return new ModularGui(widgets.build(), openListeners.build(), closeListeners.build(), width, height, holder, player);
        }
    }
}
