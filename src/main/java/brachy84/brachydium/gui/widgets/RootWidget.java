package brachy84.brachydium.gui.widgets;

import brachy84.brachydium.gui.Sprites;
import brachy84.brachydium.gui.api.ISprite;
import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.math.Point;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class RootWidget extends ParentWidget {

    private RootWidget(BackgroundWidget background, BiMap<Integer, Widget> children) {
        super(background, children);
        this.layer = 1;
    }

    public static Builder builder() {
        return builder(new BackgroundWidget(Sprites.BACKGROUND));
    }

    @Override
    public void draw(MatrixStack matrices, Point mousePos, float delta) {
        super.draw(matrices, mousePos, delta);
        background.render(matrices, mousePos, delta);
    }

    public static Builder builder(BackgroundWidget widget) {
        return new Builder(widget);
    }

    public static class Builder {
        private final Map<Integer, Widget> children = new HashMap<>();
        protected final BackgroundWidget background;
        private int nextId = 0;

        private Builder(BackgroundWidget background) {
            this.background = background;
            widget(background);
        }

        public Builder widget(Widget widget) {
            Preconditions.checkNotNull(widget);
            children.put(nextId++, widget);
            return this;
        }

        /*public MainWidget label(int x, int y, String localizationKey) {
        return widget(new LabelWidget(x, y, localizationKey));
        }

        public MainWidget label(int x, int y, String localizationKey, int color) {
            return widget(new LabelWidget(x, y, localizationKey, color, new Object[0]));
        }

        public MainWidget image(int x, int y, int width, int height, TextureArea area) {
            return widget(new ImageWidget(x, y, width, height, area));
        }

        public MainWidget dynamicLabel(int x, int y, Supplier<String> text, int color) {
            return widget(new DynamicLabelWidget(x, y, text, color));
        }
    */
        public Builder slot(Inventory inventory, int slotIndex, Point point, ISprite... overlays) {
            return widget(new ItemSlotWidget(inventory, slotIndex, point).setBackgroundSprites(overlays));
        }
/*
        public MainWidget progressBar(DoubleSupplier progressSupplier, int x, int y, int width, int height, TextureArea texture, MoveType moveType) {
            return widget(new ProgressWidget(progressSupplier, x, y, width, height, texture, moveType));
        }
*/

        public Builder bindPlayerInventory(PlayerInventory inventoryPlayer, float startY) {
            return bindPlayerInventory(inventoryPlayer, new Point(7, startY));
        }

        public Builder bindPlayerInventory(PlayerInventory inventoryPlayer) {
            return bindPlayerInventory(inventoryPlayer, new Point(7, 84));
        }

        public Builder bindPlayerInventory(PlayerInventory inventoryPlayer, Point point) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    slot(inventoryPlayer, col + (row + 1) * 9, point);
                    point.translate(18, 0); // move point 18 pixels to the right
                    /*this.widget(new SlotWidget(new PlayerMainInvWrapper(inventoryPlayer), col + (row + 1) * 9, x + col * 18, y + row * 18)
                            .setBackgroundTexture(imageLocation)
                            .setLocationInfo(true, false));*/
                }
                point.translate(-9 * 18, 18); // move 9 slots to the left and 1 slot down
            }
            point.translate(0, 4);
            return bindPlayerHotbar(inventoryPlayer, point);
        }

        public Builder bindPlayerHotbar(PlayerInventory inventoryPlayer, Point point) {
            for (int slot = 0; slot < 9; slot++) {

                slot(inventoryPlayer, slot, point);
                point.translate(18, 0); // move point 18 pixels to the right
                /*this.widget(new SlotWidget(new PlayerMainInvWrapper(inventoryPlayer), slot, x + slot * 18, y)
                        .setBackgroundTexture(imageLocation)
                        .setLocationInfo(true, true));*/
            }
            return this;
        }

        /*public MainWidget bindOpenListener(Runnable onContainerOpen) {
            this.openListeners.add(onContainerOpen);
            return this;
        }

        public MainWidget bindCloseListener(Runnable onContainerClose) {
            this.closeListeners.add(onContainerClose);
            return this;
        }*/

        public RootWidget build() {
            return new RootWidget(background, HashBiMap.create(children));
        }

    }
}
